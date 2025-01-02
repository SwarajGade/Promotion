package com.example.promotion.service;

import com.example.promotion.dto.PromoCodeDTO;
import com.example.promotion.dto.ValidationRequest;
import com.example.promotion.dto.ValidationResponse;
import com.example.promotion.enums.PromotionType;
import com.example.promotion.exception.DuplicateException;
import com.example.promotion.model.PromoCode;
import com.example.promotion.repository.PromoCodeRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Validated
public class PromoCodeService implements PromotionService<PromoCode, PromoCodeDTO> {

    @Autowired
    private  PromoCodeRepository promoCodeRepository;
    @Autowired
    private  ValidationService validationService;

    @Override
    public Mono<PromoCode> createPromotion(PromoCodeDTO dto) {
        return validatePromotion(dto)
                .filter(valid -> valid)
                .switchIfEmpty(Mono.error(new ValidationException("Validation failed")))
                .then(promoCodeRepository.existsByName(dto.getName()))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateException("Promo code with this name already exists"));
                    }
                    PromoCode promoCode = mapDtoToEntity(dto);
                    return promoCodeRepository.save(promoCode);
                });
    }

    /**
     * @param id
     * @param dto
     * @return
     */
    @Override
    public Mono<PromoCode> updatePromotion(Long id, PromoCodeDTO dto) {
        return null;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Mono<PromoCode> getPromotionById(Long id) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public Flux<PromoCode> getAllPromotions() {
        return null;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Mono<Void> deactivatePromotion(Long id) {
        return null;
    }

    @Override
    public Mono<Boolean> validatePromotion(PromoCodeDTO dto) {
        return Mono.defer(() -> {
            List<String> errors = new ArrayList<>();

            // Basic validations
            if (dto.getExpiryDate().isBefore(dto.getStartDate())) {
                errors.add("Expiry date must be after start date");
            }

            if (dto.getIsPercentage() && (dto.getMaxDiscountAmount() == null || dto.getMaxDiscountAmount() <= 0)) {
                errors.add("Max discount amount is required for percentage-based discounts");
            }

           // EV ride type validations
if ("EV".equals(dto.getRideType()) && !dto.getVehicleTypes().stream()
        .allMatch(type -> type.name().startsWith("EV_"))) {
    errors.add("Only EV vehicle types allowed for EV ride type");
}
            // Time window validations
            if ((dto.getValidTimeWindowStart() != null && dto.getValidTimeWindowEnd() == null) ||
                    (dto.getValidTimeWindowStart() == null && dto.getValidTimeWindowEnd() != null)) {
                errors.add("Both start and end time windows must be specified");
            }

            // Bundle validations
            if (dto.getMinimumRides() != null && dto.getMinimumRides() > 0 && dto.getBookingCondition() == null) {
                errors.add("Booking condition must be specified for ride bundles");
            }

            // Driver wallet validations
            if (dto.getAddToDriverWallet() && !validationService.validateDriverWalletRules(dto)) {
                errors.add("Invalid driver wallet configuration");
            }

            return Mono.just(errors.isEmpty())
                    .doOnNext(valid -> {
                        if (!valid) {
                            throw new ValidationException(String.join(", ", errors));
                        }
                    });
        });
    }

    private PromoCode mapDtoToEntity(PromoCodeDTO dto) {
        PromoCode promoCode = new PromoCode();
        BeanUtils.copyProperties(dto, promoCode);
        promoCode.setType(PromotionType.PROMO_CODE);
        promoCode.setIsActive(true);
        promoCode.setCreatedAt(LocalDateTime.now());
        promoCode.setUpdatedAt(LocalDateTime.now());
        return promoCode;
    }

    public Mono<ValidationResponse> validatePromoCode(String code, ValidationRequest request) {
        return null;
    }
}
