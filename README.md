# ServiYa

Proyecto de microservicios reorganizado para la entrega de la primera unidad.  
La solucion actual deja activo un solo microservicio funcional dentro de `services`: `service-request`.

## Estructura

```text
C:\serviya
├── infra
│   ├── config-server
│   ├── registry-server
│   ├── gateway
│   ├── config-repo
│   ├── docker-compose.yml
│   ├── docker-compose-dev.yml
│   └── docker-compose-prod.yml
├── services
│   └── service-request
├── docker-compose.yml
├── docker-compose.dev.yml
└── docker-compose.prod.yml
```

## Microservicio implementado

`service-request`

Requerimientos funcionales cubiertos:

- crear solicitud
- gestion de estados del servicio

Estados implementados:

- `SOLICITADO`
- `EN_PROCESO`
- `FINALIZADO`
- `CANCELADO`

Tablas implementadas:

- `categoria`
- `servicio`

## Arquitectura

Infraestructura incluida:

- Config Server
- Eureka Server
- Spring Cloud Gateway
- Spring Cloud LoadBalancer (`lb://MS-SERVICE-REQUEST`)
- MySQL 8

Microservicio:

- `service-request`
- arquitectura en capas: `controller -> service -> repository`
- Spring Boot 3.x
- Java 17
- Flyway
- JPA
- JWT
- Swagger

## Perfiles

El proyecto queda preparado con `dev` y `prod`.

### En el microservicio

- [application.yml](C:\serviya\services\service-request\src\main\resources\application.yml)
- [application-dev.yml](C:\serviya\services\service-request\src\main\resources\application-dev.yml)
- [application-prod.yml](C:\serviya\services\service-request\src\main\resources\application-prod.yml)

### En infra

- [gateway-dev.yml](C:\serviya\infra\config-repo\gateway-dev.yml)
- [gateway-prod.yml](C:\serviya\infra\config-repo\gateway-prod.yml)
- [registry-server-dev.yml](C:\serviya\infra\config-repo\registry-server-dev.yml)
- [registry-server-prod.yml](C:\serviya\infra\config-repo\registry-server-prod.yml)
- [ms-service-request-dev.yml](C:\serviya\infra\config-repo\ms-service-request-dev.yml)
- [ms-service-request-prod.yml](C:\serviya\infra\config-repo\ms-service-request-prod.yml)

### En Docker

Raiz:

- [docker-compose.yml](C:\serviya\docker-compose.yml)
- [docker-compose.dev.yml](C:\serviya\docker-compose.dev.yml)
- [docker-compose.prod.yml](C:\serviya\docker-compose.prod.yml)

Infra:

- [docker-compose.yml](C:\serviya\infra\docker-compose.yml)
- [docker-compose-dev.yml](C:\serviya\infra\docker-compose-dev.yml)
- [docker-compose-prod.yml](C:\serviya\infra\docker-compose-prod.yml)

## Puertos

- Config Server: `7072`
- Eureka: `7082`
- Gateway: `7092`
- Service Request instancia 1: `8003`
- Service Request instancia 2: `8004`
- MySQL Service Request: `3313`

## URLs

