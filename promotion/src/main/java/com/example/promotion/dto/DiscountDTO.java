package com.example.promotion.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.List;

@Data
public class DiscountDTO extends BasePromotionDTO {
    private String weatherCondition;

    private String geographicArea;

    private Boolean greenSurchargeEnabled;

    @PositiveOrZero(message = "Surcharge amount must be non-negative")
    private Double surchargeAmount;

    private List<String> destinationTargeting;

    @Positive(message = "Time window must be positive")
    private Integer timeWindow;

    @Min(value = 1, message = "Loyalty points multiplier must be at least 1")
    private Integer loyaltyPointsMultiplier;


}
