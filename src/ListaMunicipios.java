/* ===========================================================
 * Proyecto       : EcoTerritorio DSS
 * Entidad        : Municipio (catalogo)
 * Programado por : <nombre y apellido>
 * Fecha          : <dd/mm/aaaa>
 * -----------------------------------------------------------
 * HU-07  Como administrador del catalogo quiero registrar los
 *        municipios con su ubicacion para que las consultas se
 *        hagan solo sobre municipios validos
 * HU-06  Deshacer la ultima eliminacion de un registro
 * RF-19  Registrar municipio con codigo, nombre, departamento
 *        y coordenadas
 * RF-20  Rechazar si el codigo del municipio ya existe
 * RF-21  Rechazar si las coordenadas quedan fuera de Colombia
 * RF-17  Deshacer la ultima eliminacion
 * RF-18  Avisar cuando no hay eliminaciones por deshacer
 * RF-01  Seleccionar el municipio escribiendo su nombre (sin tildes ni mayusculas)
 * RN-01  Toda consulta se hace sobre un municipio de Colombia
 * -----------------------------------------------------------
 * Criterios: CA-07.1 (bueno) CA-07.2 (falla) CA-07.3 (falla)
 *            CA-06.1 (bueno) CA-06.2 (falla)
 * Estructuras: Arreglo con contador . Pila propia
 * =========================================================== */
import java.io.*;
import java.text.Normalizer;

public class ListaMunicipios {
    public static final int CAPACIDAD = 150;   // Antioquia tiene 125 municipios
    // Caja aproximada que encierra a Colombia (ajustar si la docente exige otra)
    private static final double LAT_MIN = -5.0,  LAT_MAX = 14.0;
    private static final double LON_MIN = -82.0, LON_MAX = -66.0;

    private Municipio[] datos = new Municipio[CAPACIDAD];
    private int contador = 0;                       // casillas ocupadas
    private PilaMunicipio eliminados = new PilaMunicipio(CAPACIDAD);

    public int getContador() { return contador; }

    // Posicion del codigo en el arreglo; -1 si no esta
    private int posicionDe(String codigo_municipio) {
        for (int i = 0; i < contador; i++) {
            if (datos[i].codigo_municipio.equals(codigo_municipio)) return i;
        }
        return -1;
    }

    // Lo usa Consulta para validar (RF-16 . CA-05.3); null si no existe
    public Municipio buscarPorCodigo(String codigo_municipio) {
        int i = posicionDe(codigo_municipio);
        if (i == -1) return null;
        return datos[i];
    }

    // RF-21 . CA-07.3 — devuelve el motivo del rechazo o null si todo esta bien
    private String validarCampos(String codigo, String nombre, String depto,
                                 double lat, double lon) {
        if (codigo == null || codigo.length() != 5)
            return "El codigo del municipio debe tener 5 caracteres";
        if (nombre == null || nombre.trim().isEmpty() || nombre.length() > 40)
            return "El nombre es obligatorio y de maximo 40 caracteres";
        if (depto == null || depto.trim().isEmpty() || depto.length() > 30)
            return "El departamento es obligatorio y de maximo 30 caracteres";
        if (codigo.contains(";") || nombre.contains(";") || depto.contains(";"))
            return "Los textos no pueden llevar punto y coma";
        if (lat < LAT_MIN || lat > LAT_MAX || lon < LON_MIN || lon > LON_MAX)
            return "La ubicacion no corresponde a Colombia";
        return null;
    }

    // RF-19 . CA-07.1 — crea; RF-20 . CA-07.2 — rechaza codigo repetido
    // Devuelve null si lo creo, o el motivo si lo rechazo
    public String crear(Municipio m) {
        if (m.codigo_municipio != null && posicionDe(m.codigo_municipio) != -1)
            return "El codigo de municipio esta repetido";
        String error = validarCampos(m.codigo_municipio, m.nombre_municipio,
                m.departamento, m.latitud, m.longitud);
        if (error != null) return error;
        if (contador == CAPACIDAD) return "El catalogo esta lleno";
        datos[contador] = m;
        contador++;
        return null;
    }

    // RF-01 — quita tildes y mayusculas para comparar nombres ("Medellín" = "medellin")
    public static String normalizar(String texto) {
        String n = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", "").toLowerCase().trim();
    }

