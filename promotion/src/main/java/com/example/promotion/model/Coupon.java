package com.example.promotion.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Table("coupons")
public class Coupon extends BasePromotion {
    private String triggerEvent;
    private String userTargeting;
    private Integer usageLimit;
    private Integer minimumDonationAmount;
    private Double purchasePrice;
    private Boolean giftMessageEnabled;
    private String sharingMechanism;
    private Double minimumRideDistance;


}

