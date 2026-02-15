package com.busesred.producer.location.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationMessage implements Serializable {
    private String busId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private String route;
    private Double speed;
}
