# Microservicio Catálogo

Microservicio Spring Boot para la gestión del catálogo dentro de una arquitectura de microservicios en evolución.

---

## Estado del proyecto

Actualmente incluye:

- API REST funcional
- Persistencia con MySQL
- Configuración por perfiles (`dev`, `prod`)
- Contenerización con Docker
- Integración operativa con **Config Server**
- Preparado para integración con:
  - **Eureka (siguiente paso)**
  - **API Gateway (después)**

---

## Arquitectura (visión)

```text
Client → API Gateway → Microservicios → Eureka → Config Server
```

Este repositorio implementa únicamente el microservicio **Catálogo**.

---

## Stack tecnológico base 2026

- Java 17
- Spring Boot 3.5.x
- Maven 3.9+
- MySQL 8
- Docker
- Docker Compose
- Spring Cloud Config Client
- Flyway
- Actuator
- SpringDoc OpenAPI

---

## Puertos utilizados

| Servicio | Puerto expuesto |
|---|---:|
| Aplicación DEV | 8081 |
| Aplicación PROD | 8082 |
| MySQL DEV | 3307 |
| MySQL PROD | 3308 |
| Config Server DEV | 7071 |
| Config Server PROD | 7072 |

---

## DEV vs PROD

| Modo | Ejecución app | Base de datos | Configuración | Puerto app |
|---|---|---|---|---:|
| DEV | `mvn spring-boot:run` | Docker/local | Config Server DEV | 8081 |
| PROD | Docker | Docker | Config Server PROD | 8082 |

---

# Ejecución DEV con Config Server

## Objetivo

Ejecutar `catalogo` en modo desarrollo consumiendo configuración desde Config Server.

---

## 1. Levantar Config Server (modo DEV)

Desde el proyecto `infra`:

```bash
mvn spring-boot:run
```

Acceso:

```
http://localhost:7071/catalogo/dev
```

---


---

## 3. Levantar catalogo DEV

MySQL
```bash
docker compose -f docker-compose-dev.yml up -d
```

---

## 4. Ejecutar catalogo

```bash
mvn spring-boot:run
```

---

## 5. Probar

```
http://localhost:8081/swagger-ui/
```

---

# Ejecución PROD con Config Server

## Objetivo

Ejecutar `catalogo` en contenedor Docker consumiendo configuración externa desde Config Server.

---

## 1. Levantar Config Server (modo PROD)

Desde `infra`:

```bash
docker compose up 
```
```bash
docker compose up -d config-server
```

Acceso:

```
http://localhost:7072/catalogo/prod
```

---

## 2. Archivo `.env` (modo PROD)

```env
CATALOGO_MYSQL_ROOT_PASSWORD=root
CATALOGO_MYSQL_DATABASE=db_catalogo

SPRING_PROFILES_ACTIVE=prod

CONFIG_SERVER_URL=http://config-server:7071

CATALOGO_DB_HOST=mysql-catalogo
CATALOGO_DB_PORT=3306
CATALOGO_DB_NAME=db_catalogo
CATALOGO_DB_USERNAME=root
CATALOGO_DB_PASSWORD=root
```

---

## 3. Redes utilizadas

- `ms-net` → infraestructura (config-server, futuro eureka, gateway)
- `catalogo-int` → red interna (mysql + catalogo)

---

## 4. Levantar modo productivo

```bash
docker compose up -d
```

---

## 5. Probar

```
http://localhost:8082/api/v1/categorias
```

---

# Configuración externa (config-repo)

Archivo esperado:

```
config-repo/catalogo-dev.yml
config-repo/catalogo-prod.yml
```

Ejemplo:

```yaml
spring:
  datasource:
    url: jdbc:mysql://${CATALOGO_DB_HOST}:${CATALOGO_DB_PORT}/${CATALOGO_DB_NAME}
    username: ${CATALOGO_DB_USERNAME}
    password: ${CATALOGO_DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver

  flyway:
    enabled: true

  jpa:
    hibernate:
      ddl-auto: validate

server:
  port: 8082
```

---

# Escalado manual (sin Eureka)

## 1. Bajar stack

```bash
docker compose down
```

## 2. Levantar solo MySQL

```bash
docker compose up -d mysql-catalogo
```

## 3. Construir imagen

```bash
docker build -t catalogo-service .
```

---

## 4. Crear instancias

### catalogo1
```powershell
docker create `
  --name catalogo1 `
  --network ms-net `
  --env-file .env `
  -p 8082:8082 `
  catalogo-service

docker network connect catalogo-int catalogo1
docker start catalogo1
```

### catalogo2
```powershell
docker create `
  --name catalogo2 `
  --network ms-net `
  --env-file .env `
  -p 8083:8082 `
  catalogo-service

docker network connect catalogo-int catalogo2
docker start catalogo2
```

### catalogo3
```powershell
docker create `
  --name catalogo3 `
  --network ms-net `
  --env-file .env `
  -p 8084:8082 `
  catalogo-service

docker network connect catalogo-int catalogo3
docker start catalogo3
```

---

## 5. Probar instancias

```
http://localhost:8082/api/v1/categorias
http://localhost:8083/api/v1/categorias
http://localhost:8084/api/v1/categorias
```

---

# Problemas resueltos

- Config Server no accesible → faltaba `ms-net`
- MySQL no accesible → faltaba `catalogo-int`
- Error datasource → config no cargada
- Error `UnknownHost` → redes mal conectadas

---

# Estado de avance

- [x] Config Server
- [ ] Eureka / Registry Server
- [ ] API Gateway
- [ ] Escalado (Gateway + lb://)
- [ ] Circuit Breaker
- [ ] Seguridad
- [ ] Balanceador externo

---

# Siguiente paso

Integrar **Eureka** para:

- registrar instancias automáticamente
- permitir descubrimiento
- usar `lb://catalogo` desde Gateway

---

# Tag sugerido

```bash
git tag -a vs02-config-server -m "Integración con Config Server"
git push origin vs02-config-server
```