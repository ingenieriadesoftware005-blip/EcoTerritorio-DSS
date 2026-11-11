# EcoTerritorio DSS — Prototipo en Java (Ingeniería de Software I, tercer cuadrante)

Grupo 5. Integrantes: Almer Alejandro Sánchez Ortiz y Salomé Alejandra Montoya Velásquez.
Documento Word con las secciones 1 a 10: <enlace o nombre del archivo>

## Reparto
| Integrante | Entidad | Archivos |
|---|---|---|
| <Estudiante A> | Municipio | Municipio.java, PilaMunicipio.java, ListaMunicipios.java |
| <Estudiante B> | Consulta | Consulta.java, PilaConsulta.java, ListaConsultas.java |
| Ambos | Menú único | Main.java |

Punto de contacto: `ListaConsultas` valida el municipio llamando a `ListaMunicipios.buscarPorCodigo`.

## Cómo compilar y ejecutar
```
javac -d out src/*.java
java -cp out Main
```
Los datos se guardan al salir en `datos/municipios.txt` y `datos/consultas.txt` y se cargan al iniciar.

## Estructuras
Arreglo de tamaño fijo con contador y pila escrita a mano sobre arreglo. No se usa `ArrayList`, `LinkedList` ni `java.util.Stack`.
