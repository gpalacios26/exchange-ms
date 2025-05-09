# Code challenge - Ejercicio Lvl 3: Microservicio Exchange

Este proyecto es un microsevicio **Java** utilizando **Spring Boot** y **WebFlux** para construir una API reactiva de posturas de compra y venta de divisas.

---

## 🚀 Configuración del Proyecto

### 1️⃣ Clonar el repositorio

```bash
git clone https://github.com/gpalacios26/exchange-ms.git
cd exchange-ms
```

### 2️⃣ Ejecutar la aplicación

Con **Maven**:

```bash
mvn spring-boot:run
```

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Especificación OpenAPI (en JSON): [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🌐 Endpoints de la API

Utilizar la colección `Exchange-MS.postman_collection.json` que se encuentra dentro de la carpeta postman.

---

## 🧪 Ejecución de Pruebas

Para correr las pruebas unitarias:

Con **Maven**:

```bash
mvn clean test
```

Las pruebas utilizan `JUnit 5`, `Mockito` y `WebTestClient` para verificar la funcionalidad de la API.

El reporte de cobertura de pruebas se genera con `Jacoco` en la carpeta `target/site/jacoco/index.html`.

---

## 📑 Requisitos

- **Java 17** instalado.
- **Maven** configurado.
- IDE recomendado: **IntelliJ IDEA**.
