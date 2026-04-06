# Microservicio Catalogo

Microservicio Spring Boot para la gestion del catalogo dentro de la arquitectura de microservicios 2026.

---

## Estado del proyecto

Actualmente incluye:

- API REST funcional
- Persistencia con MySQL
- Configuracion por perfiles (`dev`, `prod`)
- Contenerizacion con Docker
- Integracion operativa con **Config Server**
- Integracion operativa con **Registry Server (Eureka)**
- Preparado para integracion con:
  - **API Gateway (siguiente paso)**

---

## Arquitectura (estado actual)

```text
Client -> API Gateway (pendiente) -> Microservicios -> Registry Server -> Config Server
```

Este repositorio implementa unicamente el microservicio **Catalogo**.

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
| Catalogo DEV | 8081 |
| Catalogo PROD | 8082 |
| MySQL DEV | 3307 |
| MySQL PROD | 3308 |
| Config Server DEV | 7071 |
| Config Server PROD | 7072 |
| Registry Server DEV | 8761 |
| Registry Server PROD | 8762 |

---

## DEV vs PROD

| Modo | Ejecucion app | Base de datos | Configuracion | Registro | Puerto app |
|---|---|---|---|---|---:|
| DEV | `mvn spring-boot:run` | Docker/local | Config Server DEV | Registry DEV | 8081 |
| PROD | Docker | Docker | Config Server PROD | Registry PROD | 8082 |

---

# Ejecucion DEV con Config + Registry

## Objetivo

Ejecutar `catalogo` en modo desarrollo consumiendo configuracion externa y registrando la instancia en Eureka.

---

## 1. Levantar Config Server (DEV)

Desde `infra/config-server`:

```bash
mvn spring-boot:run
```

Prueba:

```text
http://localhost:7071/catalogo/dev
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

Desde `services/catalogo`:

```bash
docker compose -f docker-compose-dev.yml up -d
```

---

## 4. Ejecutar catalogo en DEV

Desde `services/catalogo`:

```bash
mvn spring-boot:run
```

---

## 5. Probar

Swagger UI:

```text
http://localhost:8081/swagger-ui/
```

Registro en Eureka:

```text
http://localhost:8761
```

---

# Escalado manual en DEV

Para levantar una segunda instancia local de `catalogo` en desarrollo, ejecuta la aplicacion en otro puerto:

```bash
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8085"
```

Prueba la segunda instancia:

```text
http://localhost:8085/swagger-ui/
```

---

# Ejecucion PROD con Config + Registry

## Objetivo

Ejecutar `catalogo` en contenedor Docker consumiendo configuracion externa y registro de servicio en Eureka.

---

## 1. Levantar infraestructura (config + registry)

Desde `infra`:

```bash
docker compose up -d
```

Pruebas:

```text
http://localhost:7072/catalogo/prod
http://localhost:8762
```

---

## 2. Archivo `.env` (modo PROD)

En `services/catalogo/.env`:

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

- `ms-net` -> red comun de infraestructura (config-server, registry-server, gateway futuro, microservicios)
- `catalogo-int` -> red interna de catalogo (mysql + app)

---

## 4. Levantar catalogo en modo productivo

Desde `services/catalogo`:

```bash
docker compose up -d
```

---

## 5. Probar

API:

```text
http://localhost:8082/api/v1/categorias
```

Eureka PROD (host):

```text
http://localhost:8762
```

---

# Configuracion externa (config-repo)

Archivos esperados:

```text
infra/config-repo/catalogo-dev.yml
infra/config-repo/catalogo-prod.yml
```

Puntos clave ya configurados:

- `catalogo-dev.yml` usa `eureka.client.service-url.defaultZone=http://localhost:8761/eureka`
- `catalogo-prod.yml` usa `eureka.client.service-url.defaultZone=http://registry-server:8761/eureka`

---

# Escalado manual (sin Gateway)

## 1. Bajar stack de catalogo

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

## 5. Probar instancias

```text
http://localhost:8082/api/v1/categorias
http://localhost:8083/api/v1/categorias
http://localhost:8084/api/v1/categorias
```

---

# Problemas resueltos

- Config Server no accesible -> faltaba red `ms-net`
- Registry Server no accesible -> infraestructura no levantada completa
- MySQL no accesible -> faltaba red `catalogo-int`
- Error datasource -> configuracion externa no cargada
- Error `UnknownHost` -> nombres de host/redes mal definidos

---

# Estado de avance

- [x] Config Server
- [x] Registry Server (Eureka)
- [ ] API Gateway
- [ ] Enrutamiento `lb://catalogo`
- [ ] Circuit Breaker
- [ ] Seguridad
- [ ] Balanceador externo

---

# Siguiente paso

Implementar **API Gateway** para:

- enrutar trafico a servicios registrados
- usar descubrimiento dinamico (`lb://catalogo`)
- preparar balanceo entre multiples instancias

---

# Tag sugerido

```bash
git tag -a vs03-registry-server -m "Catalogo integrado con Registry Server"
git push origin vs03-registry-server
```