- Eureka: [http://localhost:7082](http://localhost:7082)
- Gateway health: [http://localhost:7092/actuator/health](http://localhost:7092/actuator/health)
- Swagger service request: [http://localhost:7092/swagger-ui/index.html](http://localhost:7092/swagger-ui/index.html)
- Swagger directo del microservicio: [http://localhost:8003/swagger-ui/index.html](http://localhost:8003/swagger-ui/index.html)

## Levantar con Docker

### Perfil base

```powershell
cd C:\serviya
docker compose up -d --build
```

### Perfil dev

```powershell
cd C:\serviya
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build
```

### Perfil prod

```powershell
cd C:\serviya
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build
```

### Detener contenedores

```powershell
cd C:\serviya
docker compose down --remove-orphans
```

## Compilar

### service-request

```powershell
cd C:\serviya\services\service-request
.\mvnw.cmd -q package '-Dspring.profiles.active=dev' -DskipTests
.\mvnw.cmd -q package '-Dspring.profiles.active=prod' -DskipTests
```

### infra

```powershell
cd C:\serviya\infra\config-server
mvn -q package '-Dspring.profiles.active=dev' -DskipTests
mvn -q package '-Dspring.profiles.active=prod' -DskipTests

cd C:\serviya\infra\registry-server
mvn -q package '-Dspring.profiles.active=dev' -DskipTests
mvn -q package '-Dspring.profiles.active=prod' -DskipTests

cd C:\serviya\infra\gateway
mvn -q package '-Dspring.profiles.active=dev' -DskipTests
mvn -q package '-Dspring.profiles.active=prod' -DskipTests
```

## Endpoints del microservicio

Todos los endpoints de negocio requieren JWT Bearer valido.

Categorias:

- `POST /api/v1/categorias`
- `GET /api/v1/categorias`
- `GET /api/v1/categorias/{id}`
- `PUT /api/v1/categorias/{id}`
- `DELETE /api/v1/categorias/{id}`

Solicitudes:

- `POST /api/v1/servicios`
- `GET /api/v1/servicios`
- `GET /api/v1/servicios/{id}`
- `GET /api/v1/servicios/mis-solicitudes`
- `GET /api/v1/servicios/mis-trabajos`
- `PATCH /api/v1/servicios/{id}/aceptar`
- `PATCH /api/v1/servicios/{id}/finalizar`
- `PATCH /api/v1/servicios/{id}/cancelar`

## Como probar lo pedido

### 1. Crear categoria

```powershell
$body = @{
  nombre = 'Plomeria'
  iconoUrl = 'wrench'
} | ConvertTo-Json -Compress

Invoke-RestMethod -Method Post `
  -Uri 'http://localhost:7092/api/v1/categorias' `
  -ContentType 'application/json' `
  -Body $body
```

### 2. Crear solicitud

```powershell
$body = @{
  categoriaId = 1
  descripcion = 'Fuga de agua en cocina'
  direccionServicio = 'Av. Lima 123'
  precioAcordado = 120.50
} | ConvertTo-Json -Compress

Invoke-RestMethod -Method Post `
  -Uri 'http://localhost:7092/api/v1/servicios' `
  -Headers @{ 'Authorization' = 'Bearer TU_JWT' } `
  -ContentType 'application/json' `
  -Body $body
```

Resultado esperado:

- estado inicial `SOLICITADO`

### 3. Aceptar servicio

```powershell
Invoke-RestMethod -Method Patch `
  -Uri 'http://localhost:7092/api/v1/servicios/1/aceptar' `
  -Headers @{ 'Authorization' = 'Bearer JWT_DEL_TRABAJADOR' }
```

Resultado esperado:

- estado `EN_PROCESO`

### 4. Finalizar servicio

```powershell
Invoke-RestMethod -Method Patch `
  -Uri 'http://localhost:7092/api/v1/servicios/1/finalizar' `
  -Headers @{ 'Authorization' = 'Bearer JWT_DEL_TRABAJADOR' }
```

Resultado esperado:

- estado `FINALIZADO`

### 5. Cancelar servicio

```powershell
Invoke-RestMethod -Method Patch `
  -Uri 'http://localhost:7092/api/v1/servicios/1/cancelar' `
  -Headers @{ 'Authorization' = 'Bearer JWT_DEL_CLIENTE' }
```

Regla:

- solo se puede cancelar si aun esta en `SOLICITADO`

## Reglas de negocio implementadas

- toda solicitud nueva nace en `SOLICITADO`
- solo un trabajador puede aceptar una solicitud
- al aceptar, pasa a `EN_PROCESO`
- solo el trabajador asignado puede finalizar
- al finalizar, pasa a `FINALIZADO`
- solo el cliente creador puede cancelar
- cancelar solo es valido antes de iniciar

## Seguridad y balanceo

- los endpoints del microservicio validan JWT firmado con `security.jwt.secret`
- Swagger permite autorizar con `Bearer token`
- Gateway enruta con `lb://MS-SERVICE-REQUEST`
- Eureka registra dos instancias activas del microservicio
- el balanceo se valida llamando varias veces a:
  - `GET /api/v1/service-request/instancia`

## Tests

- prueba sin JWT -> `401`
- prueba de creacion -> estado `SOLICITADO`
- prueba de aceptar y finalizar
- prueba de finalizar con trabajador incorrecto
- prueba de cancelar solo antes del inicio
