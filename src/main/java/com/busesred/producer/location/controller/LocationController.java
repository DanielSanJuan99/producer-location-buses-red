package com.busesred.producer.location.controller;

import com.busesred.producer.location.model.LocationMessage;
import com.busesred.producer.location.service.LocationProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/location")
@Slf4j
public class LocationController {

    private final LocationProducerService producerService;

    public LocationController(LocationProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendLocation(@RequestBody LocationMessage locationMessage) {
        try {
            if (locationMessage.getTimestamp() == null) {
                locationMessage.setTimestamp(LocalDateTime.now());
            }
            
            log.info("Recibida solicitud para enviar ubicación: {}", locationMessage);
            producerService.sendLocation(locationMessage);
            
            Map<String, Object> response = new HashMap<>();
            response.put("estado", "exitoso");
            response.put("mensaje", "Ubicación enviada a RabbitMQ correctamente");
            response.put("datos", locationMessage);
            response.put("marca_tiempo", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al enviar ubicación: ", e);
            Map<String, Object> response = new HashMap<>();
            response.put("estado", "error");
            response.put("mensaje", "Error al enviar ubicación: " + e.getMessage());
            response.put("marca_tiempo", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("estado", "ACTIVO");
        response.put("servicio", "producer-location-buses-red");
        response.put("marca_tiempo", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}
