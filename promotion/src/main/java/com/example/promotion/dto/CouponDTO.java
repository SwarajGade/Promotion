package com.example.promotion.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class CouponDTO extends BasePromotionDTO {

    @NotBlank(message = "Trigger event is required")
    private String triggerEvent;

    @NotBlank(message = "User targeting criteria is required")
    private String userTargeting;

    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer usageLimit;

    @PositiveOrZero(message = "Minimum donation amount must be non-negative")
    private Integer minimumDonationAmount;

    @Positive(message = "Purchase price must be positive")
    private Double purchasePrice;

    private Boolean giftMessageEnabled;

    private String sharingMechanism;

    @PositiveOrZero(message = "Minimum ride distance must be non-negative")
    private Double minimumRideDistance;
}
