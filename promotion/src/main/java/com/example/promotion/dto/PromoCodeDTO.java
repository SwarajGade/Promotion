package com.example.promotion.dto;

import com.example.promotion.enums.VehicleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PromoCodeDTO extends BasePromotionDTO {
    @Pattern(regexp = "^[A-Z0-9_-]{3,20}$", message = "Promo code must be 3-20 characters long and contain only uppercase letters, numbers, hyphens, and underscores")
    private String code;

    @Pattern(regexp = "^(EV|REGULAR|ALL)$", message = "Invalid ride type")
    private String rideType;

    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format. Use HH:mm")
    private String validTimeWindowStart;

    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format. Use HH:mm")
    private String validTimeWindowEnd;

    @Size(min = 1, message = "At least one valid day must be specified")
    private List<String> validDays;

    @Min(value = 1, message = "Minimum rides must be at least 1")
    private Integer minimumRides;

    @Pattern(regexp = "^(SCHEDULED|BACK_TO_BACK)$", message = "Invalid booking condition")
    private String bookingCondition;

    @Size(min = 1, message = "At least one valid area must be specified")
    private List<String> validAreas;

    private LocalDateTime expiryDate;
    private LocalDateTime startDate;
    private Boolean isPercentage;
    private Double maxDiscountAmount;
    private List<VehicleType> vehicleTypes;
    private Boolean addToDriverWallet;


}