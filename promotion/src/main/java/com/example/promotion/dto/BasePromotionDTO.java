package com.example.promotion.dto;

import com.example.promotion.enums.PaymentMode;
import com.example.promotion.enums.VehicleType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BasePromotionDTO {

    @NotBlank(message = "Name is required")
    private String name;


    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    private Double discountValue;

    @NotNull(message = "Percentage indicator is required")
    private Boolean isPercentage;


    @NotNull(message = "Minimum ride fare is required")
    @PositiveOrZero(message = "Minimum ride fare must be non-negative")
    private Double minRideFare;

    private Double maxDiscountAmount;


    @NotEmpty(message = "At least one payment mode is required")
    private List<PaymentMode> paymentModes;


    @NotEmpty(message = "At least one vehicle type is required")
    private List<VehicleType> vehicleTypes;

    @NotNull(message = "Driver wallet flag is required")
    private Boolean addToDriverWallet;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;


    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expiryDate;


}
