package school.hei.rattrapageprog3.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.rattrapageprog3.service.HealthService;

@RestController
@AllArgsConstructor
public class HealthController {
    private final HealthService healthService;

    @GetMapping("/health")
    public ResponseEntity<?> checkHealth() {
        try {
            healthService.checkConnection();
            return ResponseEntity.ok("Database connection OK");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }
}
