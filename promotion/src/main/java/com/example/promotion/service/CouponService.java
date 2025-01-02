package com.example.promotion.service;

import com.example.promotion.dto.CouponDTO;
import com.example.promotion.dto.ValidationRequest;
import com.example.promotion.dto.ValidationResponse;
import com.example.promotion.enums.PromotionType;
import com.example.promotion.exception.DuplicateException;
import com.example.promotion.model.Coupon;
import com.example.promotion.repository.CouponRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Component
public class CouponService implements PromotionService<Coupon, CouponDTO> {

    @Autowired
    private  CouponRepository couponRepository;

    @Autowired
    private  ValidationService validationService;

    @Override
    public Mono<Coupon> createPromotion(CouponDTO dto) {
        return validatePromotion(dto)
                .filter(valid -> valid)
                .switchIfEmpty(Mono.error(new ValidationException("Validation failed")))
                .then(couponRepository.existsByName(dto.getName()))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateException("Coupon with this name already exists"));
                    }
                    Coupon coupon = mapDtoToEntity(dto);
                    return couponRepository.save(coupon);
                });
    }


    @Override
    public Mono<Coupon> updatePromotion(Long id, CouponDTO dto) {
        return null;
    }


    @Override
    public Mono<Coupon> getPromotionById(Long id) {
        return null;
    }


    @Override
    public Flux<Coupon> getAllPromotions() {
        return null;
    }


    @Override
    public Mono<Void> deactivatePromotion(Long id) {
        return null;
    }

    @Override
    public Mono<Boolean> validatePromotion(CouponDTO dto) {
        return Mono.defer(() -> {
            List<String> errors = new ArrayList<>();

            // Basic validations
            if (dto.getUsageLimit() <= 0) {
                errors.add("Usage limit must be greater than zero");
            }

            if (dto.getPurchasePrice() != null && dto.getPurchasePrice() >= dto.getDiscountValue()) {
                errors.add("Purchase price must be less than discount value");
            }

            if (dto.getMinimumDonationAmount() != null && dto.getMinimumDonationAmount() <= 0) {
                errors.add("Minimum donation amount must be greater than zero");
            }

            // Validate sharing mechanism for gift coupons
            if (Boolean.TRUE.equals(dto.getGiftMessageEnabled()) &&
                    (dto.getSharingMechanism() == null || dto.getSharingMechanism().isEmpty())) {
                errors.add("Sharing mechanism is required for gift coupons");
            }

            return Mono.just(errors.isEmpty())
                    .doOnNext(valid -> {
                        if (!valid) {
                            throw new ValidationException(String.join(", ", errors));
                        }
                    });
        });
    }
    private Coupon mapDtoToEntity(CouponDTO dto) {
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(dto, coupon);
        coupon.setType(PromotionType.COUPON);
        coupon.setIsActive(true);
        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setUpdatedAt(LocalDateTime.now());
        return coupon;
    }

    public Mono<ValidationResponse> validateCoupon(String code, ValidationRequest request) {
        return couponRepository.findByCode(code)
                .flatMap(coupon -> {
                    ValidationResponse response = new ValidationResponse();
                    if (coupon == null) {
                        response.setIsValid(false);
                        response.setMessage("Coupon not found");
                        return Mono.just(response);
                    }

                    // Add your validation logic here
                    boolean isValid = validationService.validateCouponRules(coupon, request);
                    response.setIsValid(isValid);
                    response.setMessage(isValid ? "Coupon is valid" : "Coupon is invalid");

                    return Mono.just(response);
                });
    }
}
