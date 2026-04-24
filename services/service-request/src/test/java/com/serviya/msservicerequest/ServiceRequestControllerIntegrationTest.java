package com.serviya.msservicerequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviya.msservicerequest.dto.service.CreateServiceRequest;
import com.serviya.msservicerequest.entity.Categoria;
import com.serviya.msservicerequest.repository.CategoriaRepository;
import com.serviya.msservicerequest.repository.ServicioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ServiceRequestControllerIntegrationTest {

    private static final String JWT_SECRET = "VGhpc0lzQVN1cGVyU2VjdXJlSldUU2VjcmV0S2V5Rm9yTXNVc2VyczIwMjY=";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @BeforeEach
    void setUp() {
        servicioRepository.deleteAll();
        categoriaRepository.deleteAll();
    }

    @Test
    void shouldRejectCreateWithoutJwt() throws Exception {
        Long categoriaId = saveCategoria("Plomeria");
        CreateServiceRequest request = buildRequest(categoriaId);

        mockMvc.perform(post("/api/v1/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateServiceRequestWithSolicitadoState() throws Exception {
        Long categoriaId = saveCategoria("Gasfiteria");
        CreateServiceRequest request = buildRequest(categoriaId);

        mockMvc.perform(post("/api/v1/servicios")
                        .header("Authorization", bearerToken(101L, "cliente@serviya.test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(101))
                .andExpect(jsonPath("$.trabajadorId").doesNotExist())
                .andExpect(jsonPath("$.estado").value("SOLICITADO"));
    }

    @Test
    void shouldAcceptAndFinishServiceWithAuthenticatedWorker() throws Exception {
        Long categoriaId = saveCategoria("Electricidad");
        Long servicioId = createServicio(categoriaId, 101L);

        mockMvc.perform(patch("/api/v1/servicios/{id}/aceptar", servicioId)
                        .header("Authorization", bearerToken(202L, "worker@serviya.test")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trabajadorId").value(202))
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));

        mockMvc.perform(patch("/api/v1/servicios/{id}/finalizar", servicioId)
                        .header("Authorization", bearerToken(202L, "worker@serviya.test")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("FINALIZADO"));
    }

    @Test
    void shouldNotAllowFinishingByAnotherWorker() throws Exception {
        Long categoriaId = saveCategoria("Cerrajeria");
        Long servicioId = createServicio(categoriaId, 101L);

        mockMvc.perform(patch("/api/v1/servicios/{id}/aceptar", servicioId)
                        .header("Authorization", bearerToken(202L, "worker@serviya.test")))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/v1/servicios/{id}/finalizar", servicioId)
                        .header("Authorization", bearerToken(303L, "otro-worker@serviya.test")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Solo el trabajador asignado puede finalizar el servicio"));
    }

    @Test
    void shouldCancelOnlyWhenServiceIsStillSolicitado() throws Exception {
        Long categoriaId = saveCategoria("Pintura");
        Long servicioId = createServicio(categoriaId, 101L);

        mockMvc.perform(patch("/api/v1/servicios/{id}/cancelar", servicioId)
                        .header("Authorization", bearerToken(101L, "cliente@serviya.test")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));

        Long otroServicioId = createServicio(categoriaId, 101L);
        mockMvc.perform(patch("/api/v1/servicios/{id}/aceptar", otroServicioId)
                        .header("Authorization", bearerToken(202L, "worker@serviya.test")))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/v1/servicios/{id}/cancelar", otroServicioId)
                        .header("Authorization", bearerToken(101L, "cliente@serviya.test")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Solo se puede cancelar un servicio antes de su inicio"));
    }

    private Long createServicio(Long categoriaId, Long clienteId) throws Exception {
        CreateServiceRequest request = buildRequest(categoriaId);
        String response = mockMvc.perform(post("/api/v1/servicios")
                        .header("Authorization", bearerToken(clienteId, "cliente@serviya.test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        return objectMapper.readTree(response).get("id").asLong();
    }

    private Long saveCategoria(String nombre) {
        return categoriaRepository.save(Categoria.builder()
                        .nombre(nombre)
                        .iconoUrl("icon")
                        .build())
                .getId();
    }

    private CreateServiceRequest buildRequest(Long categoriaId) {
        return CreateServiceRequest.builder()
                .categoriaId(categoriaId)
                .descripcion("Servicio de prueba")
                .direccionServicio("Av. Test 123")
                .precioAcordado(new BigDecimal("120.50"))
                .build();
    }

    private String bearerToken(Long userId, String email) {
        return "Bearer " + Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(signingKey())
                .compact();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
    }
}
