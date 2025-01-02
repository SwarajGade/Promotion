package com.example.promotion.model;

import com.example.promotion.enums.PaymentMode;
import com.example.promotion.enums.PromotionType;
import com.example.promotion.enums.VehicleType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Table("promo_codes")
public class PromoCode extends BasePromotion {
    private String rideType;
    private String validTimeWindowStart;
    private String validTimeWindowEnd;
    private List<String> validDays;
    private Integer minimumRides;
    private String bookingCondition;
    private List<String> validAreas;


}