/* ===========================================================
 * Proyecto       : EcoTerritorio DSS
 * Entidad        : Municipio (pila de eliminados)
 * Programado por : <nombre y apellido>
 * Fecha          : <dd/mm/aaaa>
 * -----------------------------------------------------------
 * HU-06  Como docente quiero deshacer la ultima eliminacion de
 *        un registro para recuperarlo sin volver a digitarlo
 * RF-17  Deshacer la ultima eliminacion
 * RF-18  Avisar cuando no hay eliminaciones por deshacer
 * -----------------------------------------------------------
 * Criterios: CA-06.1 (bueno) CA-06.2 (falla)
 * Estructuras: Pila propia sobre arreglo (sin java.util.Stack)
 * =========================================================== */
public class PilaMunicipio {
    private Municipio[] datos;
    private int posCima;   // indice del ultimo apilado; -1 si esta vacia

    public PilaMunicipio(int capacidad) {
        datos = new Municipio[capacidad];
        posCima = -1;
    }

    // RF-17 . CA-06.1 — guarda un registro eliminado; false si esta llena
    public boolean apilar(Municipio m) {
        if (posCima == datos.length - 1) return false;
        posCima++;
        datos[posCima] = m;
        return true;
    }

    // RF-17 — saca el ultimo apilado; RF-18 . CA-06.2 — null si esta vacia
    public Municipio desapilar() {
        if (estaVacia()) return null;
        Municipio m = datos[posCima];
        datos[posCima] = null;
        posCima--;
        return m;
    }

    // Mira el ultimo apilado sin sacarlo; null si esta vacia
    public Municipio cima() {
        if (estaVacia()) return null;
        return datos[posCima];
    }

    public boolean estaVacia() { return posCima == -1; }

    public int tamano() { return posCima + 1; }
}
