# EcoTerritorio DSS

Sistema de soporte a decisiones para consultar, comparar e interpretar indicadores ambientales (NDVI, temperatura superficial y radiación solar) de los municipios de Colombia, sin escribir código.

Proyecto de aula de **Ingeniería de Software I**, Corporación Universitaria Remington (Uniremington), sede Medellín.

## Integrantes

| Integrante | Rol en el prototipo |
|---|---|
| Almer Alejandro Sánchez Ortiz | [Completar: entidad que programa] |
| Salomé Alejandra Montoya Velásquez | [Completar: entidad que programa] |

- **Grupo:** 5
- **Docente:** Gloria Amparo Lora Patiño

## Problema y objetivo

En las sesiones de laboratorio, estudiantes y docentes de áreas ambientales pierden tiempo de clase configurando herramientas (QGIS, Google Earth Engine) en lugar de interpretar los datos del territorio.

**Objetivo general:** desarrollar la plataforma web EcoTerritorio DSS para estudiantes, docentes, investigadores y entidades ambientales de Colombia, que permita visualizar e interpretar cambios en la cobertura vegetal (NDVI), la temperatura superficial del suelo (LST) y la radiación solar regional, integrando datos satelitales abiertos, para simplificar el análisis territorial y reducir las barreras técnicas de consulta.

## ODS relacionados

- **ODS 15** (principal): Vida de ecosistemas terrestres, meta 15.1
- **ODS 13**: Acción por el clima, meta 13.3
- **ODS 7**: Energía asequible y no contaminante, meta 7.2

## Documentación

| Documento | Contenido |
|---|---|
| [Cuadrantes 1 y 2](docs/Cuadrante1y2.pdf) | Entorno, problema, hipótesis, estado del arte, marco teórico, elicitación, requisitos, casos de uso, arquitectura y validación |
| [Cuadrante 3](docs/Cuadrante3.pdf) | Metodología, alcance modificado, historias y criterios, requisitos, matriz de trazabilidad, diccionario de datos, mockups y prototipo |

Trazabilidad del cuadrante 3: 15 historias de usuario, 55 criterios de aceptación, 57 requisitos funcionales, 7 no funcionales y 20 reglas de negocio. Cada clase del código cita en su cabecera las historias, requisitos, reglas y criterios que realiza.

## Qué hace el prototipo

Programa de consola en Java con dos entidades, **Municipio** y **Consulta**, y las mismas siete operaciones en cada una:

| Operación | Descripción |
|---|---|
| Crear | Registra con validaciones (código único, datos completos, rangos) |
| Consultar por código | Muestra un registro o avisa si no existe |
| Buscar por texto | Coincidencias parciales, sin importar mayúsculas ni tildes |
| Actualizar | Cambia los datos permitidos; el código no se modifica |
| Eliminar | Quita el registro sin dejar hueco y lo guarda en una pila |
| Listar y totalizar | Lista todo y calcula promedios o totales |
| Deshacer | Recupera la última eliminación (pila) |

Los datos se guardan al salir en archivos de texto y se cargan al iniciar.

**Alcance de este corte:** el prototipo registra los valores de NDVI, LST y radiación como datos de cada consulta. La conexión en vivo con Google Earth Engine y NASA POWER y la versión para celular quedan para el semestre siguiente.

## Estructura del repositorio

```
EcoTerritorio-DSS
├── README.md
├── docs
│   ├── Cuadrante1y2.pdf
│   └── Cuadrante3.pdf
├── diagrams
│   ├── CasosDeUso.png
│   ├── UML.png
│   └── Arquitectura.png
├── datos_ejemplo
│   ├── municipios.txt
│   └── consultas.txt
└── src
    ├── Main.java
    ├── Municipio.java
    ├── PilaMunicipio.java
    ├── ListaMunicipios.java
    ├── Consulta.java
    ├── PilaConsulta.java
    └── ListaConsultas.java
```

## Cómo compilar y ejecutar

Requiere una JVM de Java 17 o superior.

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

Los archivos `municipios.txt` y `consultas.txt` se crean en la carpeta desde la que se ejecuta el programa, al salir con la opción `0`.

## Datos de ejemplo

La carpeta `datos_ejemplo` trae 13 municipios de Antioquia y 13 consultas con **valores de ejemplo, no son mediciones reales**. Para probarlos, copie los dos archivos a la carpeta desde la que ejecuta el programa.

## Estructuras de datos

Arreglo de tamaño fijo con contador y pila escrita a mano sobre arreglo. No se usa `ArrayList`, `LinkedList` ni `java.util.Stack`.
