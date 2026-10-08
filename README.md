# Solución a Evaluación Técnica - Java Backend

Solución a tres ejercicios en Java. Los casos de prueba están en `Main.java`.

## Estructura del proyecto

```text
src/main/java/
├── Main.java            # Punto de entrada: ejecuta los casos de los 3 ejercicios
├── consolidation/       # Ejercicio 1: consolidación de movimientos por cuenta
│   ├── model/           #   InOperation, OutOperation
│   └── service/         #   ProcessorConsolidation
├── suspicious/          # Ejercicio 2: detección de operaciones sospechosas
│   ├── model/           #   InOperations, OutOperations
│   └── service/         #   SuspiciousAnalyzer
└── rules/               # Ejercicio 3: refactor de reglas de aprobación
    ├── model/           #   InOperationRules
    └── service/         #   ComplianceProcessor
```

---

## Diagrama de Arquitectura

```mermaid
graph TD
    subgraph Entrada
        A[JSON]
    end

    subgraph Core System - Java
        B[Main] --> C[Ejercicio 1: ProcessorConsolidation]
        B --> D[Ejercicio 2: SuspiciousAnalyzer]
        B --> E[Ejercicio 3: ComplianceProcessor]
    end

    subgraph Procesamiento por Ejercicio - Los casos de prueba estan dentro de un solo MAIN
        C -->|1. Evitar duplicación por ID<br>2. Ignorar monto <= 0<br>3. Ordenar <br>4. Resta-Suma  DEBIT/CREDIT| C1[Output: Listado por AccountId ASC]
        D -->|Regla 1: Ventana 5 min DEBIT<br>Regla 2: Monto > 50,000<br>Regla 3: > 2 cuentas por Device| D1[Output: Lista operaciones]
        E -->|Validaciones Base<br>Aprobación Manager >= 100,000| E1[Output: Boolean Approved/Rejected]
    end
```

---

## Requisitos

**Java JDK 21** (configurado en el `pom.xml`).
- **Maven 3.8+**.

> ⚠️ **Importante:** Maven usa el JDK definido en la variable `JAVA_HOME`, que puede
> ser distinto al que muestra `java -version`. Antes de ejecutar, verifica:
>
> ```bash
> mvn -version
> ```
>
> La línea `Java version` debe indicar **21**. Si indica otra versión (por ejemplo 1.8),
> consulta la sección *Solución de problemas*.

## Cómo ejecutar

1. Clonar el repositorio:
```bash
   git clone https://github.com/AlfEstbns/Tests.git
   cd Tests
```

2. Ejecutar (elige una opción):

   **Desde IntelliJ IDEA:** abrir el proyecto como Maven y ejecutar la clase `Main`.

   **Desde terminal:**
```bash
   mvn compile exec:java
```
## Solución de problemas

**Error `invalid target release: 21`**

Maven está usando un JDK anterior a 21. Apunta `JAVA_HOME` al JDK 21 y vuelve a ejecutar.

*Windows (PowerShell), solo para la sesión actual:*
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
mvn -version
```

*Linux / macOS:*
```bash
export JAVA_HOME=/ruta/al/jdk-21
export PATH="$JAVA_HOME/bin:$PATH"
mvn -version
```

Ajusta la ruta a donde esté instalado tu JDK 21. Luego ejecuta:
```bash
mvn clean compile exec:java
```
3. Salida esperada: un bloque por ejercicio. Cada caso del ejercicio 3 indica
   el resultado y el valor esperado está anotado en el comentario del código.

### Ejecutar otros casos del ejercicio 2

En `Main.ejercicio2Sospechosas()` están los JSON de prueba (ejemplo del PDF,
el mismo desordenado y casos límite). Descomenta la línea del caso que quieras ver.

## Decisiones técnicas y supuestos

### Ejercicio 1: consolidación
- **"Primero" en duplicados:** se toma el primero según la posición en la lista
  de entrada. El enunciado no define el criterio; la alternativa sería el más
  antiguo por `timestamp`.
- El `id` se registra antes de validar el monto: un reintento no se cuenta aunque
  el original haya sido inválido.
- Movimientos con `amount` nulo o <= 0, o con tipo distinto de DEBIT/CREDIT, se ignoran.
- `BigDecimal` para evitar errores de precisión en montos.
- Se asume una sola moneda por cuenta (`currency` no se valida).
- JSON inválido lanza `IllegalArgumentException`; entrada nula o vacía devuelve `[]`.

### Ejercicio 2: operaciones sospechosas
- `java.time.Instant` para marcas de tiempo ISO 8601.
- Los eventos se ordenan por `timestamp` antes de evaluar, así el orden de
  entrada no cambia el resultado.
- Ventana de 5 minutos **inclusiva**: un cuarto débito a exactamente 5:00 genera alerta.
- Tras una alerta se saltan los eventos ya reportados para no repetir alertas
  con ventanas traslapadas.
- La regla de monto alto aplica a DEBIT y CREDIT; el monto exacto de 50,000 no alerta.
- En dispositivo compartido se reportan cuentas únicas.

### Ejercicio 3: refactor
- Se reemplazó el anidamiento por condiciones que rechazan de forma temprana,
  con un método por regla y constantes para el umbral y las monedas.
- Se aceptan monedas en minúsculas (`mxn`); el código original usaba `equals`.

### Nombres
Se evitaron los nombres prohibidos por el enunciado. Clases y métodos:
`ProcessorConsolidation.evaluateMovements`, `SuspiciousAnalyzer.evaluateRisks`,
`ComplianceProcessor.evaluateApproval`.

## Pruebas

Las pruebas son manuales, en `Main.java`:

- **Ejercicio 1:** duplicado por `id`, monto negativo, orden por `accountId`.
- **Ejercicio 2:** ejemplo del PDF, el mismo desordenado, bordes (5:00 y 5:01,
  50,000 y 50,000.01, 2 y 3 cuentas por dispositivo), entradas vacías.
- **Ejercicio 3:** 15 casos, incluidos el borde de 100,000 con y sin manager.
---

## Declaración de Uso de Documentación e IA Generativa
- **Herramientas de IA (Claude):** apoyo para contrastar interpretaciones del enunciado, la comprensión lectora de cada regla, sugerir casos de prueba, estructurar este documento, apoyo para revisar la lógica.
- **Documentación Oficial:** Se consultó la documentación oficial de Java 21 (`java.time`) y Jackson para la correcta serialización de objetos a JSON.