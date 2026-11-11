/* ===========================================================
 * Proyecto       : EcoTerritorio DSS
 * Entidad        : Consulta
 * Programado por : <nombre y apellido>
 * Fecha          : <dd/mm/aaaa>
 * -----------------------------------------------------------
 * HU-05  Como docente quiero guardar el resultado de una
 *        consulta para compararlo sin repetirla
 * HU-06  Deshacer la ultima eliminacion de un registro
 * RF-14  Guardar consulta con municipio, indicador, periodo y valor
 * RF-15  Rechazar si el codigo de la consulta ya existe
 * RF-16  Rechazar si el municipio no esta en el catalogo
 * RF-17  Deshacer la ultima eliminacion
 * RF-18  Avisar cuando no hay eliminaciones por deshacer
 * RF-22  Mostrar el resumen de un municipio (promedios y NDVI interpretado)
 * RN-01  Toda consulta se hace sobre un municipio de Colombia
 * RN-04  Temperatura en grados C y radiacion en kWh/m2/dia
 * -----------------------------------------------------------
 * Criterios: CA-05.1 (bueno) CA-05.2 (falla) CA-05.3 (falla)
 *            CA-06.1 (bueno) CA-06.2 (falla)
 *            CA-08.1 (bueno) CA-08.2 (falla) CA-08.3 (falla)
 * Estructuras: Arreglo con contador . Pila propia
 * =========================================================== */
import java.io.*;
import java.time.LocalDate;

public class ListaConsultas {
    public static final int CAPACIDAD = 200;

    private Consulta[] datos = new Consulta[CAPACIDAD];
    private int contador = 0;
    private PilaConsulta eliminados = new PilaConsulta(CAPACIDAD);
    private ListaMunicipios catalogo;      // punto de contacto: valida contra Municipio

    public ListaConsultas(ListaMunicipios catalogo) {
        this.catalogo = catalogo;
    }

    public int getContador() { return contador; }

    private int posicionDe(String codigo_consulta) {
        for (int i = 0; i < contador; i++) {
            if (datos[i].codigo_consulta.equals(codigo_consulta)) return i;
        }
        return -1;
    }

    public Consulta buscarPorCodigo(String codigo_consulta) {
        int i = posicionDe(codigo_consulta);
        if (i == -1) return null;
        return datos[i];
    }

    // Campos que no dependen de otra entidad; devuelve el motivo o null
    private String validarCampos(Consulta c) {
        if (c.codigo_consulta == null || c.codigo_consulta.length() != 6)
            return "El codigo de la consulta debe tener 6 caracteres";
        if (c.codigo_consulta.contains(";"))
            return "Los textos no pueden llevar punto y coma";
        if (!(c.indicador.equals("NDVI") || c.indicador.equals("LST")
                || c.indicador.equals("RADIACION")))
            return "El indicador debe ser NDVI, LST o RADIACION";
        if (c.fecha_inicio == null || c.fecha_fin == null)
            return "Las fechas son obligatorias";
        if (c.fecha_fin.isBefore(c.fecha_inicio))
            return "La fecha final no puede ser anterior a la inicial";
        if (c.nubosidad_pct < 0 || c.nubosidad_pct > 100)
            return "La nubosidad debe estar entre 0 y 100";
        if (c.indicador.equals("NDVI") && (c.valor_promedio < -1 || c.valor_promedio > 1))
            return "El NDVI debe estar entre -1 y 1";
        if (c.indicador.equals("RADIACION") && c.valor_promedio < 0)
            return "La radiacion no puede ser negativa";
        return null;
    }

    // RF-14 . CA-05.1 — crea
    // RF-15 . CA-05.2 — rechaza codigo repetido
    // RF-16 . CA-05.3 — rechaza si el municipio no esta en el catalogo
    public String crear(Consulta c) {
        if (c.codigo_consulta != null && posicionDe(c.codigo_consulta) != -1)
            return "El codigo de consulta esta repetido";
        if (catalogo.buscarPorCodigo(c.codigo_municipio) == null)
            return "El municipio " + c.codigo_municipio + " no existe en el catalogo";
        String error = validarCampos(c);
        if (error != null) return error;
        if (contador == CAPACIDAD) return "El listado de consultas esta lleno";
        datos[contador] = c;
        contador++;
        return null;
    }

    // Busca por pedazo de texto en codigo de consulta, de municipio o indicador
    public Consulta[] buscarPorTexto(String texto) {
        String t = texto.toLowerCase();
        int cuantos = 0;
        for (int i = 0; i < contador; i++) {
            if (coincide(datos[i], t)) cuantos++;
        }
        Consulta[] resultado = new Consulta[cuantos];
        int k = 0;
        for (int i = 0; i < contador; i++) {
            if (coincide(datos[i], t)) { resultado[k] = datos[i]; k++; }
        }
        return resultado;
    }

