package tech.buildrun.btgpactual.orderms.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Check Controller - Verifica o status da aplicação e suas dependências
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class HealthCheckController {
    private final MongoTemplate mongoTemplate;

    @GetMapping("/health")
    public ResponseEntity<HealthStatus> health() {
        try {
            // Verifica conexão com MongoDB
            mongoTemplate.executeCommand("{ ping: 1 }");

            log.info("Health check: MongoDB está acessível");
            return ResponseEntity.ok(new HealthStatus(
                    "UP",
                    "API está rodando e banco de dados está acessível"
            ));
        } catch (Exception e) {
            log.error("Health check falhou: {}", e.getMessage());
            return ResponseEntity.status(503).body(new HealthStatus(
                    "DOWN",
                    "Erro ao conectar no banco de dados: " + e.getMessage()
            ));
        }
    }

    public record HealthStatus(String status, String message) {
    }
}

