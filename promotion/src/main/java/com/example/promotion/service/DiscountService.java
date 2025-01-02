package com.example.promotion.service;

import com.example.promotion.dto.DiscountDTO;
import com.example.promotion.dto.ValidationRequest;
import com.example.promotion.dto.ValidationResponse;
import com.example.promotion.enums.PromotionType;
import com.example.promotion.enums.ValidationErrorType;
import com.example.promotion.exception.DuplicateException;
import com.example.promotion.model.Discount;
import com.example.promotion.repository.DiscountRepository;
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
public class DiscountService implements PromotionService<Discount, DiscountDTO> {

    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private ValidationService validationService;

    @Override
    public Mono<Discount> createPromotion(DiscountDTO dto) {
        return validatePromotion(dto)
                .filter(valid -> valid)
                .switchIfEmpty(Mono.error(new ValidationException("Validation failed")))
                .then(discountRepository.existsByName(dto.getName()))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateException("Discount with this name already exists"));
                    }
                    Discount discount = mapDtoToEntity(dto);
                    return discountRepository.save(discount);
                });
    }

    @Override
    public Mono<Discount> updatePromotion(Long id, DiscountDTO dto) {
        return discountRepository.findById(id)
                .switchIfEmpty(Mono.error(new ValidationException("Discount not found")))
                .flatMap(existingDiscount -> {
                    BeanUtils.copyProperties(dto, existingDiscount, "id", "createdAt");
                    existingDiscount.setUpdatedAt(LocalDateTime.now());
                    return discountRepository.save(existingDiscount);
                });
    }

    @Override
    public Mono<Discount> getPromotionById(Long id) {
        return discountRepository.findById(id)
                .switchIfEmpty(Mono.error(new ValidationException("Discount not found")));
    }

    @Override
    public Flux<Discount> getAllPromotions() {
        return discountRepository.findAll();
    }

    @Override
    public Mono<Void> deactivatePromotion(Long id) {
        return discountRepository.findById(id)
                .switchIfEmpty(Mono.error(new ValidationException("Discount not found")))
                .flatMap(discount -> {
                    discount.setIsActive(false);
                    return discountRepository.save(discount).then();
                });
    }

    @Override
    public Mono<Boolean> validatePromotion(DiscountDTO dto) {
        return Mono.defer(() -> {
            List<String> errors = new ArrayList<>();

            // Weather-based validations
            if (dto.getWeatherCondition() != null && dto.getGeographicArea() == null) {
                errors.add("Geographic area is required for weather-based discounts");
            }

            // Green surcharge validations
            if (Boolean.TRUE.equals(dto.getGreenSurchargeEnabled())) {
                if (dto.getSurchargeAmount() == null || dto.getSurchargeAmount() <= 0) {
                    errors.add("Surcharge amount must be specified and greater than zero");
                }
                if (dto.getDiscountValue() < dto.getSurchargeAmount()) {
                    errors.add("Discount value should be greater than or equal to surcharge amount");
                }
            }

            // Destination targeting validations
            if (dto.getDestinationTargeting() != null && dto.getDestinationTargeting().isEmpty()) {
                errors.add("Destination targeting list cannot be empty if specified");
            }

            // Time window validations
            if (dto.getTimeWindow() != null && dto.getTimeWindow() <= 0) {
                errors.add("Time window must be greater than zero");
            }

            // Loyalty points multiplier validations
            if (dto.getLoyaltyPointsMultiplier() != null && dto.getLoyaltyPointsMultiplier() < 1) {
                errors.add("Loyalty points multiplier must be at least 1");
            }

            return Mono.just(errors.isEmpty())
                    .doOnNext(valid -> {
                        if (!valid) {
                            throw new ValidationException(String.join(", ", errors));
                        }
                    });
        });
    }

    private Discount mapDtoToEntity(DiscountDTO dto) {
        Discount discount = new Discount();
        BeanUtils.copyProperties(dto, discount);
        discount.setType(PromotionType.DISCOUNT);
        discount.setIsActive(true);
        discount.setCreatedAt(LocalDateTime.now());
        discount.setUpdatedAt(LocalDateTime.now());
        return discount;
    }

    public Flux<ValidationResponse> validateDiscount(String code, ValidationRequest request) {
        return discountRepository.findByCode(code)
                .switchIfEmpty(Mono.error(new ValidationException("Discount code not found")))
                .cast(Discount.class)
                .flatMap(discount -> {
                    ValidationResponse response = new ValidationResponse();
                    response.setCode(code);

                    // Check if the discount is expired
                    if (LocalDateTime.now().isAfter(discount.getExpiryDate())) {
                        response.setValid(false);
                        response.setErrorType(ValidationErrorType.EXPIRED);
                        response.setMessage("Discount code is expired");
                        return Mono.just(response);
                    }

                    // Check if the payment mode is valid
                    if (!discount.getPaymentModes().contains(request.getPaymentMode())) {
                        response.setValid(false);
                        response.setErrorType(ValidationErrorType.INVALID_PAYMENT_MODE);
                        response.setMessage("Invalid payment mode for this discount");
                        return Mono.just(response);
                    }

                    // Check if the vehicle type is valid
                    if (!discount.getVehicleTypes().contains(request.getVehicleType())) {
                        response.setValid(false);
                        response.setErrorType(ValidationErrorType.INVALID_VEHICLE_TYPE);
                        response.setMessage("Invalid vehicle type for this discount");
                        return Mono.just(response);
                    }

                    // Check if the ride fare meets the minimum requirement
                    if (request.getRideFare() < discount.getMinRideFare()) {
                        response.setValid(false);
                        response.setErrorType(ValidationErrorType.MINIMUM_FARE_NOT_MET);
                        response.setMessage("Ride fare does not meet the minimum requirement for this discount");
                        return Mono.just(response);
                    }

                    // Check if the ride type is valid
                    if (!discount.getRideType().equals(request.getRideType())) {
                        response.setValid(false);
                        response.setErrorType(ValidationErrorType.INVALID_RIDE_TYPE);
                        response.setMessage("Invalid ride type for this discount");
                        return Mono.just(response);
                    }

                    // Check if the current time is within the valid time window
                    LocalDateTime now = LocalDateTime.now();
                    if (now.isBefore(LocalDateTime.parse(discount.getValidTimeWindowStart())) ||
                            now.isAfter(LocalDateTime.parse(discount.getValidTimeWindowEnd()))) {
                        response.setValid(false);
                        response.setErrorType(ValidationErrorType.INVALID_TIME_WINDOW);
                        response.setMessage("Current time is outside the valid time window for this discount");
                        return Mono.just(response);
                    }

                    // Check if the current day is valid
                    if (!discount.getValidDays().contains(now.getDayOfWeek().toString())) {
                        response.setValid(false);
                        response.setErrorType(ValidationErrorType.INVALID_DAY);
                        response.setMessage("Current day is not valid for this discount");
                        return Mono.just(response);
                    }

                    // Additional validations can be added here

                    // If all validations pass
                    response.setValid(true);
                    response.setMessage("Discount code is valid");
                    response.setDiscountAmount(calculateDiscountAmount(discount, request));
                    return Mono.just(response);
                });
    }

    private double calculateDiscountAmount(Discount discount, ValidationRequest request) {
        double discountAmount;
        if (discount.getIsPercentage()) {
            discountAmount = request.getRideFare() * (discount.getDiscountValue() / 100);
            if (discount.getMaxDiscountAmount() != null) {
                discountAmount = Math.min(discountAmount, discount.getMaxDiscountAmount());
            }
        } else {
            discountAmount = discount.getDiscountValue();
        }
        return discountAmount;
    }

}