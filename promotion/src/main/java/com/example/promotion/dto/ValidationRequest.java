package com.example.promotion.dto;

import com.example.promotion.enums.PaymentMode;
import com.example.promotion.enums.VehicleType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ValidationRequest {
    private Long userId;
    private Double rideFare;
    private String rideType;
    private VehicleType vehicleType;
    private PaymentMode paymentMode;
    private LocalDateTime rideTime;
    private String sourceArea;
    private String destinationArea;
    private Double rideDistance;
    private Integer consecutiveRides;
    private String weatherCondition;
    private Boolean isGreenRide;
}
