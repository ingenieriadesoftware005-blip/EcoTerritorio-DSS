/* ===========================================================
 * Proyecto       : EcoTerritorio DSS
 * Entidad        : Consulta
 * Programado por : <nombre y apellido>
 * Fecha          : <dd/mm/aaaa>
 * -----------------------------------------------------------
 * HU-05  Como docente quiero guardar el resultado de una
 *        consulta para compararlo sin repetirla
 * RF-14  Guardar consulta con municipio, indicador, periodo y valor
 * -----------------------------------------------------------
 * Criterios: CA-05.1 (bueno)
 * Estructuras: registro simple (los campos son los del diccionario)
 * =========================================================== */
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Consulta {
    public String codigo_consulta;
    public String codigo_municipio;
    public String indicador;          // NDVI, LST o RADIACION
    public LocalDate fecha_inicio;
    public LocalDate fecha_fin;
    public double valor_promedio;
    public double nubosidad_pct;

    public Consulta(String codigo_consulta, String codigo_municipio, String indicador,
                    LocalDate fecha_inicio, LocalDate fecha_fin,
                    double valor_promedio, double nubosidad_pct) {
        this.codigo_consulta = codigo_consulta;
        this.codigo_municipio = codigo_municipio;
        this.indicador = indicador;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.valor_promedio = valor_promedio;
        this.nubosidad_pct = nubosidad_pct;
    }

    public String aLinea() {
        return codigo_consulta + ";" + codigo_municipio + ";" + indicador + ";"
                + fecha_inicio + ";" + fecha_fin + ";" + valor_promedio + ";" + nubosidad_pct;
    }

    public static Consulta desdeLinea(String linea) {
        String[] p = linea.split(";");
        if (p.length != 7) return null;
        try {
            return new Consulta(p[0], p[1], p[2], LocalDate.parse(p[3]), LocalDate.parse(p[4]),
                    Double.parseDouble(p[5]), Double.parseDouble(p[6]));
        } catch (NumberFormatException e) {
            return null;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public String toString() {
        return String.format("%-7s %-6s %-10s %s a %s  valor=%8.3f  nubes=%5.1f%%",
                codigo_consulta, codigo_municipio, indicador,
                fecha_inicio, fecha_fin, valor_promedio, nubosidad_pct);
    }
}
