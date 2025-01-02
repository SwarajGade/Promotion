package com.example.promotion.model;

import com.example.promotion.enums.PaymentMode;
import com.example.promotion.enums.PromotionType;
import com.example.promotion.enums.VehicleType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.List;

@Data
public abstract class BasePromotion {
    @Id
    private Long id;
    private String name;
    private Double discountValue;
    private Boolean isPercentage;
    private Double minRideFare;
    private Double maxDiscountAmount;
    private List<PaymentMode> paymentModes;
    private List<VehicleType> vehicleTypes;
    private Boolean addToDriverWallet;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private Boolean isActive;
    private PromotionType type;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    public void setName(String name) {
        this.name = (name != null) ? name.toLowerCase() : null;
    }
}