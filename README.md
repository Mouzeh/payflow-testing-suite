# PayFlow Testing Suite 🧪

Suite de automatización de pruebas para un sistema de procesamiento de pagos, construida con stack QA enterprise. Cubre pruebas de API, validación de base de datos y reporte visual con Allure.

## 📊 Resultados

| Métrica | Valor |
|---|---|
| Test Cases | 10 |
| Tasa de éxito | 100% |
| Tiempo de ejecución | ~20s |
| Cobertura | API + BD |

## 🛠️ Stack Tecnológico

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| BDD Framework | Cucumber 7 + Gherkin |
| Pruebas API | RestAssured 5 |
| Base de datos | PostgreSQL 17 |
| Reportes | Allure Reports 2.25 |
| CI/CD | GitHub Actions |
| Gestión | Maven |
| Control versiones | Git / GitHub |

## 🧪 Escenarios de Prueba

### @Critical
| ID | Escenario | Resultado |
|---|---|---|
| TC001 | Pago aprobado con monto válido (USD) | ✅ PASS |
| TC002 | Pago aprobado en euros (EUR) | ✅ PASS |
| TC003 | Pago aprobado monto mínimo | ✅ PASS |
| TC004 | Pago rechazado con tarjeta declinada | ✅ PASS |
| TC005 | Pago rechazado por fondos insuficientes | ✅ PASS |

### @Normal
| ID | Escenario | Resultado |
|---|---|---|
| TC006 | Pago rechazado con tarjeta vencida | ✅ PASS |
| TC007 | Pago rechazado con monto cero | ✅ PASS |
| TC008 | Pago aprobado en pesos chilenos (CLP) | ✅ PASS |
| TC009 | Pago aprobado monto alto | ✅ PASS |
| TC010 | Pago aprobado en pesos mexicanos (MXN) | ✅ PASS |

## 🗄️ Validación de Base de Datos

Cada transacción aprobada se registra y valida en PostgreSQL:

```sql
SELECT test_case, stripe_charge_id, monto, moneda, estado
FROM transacciones ORDER BY id;

-- Resultado:
-- TC001 | ch_3TZW9l... |  5000  | usd | succeeded
-- TC002 | ch_3TZW9p... | 10000  | eur | succeeded
-- TC003 | ch_3TZW9r... |    50  | usd | succeeded
-- TC008 | ch_3TZW9x... |  5000  | clp | succeeded
-- TC009 | ch_3TZW9z... | 999999 | usd | succeeded
-- TC010 | ch_3TZWA1... | 50000  | mxn | succeeded
```

## 🚀 Cómo ejecutar

### Prerrequisitos
- Java 21
- Maven 3.8+
- PostgreSQL 17
- Cuenta Stripe (sandbox gratuita)

### Setup

```bash
# 1. Clonar el repositorio
git clone https://github.com/Mouzeh/payflow-testing-suite.git
cd payflow-testing-suite/payflow-testing-suite

# 2. Crear base de datos
psql -U postgres -c "CREATE DATABASE payflow_test;"
psql -U postgres -d payflow_test -c "
CREATE TABLE transacciones (
  id SERIAL PRIMARY KEY,
  test_case VARCHAR(20) NOT NULL,
  stripe_charge_id VARCHAR(100),
  monto INTEGER NOT NULL,
  moneda VARCHAR(10) NOT NULL,
  estado VARCHAR(20) NOT NULL,
  descripcion TEXT,
  creado_en TIMESTAMP DEFAULT NOW()
);"

# 3. Configurar credenciales
cp src/test/resources/config.properties.example src/test/resources/config.properties
# Editar config.properties con tu Stripe API key y datos de BD

# 4. Ejecutar suite completa
mvn clean test

# 5. Ver reporte Allure
cp src/test/resources/environment.properties target/allure-results/
allure serve target/allure-results
```

## 📁 Estructura del Proyecto

```
payflow-testing-suite/
├── src/test/
│   ├── java/com/payflow/
│   │   ├── config/
│   │   │   ├── ConfigManager.java      # Gestión de configuración
│   │   │   └── DatabaseManager.java    # Conexión y operaciones BD
│   │   ├── steps/
│   │   │   ├── PagosSteps.java        # Step definitions principales
│   │   │   └── TestCaseSteps.java     # Captura de metadata
│   │   └── runner/
│   │       └── RunnerTest.java        # Configuración Cucumber
│   └── resources/
│       ├── features/
│       │   └── pagos.feature          # Escenarios BDD en español
│       ├── allure.properties          # Configuración Allure
│       └── environment.properties     # Variables de ambiente
├── .github/workflows/
│   └── qa-pipeline.yml               # Pipeline CI/CD
└── pom.xml                           # Dependencias Maven
```

## 👤 Autor

**Rudy Castillo** — QA Automation Engineer  
[GitHub](https://github.com/Mouzeh)

---
*Proyecto desarrollado como portafolio profesional QA orientado a sistemas financieros y medios de pago.*