    private boolean coincide(Consulta c, String t) {
        return c.codigo_consulta.toLowerCase().contains(t)
            || c.codigo_municipio.toLowerCase().contains(t)
            || c.indicador.toLowerCase().contains(t);
    }

    // Se pueden cambiar el periodo, el valor y la nubosidad; municipio e indicador no
    public String actualizar(String codigo, LocalDate ini, LocalDate fin,
                             double valor, double nubosidad) {
        int pos = posicionDe(codigo);
        if (pos == -1) return "No existe una consulta con ese codigo";
        Consulta prueba = new Consulta(codigo, datos[pos].codigo_municipio,
                datos[pos].indicador, ini, fin, valor, nubosidad);
        String error = validarCampos(prueba);
        if (error != null) return error;
        datos[pos].fecha_inicio = ini;
        datos[pos].fecha_fin = fin;
        datos[pos].valor_promedio = valor;
        datos[pos].nubosidad_pct = nubosidad;
        return null;
    }

    // RF-17 — elimina desplazando a la izquierda y apila para poder deshacer
    public String eliminar(String codigo) {
        int pos = posicionDe(codigo);
        if (pos == -1) return "No existe una consulta con ese codigo";
        if (!eliminados.apilar(datos[pos])) return "La pila de eliminaciones esta llena";
        for (int i = pos; i < contador - 1; i++) {
            datos[i] = datos[i + 1];
        }
        datos[contador - 1] = null;
        contador--;
        return null;
    }

    // RF-17 . CA-06.1 — devuelve la ultima eliminada; RF-18 . CA-06.2 — avisa si no hay
    public String deshacer() {
        Consulta c = eliminados.cima();
        if (c == null) return "No hay nada que deshacer";
        if (posicionDe(c.codigo_consulta) != -1)
            return "No se puede deshacer: ya existe una consulta con el codigo " + c.codigo_consulta;
        if (catalogo.buscarPorCodigo(c.codigo_municipio) == null)
            return "No se puede deshacer: el municipio " + c.codigo_municipio + " ya no esta en el catalogo";
        if (contador == CAPACIDAD) return "El listado de consultas esta lleno";
        eliminados.desapilar();
        datos[contador] = c;
        contador++;
        return null;
    }

    public void listar() {
        if (contador == 0) { System.out.println("(no hay consultas)"); return; }
        for (int i = 0; i < contador; i++) {
            System.out.println((i + 1) + ". " + datos[i]);
        }
    }

    // Calculo del listado: promedio de valor_promedio de un indicador; NaN si no hay
    public double promedioPorIndicador(String indicador) {
        double suma = 0;
        int cuantas = 0;
        for (int i = 0; i < contador; i++) {
            if (datos[i].indicador.equals(indicador)) {
                suma += datos[i].valor_promedio;
                cuantas++;
            }
        }
        if (cuantas == 0) return Double.NaN;
        return suma / cuantas;
    }

    // RF-22 . CA-08.1 — promedio de un indicador solo para un municipio; NaN si no hay datos
    public double promedioDeMunicipio(String codigo_municipio, String indicador) {
        double suma = 0;
        int cuantas = 0;
        for (int i = 0; i < contador; i++) {
            if (datos[i].codigo_municipio.equals(codigo_municipio)
                    && datos[i].indicador.equals(indicador)) {
                suma += datos[i].valor_promedio;
                cuantas++;
            }
        }
        if (cuantas == 0) return Double.NaN;
        return suma / cuantas;
    }

    // RF-22 . CA-08.1 — traduce el NDVI a una frase que se entienda
    // Rangos aproximados de uso comun; confirmar con la fuente que citen en el documento
    public static String interpretarNDVI(double ndvi) {
        if (ndvi < 0)   return "agua, nubes o zona sin vegetacion";
        if (ndvi < 0.2) return "suelo desnudo o vegetacion muy escasa";
        if (ndvi < 0.4) return "vegetacion escasa (pastos o rastrojo)";
        if (ndvi < 0.6) return "vegetacion moderada";
        return "vegetacion densa y saludable";
    }

    // Guardar al salir (RNF-05)
    public String guardar(String ruta) {
        try {
            File f = new File(ruta);
            if (f.getParentFile() != null) f.getParentFile().mkdirs();
            PrintWriter out = new PrintWriter(new FileWriter(f));
            for (int i = 0; i < contador; i++) out.println(datos[i].aLinea());
            out.close();
            return null;
        } catch (IOException e) {
            return "No se pudo guardar: " + e.getMessage();
        }
    }

    // Cargar al iniciar (los municipios ya deben estar cargados)
    public int cargar(String ruta) {
        File f = new File(ruta);
        if (!f.exists()) return 0;
        int cargadas = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
            String linea;
            while ((linea = in.readLine()) != null) {
                Consulta c = Consulta.desdeLinea(linea);
                if (c != null && crear(c) == null) cargadas++;
            }
            in.close();
        } catch (IOException e) {
            System.out.println("No se pudo leer " + ruta + ": " + e.getMessage());
        }
        return cargadas;
    }
}
