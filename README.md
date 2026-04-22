# Microservicio MS5 - Assignment (ms-assignment)

Microservicio Spring Boot para la gestión de asignaciones dentro de la arquitectura de microservicios 2026.

---

## Estado del proyecto

Actualmente incluye:

- API REST funcional
- Persistencia con MySQL
- Configuracion por perfiles (`dev`, `prod`)
- Contenerizacion con Docker
- Integracion operativa con **Config Server**
- Integracion operativa con **Registry Server (Eureka)**
- Integracion operativa con **API Gateway**
- Enrutamiento dinamico con **`lb://ms-assignment`**

---

## Arquitectura (estado actual)

```text
Client -> API Gateway -> Microservicios -> Registry Server -> Config Server
```

Este repositorio implementa unicamente el microservicio **ms-assignment**.

---

## Stack tecnologico base 2026

- Java 17
- Spring Boot 3.5.x
- Spring Cloud 2025.x
- Maven 3.9+
- MySQL 8
- Docker
- Docker Compose
- Spring Cloud Config Client
- Eureka Client
- Flyway
- Actuator
- SpringDoc OpenAPI

---

## Puertos utilizados

| Servicio | Puerto expuesto |
|---|---:|
| MS-ASSIGNMENT DEV | 8081 |
| MS-ASSIGNMENT PROD | 8082 |
| MySQL DEV | 3307 |
| MySQL PROD | 3308 |
| Config Server DEV | 7071 |
| Config Server PROD | 7072 |
| Registry Server DEV | 8761 |
| Registry Server PROD | 8762 |
| Gateway DEV | 9090 |
| Gateway PROD | 9091 |

---

## DEV vs PROD

| Modo | Ejecucion app | Base de datos | Configuracion | Registro | Puerto app |
|---|---|---|---|---|---:|
| DEV | `mvn spring-boot:run` | Docker/local | Config Server DEV | Registry DEV | 8081 |
| PROD | Docker | Docker | Config Server PROD | Registry PROD | 8082 |

---

# Ejecucion DEV con Config + Registry

## Objetivo

Ejecutar `ms-assignment` en modo desarrollo consumiendo configuracion externa y registrando la instancia en Eureka.

---

## 1. Levantar Config Server (DEV)

Desde `infra/config-server`:

```bash
mvn spring-boot:run
```

Prueba:

```text
http://localhost:7071/ms-assignment/dev
```

---

## 2. Levantar Registry Server (DEV)

Desde `infra/registry-server`:

```bash
mvn spring-boot:run
```

Dashboard:

```text
http://localhost:8761
```

---

## 3. Levantar MySQL de desarrollo

Desde `services/MS5`:

```bash
docker compose -f docker-compose-dev.yml up -d
```

---

## 4. Ejecutar ms-assignment en DEV

Desde `services/MS5`:

```bash
mvn spring-boot:run
```

---

## 5. Probar

Swagger UI:

```text
http://localhost:8081/swagger-ui/index.html
```

Registro en Eureka:

```text
http://localhost:8761
```

---

# Escalado manual en DEV

Para levantar una segunda instancia local de `ms-assignment` en desarrollo, ejecuta la aplicacion en otro puerto:

```bash
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8085"
```

Prueba la segunda instancia:

```text
http://localhost:8085/swagger-ui/index.html
```

---

# Ejecucion PROD con Config + Registry

## Objetivo

Ejecutar `ms-assignment` en contenedor Docker consumiendo configuracion externa y registro de servicio en Eureka.

---

## 1. Levantar infraestructura (config + registry)

Desde `infra`:

```bash
docker compose up -d
```

Pruebas:

```text
http://localhost:7072/ms-assignment/prod
http://localhost:8762
```

---

## 2. Archivo `.env` (modo PROD)

En `services/MS5/.env`:

```env
ASSIGNMENT_MYSQL_ROOT_PASSWORD=root
ASSIGNMENT_MYSQL_DATABASE=db_asignacion

SPRING_PROFILES_ACTIVE=prod

CONFIG_SERVER_URL=http://config-server:7071

ASSIGNMENT_DB_HOST=mysql-asignacion
ASSIGNMENT_DB_PORT=3306
ASSIGNMENT_DB_NAME=db_asignacion
ASSIGNMENT_DB_USERNAME=root
ASSIGNMENT_DB_PASSWORD=root
```

