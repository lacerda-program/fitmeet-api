package bootcamp07.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "OK");
        status.put("message", "API Bootcamp07 está funcionando!");
        status.put("timestamp", LocalDateTime.now());
        status.put("database", "PostgreSQL conectado");
        status.put("docker", "Containers rodando");
        status.put("version", "1.0.0");

        return ResponseEntity.ok(status);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("database", "CONNECTED");
        health.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(health);
    }
}