    // RF-01 . CA-01.1 — municipios cuyo nombre coincide exacto con lo escrito.
    // Puede devolver mas de uno si hay homonimos en varios departamentos.
    public Municipio[] buscarPorNombre(String nombre) {
        String t = normalizar(nombre);
        int cuantos = 0;
        for (int i = 0; i < contador; i++) {
            if (normalizar(datos[i].nombre_municipio).equals(t)) cuantos++;
        }
        Municipio[] resultado = new Municipio[cuantos];
        int k = 0;
        for (int i = 0; i < contador; i++) {
            if (normalizar(datos[i].nombre_municipio).equals(t)) { resultado[k] = datos[i]; k++; }
        }
        return resultado;
    }

    // Busca por pedazo de texto en codigo, nombre o departamento
    public Municipio[] buscarPorTexto(String texto) {
        String t = normalizar(texto);
        int cuantos = 0;
        for (int i = 0; i < contador; i++) {
            if (coincide(datos[i], t)) cuantos++;
        }
        Municipio[] resultado = new Municipio[cuantos];
        int k = 0;
        for (int i = 0; i < contador; i++) {
            if (coincide(datos[i], t)) { resultado[k] = datos[i]; k++; }
        }
        return resultado;
    }

    private boolean coincide(Municipio m, String t) {
        return m.codigo_municipio.contains(t)
            || normalizar(m.nombre_municipio).contains(t)
            || normalizar(m.departamento).contains(t);
    }

    // RF-21 . CA-07.3 — el codigo no se cambia: es la llave
    public String actualizar(String codigo, String nombre, String depto,
                             double lat, double lon) {
        int pos = posicionDe(codigo);
        if (pos == -1) return "No existe un municipio con ese codigo";
        String error = validarCampos(codigo, nombre, depto, lat, lon);
        if (error != null) return error;
        datos[pos].nombre_municipio = nombre;
        datos[pos].departamento = depto;
        datos[pos].latitud = lat;
        datos[pos].longitud = lon;
        return null;
    }

    // RF-17 — elimina desplazando a la izquierda y apila para poder deshacer
    public String eliminar(String codigo) {
        int pos = posicionDe(codigo);
        if (pos == -1) return "No existe un municipio con ese codigo";
        if (!eliminados.apilar(datos[pos])) return "La pila de eliminaciones esta llena";
        for (int i = pos; i < contador - 1; i++) {
            datos[i] = datos[i + 1];
        }
        datos[contador - 1] = null;
        contador--;
        return null;
    }

    // RF-17 . CA-06.1 — devuelve al arreglo el ultimo eliminado (queda de ultimo)
    // RF-18 . CA-06.2 — avisa si no hay nada que deshacer
    public String deshacer() {
        Municipio m = eliminados.cima();
        if (m == null) return "No hay nada que deshacer";
        if (posicionDe(m.codigo_municipio) != -1)
            return "No se puede deshacer: ya existe un municipio con el codigo " + m.codigo_municipio;
        if (contador == CAPACIDAD) return "El catalogo esta lleno";
        eliminados.desapilar();
        datos[contador] = m;
        contador++;
        return null;
    }

    public void listar() {
        if (contador == 0) { System.out.println("(no hay municipios)"); return; }
        for (int i = 0; i < contador; i++) {
            System.out.println((i + 1) + ". " + datos[i]);
        }
    }

    // Calculo del listado. DECISION PENDIENTE: Municipio no tiene campo numerico
    // propio; mientras se acuerda con la docente, se cuentan los municipios por departamento.
    public int contarPorDepartamento(String departamento) {
        int cuantos = 0;
        for (int i = 0; i < contador; i++) {
            if (datos[i].departamento.equalsIgnoreCase(departamento)) cuantos++;
        }
        return cuantos;
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

    // Cargar al iniciar; devuelve cuantos registros cargo
    public int cargar(String ruta) {
        File f = new File(ruta);
        if (!f.exists()) return 0;
        int cargados = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
            String linea;
            while ((linea = in.readLine()) != null) {
                Municipio m = Municipio.desdeLinea(linea);
                if (m != null && crear(m) == null) cargados++;
            }
            in.close();
        } catch (IOException e) {
            System.out.println("No se pudo leer " + ruta + ": " + e.getMessage());
        }
        return cargados;
    }
}