---

## 3. Redes utilizadas

- `ms-net` -> red comun de infraestructura (config-server, registry-server, gateway futuro, microservicios)
- `assignment-int` -> red interna de ms-assignment (mysql + app)

---

## 4. Levantar ms-assignment en modo productivo

Desde `services/MS5`:

```bash
docker compose up -d
```

---

## 5. Probar

API:

```text
http://localhost:8082/api/v1/asignaciones/auto/1
```

Eureka PROD (host):

```text
http://localhost:8762
```

---

# Configuracion externa (config-repo)

Archivos esperados:

```text
infra/config-repo/ms-assignment-dev.yml
infra/config-repo/ms-assignment-prod.yml
```

Puntos clave ya configurados:

- `ms-assignment-dev.yml` usa `eureka.client.service-url.defaultZone=http://localhost:8761/eureka`
- `ms-assignment-prod.yml` usa `eureka.client.service-url.defaultZone=http://registry-server:8761/eureka`

---

# Escalado manual (sin Gateway)

## 1. Bajar stack de ms-assignment

```bash
docker compose down
```

## 2. Levantar solo MySQL

```bash
docker compose up -d mysql-asignacion
```

## 3. Construir imagen

```bash
docker build -t ms-assignment-service .
```

## 4. Crear instancias

### ms-assignment1

```powershell
docker create `
  --name ms-assignment1 `
  --network ms-net `
  --env-file .env `
  -p 8082:8082 `
  ms-assignment-service

docker network connect assignment-int ms-assignment1
docker start ms-assignment1
```

### ms-assignment2

```powershell
docker create `
  --name ms-assignment2 `
  --network ms-net `
  --env-file .env `
  -p 8083:8082 `
  ms-assignment-service

docker network connect assignment-int ms-assignment2
docker start ms-assignment2
```

### ms-assignment3

```powershell
docker create `
  --name ms-assignment3 `
  --network ms-net `
  --env-file .env `
  -p 8084:8082 `
  ms-assignment-service

docker network connect assignment-int ms-assignment3
docker start ms-assignment3
```

## 5. Probar instancias

```text
http://localhost:8082/api/v1/asignaciones/auto/1
http://localhost:8083/api/v1/asignaciones/auto/1
http://localhost:8084/api/v1/asignaciones/auto/1
```

---

# Problemas resueltos

- Config Server no accesible -> faltaba red `ms-net`
- Registry Server no accesible -> infraestructura no levantada completa
- MySQL no accesible -> faltaba red `assignment-int`
- Error datasource -> configuracion externa no cargada
- Error `UnknownHost` -> nombres de host/redes mal definidos

---

# Gateway + Load Balance (trabajado)

Orden aplicado durante la implementacion:

1. Crear o clonar repo `infra` desde el tag `vs03-registry-server` y repo `ms-assignment` tambien desde `vs03-registry-server`.
2. Crear proyecto `gateway` en `infra`.
3. Conectar `gateway` a Config Server.
4. Probar `gateway` en DEV.
5. Suscribir `gateway` a Eureka en modo DEV.
6. Definir ruta: `uri: lb://ms-assignment`.
7. Escalado en DEV con multiples instancias.
8. Configurar `gateway` para PROD.
9. Probar `gateway` en PROD.
10. Levantar varias instancias de `ms-assignment` en PROD y probar.
11. Revision de escalado automatico.

---

# Estado de avance

- [x] Config Server
- [x] Registry Server (Eureka)
- [x] API Gateway
- [x] Enrutamiento `lb://ms-assignment`
- [ ] Feign
- [ ] Circuit Breaker
- [ ] Balanceador externo
- [ ] Seguridad

---

# Siguiente paso

Continuar con atributos de calidad sobre la base actual:

- implementar comunicacion entre microservicios con Feign
- agregar resiliencia con Circuit Breaker
- formalizar estrategia de balanceador externo
- integrar seguridad

---

# Tag sugerido

```bash
git tag -a vs04-gateway-lb -m "ms-assignment integrado con API Gateway y enrutamiento lb://ms-assignment"
git push origin vs04-gateway-lb
```
