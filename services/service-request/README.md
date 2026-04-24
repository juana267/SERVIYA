# ServiYa - ms-service-request

Microservicio `MS4 - Service Request` para la primera unidad de `ServiYa`. Implementa el nucleo operativo del silabo: crear solicitud de servicio y gestionar sus estados. Tambien incluye la tabla `categoria`, porque en el modelo entregado el servicio depende de `SERVICIO` y `CATEGORIA`.

## Ruta del proyecto

`C:\ms-service-request`

## Puertos

- Instancia 1: `8003`
- Instancia 2: `8004`

## Base de datos

- MySQL 8
- Base sugerida: `db_ms_service_request`

## Tablas implementadas

- `categoria`
- `servicio`

Campos de `servicio` segun el modelo:

- `id_servicio`
- `id_cliente`
- `id_trabajador`
- `id_categoria`
- `descripcion`
- `estado`
- `precio_acordado`
- `fecha_solicitud`
- `fecha_inicio`
- `fecha_fin`
- `direccion_servicio`
- `lat`
- `lng`

## Requerimientos del silabo cubiertos

- RF-05: crear solicitud de servicio con estado inicial `SOLICITADO`
- RF-06: aceptar solicitud `EN_PROCESO`, finalizar `FINALIZADO` y cancelar `CANCELADO`
- RF-10: CRUD de categorias de servicio
- RNF: Config Server, Eureka, JWT, OpenFeign, Maven, Docker y arquitectura por capas

## Integracion con otros servicios

- Seguridad JWT compatible con `ms-users`
- OpenFeign hacia `ms-users` para validar al usuario autenticado

## Endpoints principales

- `POST /api/v1/categorias`
- `GET /api/v1/categorias`
- `GET /api/v1/categorias/{id}`
- `PUT /api/v1/categorias/{id}`
- `DELETE /api/v1/categorias/{id}`
- `POST /api/v1/servicios`
- `GET /api/v1/servicios`
- `GET /api/v1/servicios/{id}`
- `GET /api/v1/servicios/mis-solicitudes`
- `GET /api/v1/servicios/mis-trabajos`
- `PATCH /api/v1/servicios/{id}/aceptar`
- `PATCH /api/v1/servicios/{id}/finalizar`
- `PATCH /api/v1/servicios/{id}/cancelar`

## Reglas implementadas

- El cliente autenticado crea la solicitud
- Toda solicitud nueva nace como `SOLICITADO`
- Solo otro usuario puede aceptar la solicitud
- Al aceptar, el servicio pasa a `EN_PROCESO` y guarda `fecha_inicio`
- Solo el trabajador asignado puede finalizarla
- Solo el cliente creador puede cancelarla y solo antes del inicio

## Ejecucion local

```powershell
cd C:\ms-service-request
.\mvnw spring-boot:run
```

Swagger:

`http://localhost:8003/swagger-ui/index.html`

Segunda instancia:

```powershell
.\mvnw spring-boot:run "-Dspring-boot.run.arguments=--server.port=8004"
```
