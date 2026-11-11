/* ===========================================================
 * Proyecto       : EcoTerritorio DSS
 * Entidad        : Municipio
 * Programado por : <nombre y apellido>
 * Fecha          : <dd/mm/aaaa>
 * -----------------------------------------------------------
 * HU-07  Como administrador del catalogo quiero registrar los
 *        municipios con su ubicacion para que las consultas se
 *        hagan solo sobre municipios validos
 * RF-19  Registrar municipio con codigo, nombre, departamento
 *        y coordenadas
 * RN-01  Toda consulta se hace sobre un municipio de Colombia
 * -----------------------------------------------------------
 * Criterios: CA-07.1 (bueno)
 * Estructuras: registro simple (los campos son los del diccionario)
 * =========================================================== */
public class Municipio {
    public String codigo_municipio;
    public String nombre_municipio;
    public String departamento;
    public double latitud;
    public double longitud;

    public Municipio(String codigo_municipio, String nombre_municipio,
                     String departamento, double latitud, double longitud) {
        this.codigo_municipio = codigo_municipio;
        this.nombre_municipio = nombre_municipio;
        this.departamento = departamento;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // Una linea del archivo de texto: campos separados por punto y coma
    public String aLinea() {
        return codigo_municipio + ";" + nombre_municipio + ";" + departamento
                + ";" + latitud + ";" + longitud;
    }

    // Reconstruye un municipio desde una linea; null si la linea esta danada
    public static Municipio desdeLinea(String linea) {
        String[] p = linea.split(";");
        if (p.length != 5) return null;
        try {
            return new Municipio(p[0], p[1], p[2],
                    Double.parseDouble(p[3]), Double.parseDouble(p[4]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String toString() {
        return String.format("%-6s %-25s %-15s %9.4f %10.4f",
                codigo_municipio, nombre_municipio, departamento, latitud, longitud);
    }
}
