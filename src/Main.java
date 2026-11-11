/* ===========================================================
 * Proyecto       : EcoTerritorio DSS
 * Entidad        : Menu unico (Municipio y Consulta)
 * Programado por : <nombre y apellido>
 * Fecha          : <dd/mm/aaaa>
 * -----------------------------------------------------------
 * HU-05, HU-06, HU-07 (ver ListaMunicipios y ListaConsultas)
 * RF-14 a RF-21
 * RF-01  El municipio se escoge escribiendo su nombre, no su codigo
 * -----------------------------------------------------------
 * Estructuras: usa ListaMunicipios y ListaConsultas
 * =========================================================== */
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static final String ARCH_MUN = "datos/municipios.txt";
    static final String ARCH_CON = "datos/consultas.txt";

    static ListaMunicipios municipios = new ListaMunicipios();
    static ListaConsultas consultas = new ListaConsultas(municipios);

    public static void main(String[] args) {
        int m = municipios.cargar(ARCH_MUN);      // primero municipios...
        int c = consultas.cargar(ARCH_CON);       // ...porque la consulta los valida
        System.out.println("EcoTerritorio DSS - cargados " + m + " municipios y " + c + " consultas");

        String op = "";
        while (!op.equals("0")) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Municipios");
            System.out.println("2. Consultas");
            System.out.println("0. Guardar y salir");
            op = leer("Opcion: ");
            if (op.equals("1")) menuMunicipios();
            else if (op.equals("2")) menuConsultas();
        }
        mostrar(municipios.guardar(ARCH_MUN), "Municipios guardados");
        mostrar(consultas.guardar(ARCH_CON), "Consultas guardadas");
    }

    // ---------- Municipios ----------
    static void menuMunicipios() {
        String op = "";
        while (!op.equals("0")) {
            System.out.println("\n--- MUNICIPIOS ---");
            System.out.println("1. Crear   2. Consultar por codigo   3. Buscar por texto");
            System.out.println("4. Actualizar   5. Eliminar   6. Listar   7. Deshacer   0. Volver");
            op = leer("Opcion: ");
            if (op.equals("1")) {
                String cod = leer("codigo_municipio (5 caracteres): ");
                String nom = leer("nombre_municipio: ");
                String dep = leer("departamento: ");
                double lat = leerDecimal("latitud: ");
                double lon = leerDecimal("longitud: ");
                if (Double.isNaN(lat) || Double.isNaN(lon)) continue;
                mostrar(municipios.crear(new Municipio(cod, nom, dep, lat, lon)), "Municipio creado");
            } else if (op.equals("2")) {
                Municipio x = municipios.buscarPorCodigo(leer("codigo_municipio: "));
                System.out.println(x == null ? "No existe un municipio con ese codigo" : x.toString());
            } else if (op.equals("3")) {
                Municipio[] r = municipios.buscarPorTexto(leer("Texto: "));
                System.out.println(r.length + " resultado(s)");
                for (int i = 0; i < r.length; i++) System.out.println(r[i]);
            } else if (op.equals("4")) {
                String cod = leer("codigo_municipio a actualizar: ");
                String nom = leer("nuevo nombre_municipio: ");
                String dep = leer("nuevo departamento: ");
                double lat = leerDecimal("nueva latitud: ");
                double lon = leerDecimal("nueva longitud: ");
                if (Double.isNaN(lat) || Double.isNaN(lon)) continue;
                mostrar(municipios.actualizar(cod, nom, dep, lat, lon), "Municipio actualizado");
            } else if (op.equals("5")) {
                mostrar(municipios.eliminar(leer("codigo_municipio a eliminar: ")), "Municipio eliminado");
            } else if (op.equals("6")) {
                municipios.listar();
                System.out.println("Total: " + municipios.getContador());
                String d = leer("Contar municipios de un departamento (Enter para omitir): ");
                if (!d.isEmpty()) System.out.println(d + ": " + municipios.contarPorDepartamento(d));
            } else if (op.equals("7")) {
                mostrar(municipios.deshacer(), "Eliminacion deshecha");
            }
        }
    }

    // ---------- Consultas ----------
    static void menuConsultas() {
        String op = "";
        while (!op.equals("0")) {
            System.out.println("\n--- CONSULTAS ---");
            System.out.println("1. Crear   2. Consultar por codigo   3. Buscar por texto");
            System.out.println("4. Actualizar   5. Eliminar   6. Listar   7. Deshacer   8. Resumen de un municipio   0. Volver");
            op = leer("Opcion: ");
            if (op.equals("1")) {
                String cod = leer("codigo_consulta (6 caracteres): ");
                Municipio mu = pedirMunicipio("Municipio (nombre): ");   // RF-01
                if (mu == null) continue;
                String mun = mu.codigo_municipio;      // el codigo DANE queda como llave interna
                String ind = leer("indicador (NDVI, LST o RADIACION): ").toUpperCase();
                LocalDate ini = leerFecha("fecha_inicio (aaaa-mm-dd): ");
                LocalDate fin = leerFecha("fecha_fin (aaaa-mm-dd): ");
                double val = leerDecimal("valor_promedio: ");
                double nub = leerDecimal("nubosidad_pct: ");
                if (ini == null || fin == null || Double.isNaN(val) || Double.isNaN(nub)) continue;
                mostrar(consultas.crear(new Consulta(cod, mun, ind, ini, fin, val, nub)), "Consulta creada");
            } else if (op.equals("2")) {
                Consulta x = consultas.buscarPorCodigo(leer("codigo_consulta: "));
                System.out.println(x == null ? "No existe una consulta con ese codigo" : x.toString());
            } else if (op.equals("3")) {
                Consulta[] r = consultas.buscarPorTexto(leer("Texto: "));
                System.out.println(r.length + " resultado(s)");
                for (int i = 0; i < r.length; i++) System.out.println(r[i]);
            } else if (op.equals("4")) {
                String cod = leer("codigo_consulta a actualizar: ");
                LocalDate ini = leerFecha("nueva fecha_inicio (aaaa-mm-dd): ");
                LocalDate fin = leerFecha("nueva fecha_fin (aaaa-mm-dd): ");
                double val = leerDecimal("nuevo valor_promedio: ");
                double nub = leerDecimal("nueva nubosidad_pct: ");
                if (ini == null || fin == null || Double.isNaN(val) || Double.isNaN(nub)) continue;
                mostrar(consultas.actualizar(cod, ini, fin, val, nub), "Consulta actualizada");
            } else if (op.equals("5")) {
                mostrar(consultas.eliminar(leer("codigo_consulta a eliminar: ")), "Consulta eliminada");
            } else if (op.equals("6")) {
                consultas.listar();
                System.out.println("Total: " + consultas.getContador());
                String[] inds = {"NDVI", "LST", "RADIACION"};
                for (int i = 0; i < inds.length; i++) {
                    double p = consultas.promedioPorIndicador(inds[i]);
                    System.out.println("Promedio " + inds[i] + ": "
                            + (Double.isNaN(p) ? "sin datos" : String.format("%.3f", p)));
                }
            } else if (op.equals("7")) {
                mostrar(consultas.deshacer(), "Eliminacion deshecha");
            } else if (op.equals("8")) {
                resumenMunicipio();
            }
        }
    }

    // RF-22 . CA-08.1 / CA-08.2 / CA-08.3 — que tan verde y que temperatura tiene un municipio
    static void resumenMunicipio() {
        Municipio m = pedirMunicipio("Municipio (nombre): ");      // CA-08.2 si no existe
        if (m == null) return;
        String cod = m.codigo_municipio;
        double ndvi = consultas.promedioDeMunicipio(cod, "NDVI");
        double lst = consultas.promedioDeMunicipio(cod, "LST");
        double rad = consultas.promedioDeMunicipio(cod, "RADIACION");
        if (Double.isNaN(ndvi) && Double.isNaN(lst) && Double.isNaN(rad)) {   // CA-08.3
            System.out.println("AVISO: No hay consultas guardadas para " + m.nombre_municipio);
            return;
        }
        System.out.println("Resumen de " + m.nombre_municipio + " (" + m.departamento + ")");
        if (Double.isNaN(ndvi)) System.out.println("  Vegetacion (NDVI): sin datos");
        else System.out.println("  Vegetacion (NDVI): " + String.format("%.2f", ndvi)
                + " -> " + ListaConsultas.interpretarNDVI(ndvi));
        if (Double.isNaN(lst)) System.out.println("  Temperatura promedio: sin datos");
        else System.out.println("  Temperatura promedio: " + String.format("%.1f", lst) + " C");
        if (Double.isNaN(rad)) System.out.println("  Radiacion solar: sin datos");
        else System.out.println("  Radiacion solar: " + String.format("%.2f", rad) + " kWh/m2/dia");
    }

    // RF-01 . CA-01.1 / CA-01.3 — pide el municipio por nombre y devuelve su registro
    // Devuelve null (y avisa) si no existe; si hay homonimos, pide el departamento
    static Municipio pedirMunicipio(String pregunta) {
        String nombre = leer(pregunta);
        if (nombre.isEmpty()) {
            System.out.println("AVISO: Escriba el nombre de un municipio");
            return null;
        }
        Municipio[] r = municipios.buscarPorNombre(nombre);
        if (r.length == 0) {
            System.out.println("AVISO: No hay un municipio llamado " + nombre + " en el catalogo");
            Municipio[] parecidos = municipios.buscarPorTexto(nombre);
            if (parecidos.length > 0) {
                System.out.println("Quiso decir:");
                for (int i = 0; i < parecidos.length; i++)
                    System.out.println("  - " + parecidos[i].nombre_municipio
                            + " (" + parecidos[i].departamento + ")");
            }
            return null;
        }
        if (r.length == 1) return r[0];
        System.out.println("Hay " + r.length + " municipios con ese nombre:");
        for (int i = 0; i < r.length; i++)
            System.out.println("  - " + r[i].nombre_municipio + " (" + r[i].departamento + ")");
        String dep = ListaMunicipios.normalizar(leer("Departamento: "));
        for (int i = 0; i < r.length; i++) {
            if (ListaMunicipios.normalizar(r[i].departamento).equals(dep)) return r[i];
        }
        System.out.println("AVISO: Ningun municipio con ese nombre esta en ese departamento");
        return null;
    }

    // ---------- Ayudas de entrada y salida ----------
    // Si el resultado es null todo salio bien; si no, es el mensaje de falla
    static void mostrar(String resultado, String mensajeOk) {
        if (resultado == null) System.out.println("OK: " + mensajeOk);
        else System.out.println("AVISO: " + resultado);
    }

    static String leer(String pregunta) {
        System.out.print(pregunta);
        if (!sc.hasNextLine()) return "0";       // fin de la entrada: salir
        return sc.nextLine().trim();
    }

    static double leerDecimal(String pregunta) {
        String s = leer(pregunta).replace(',', '.');
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            System.out.println("AVISO: valor numerico invalido");
            return Double.NaN;
        }
    }

    static LocalDate leerFecha(String pregunta) {
        try {
            return LocalDate.parse(leer(pregunta));
        } catch (DateTimeParseException e) {
            System.out.println("AVISO: fecha invalida, use aaaa-mm-dd");
            return null;
        }
    }
}
