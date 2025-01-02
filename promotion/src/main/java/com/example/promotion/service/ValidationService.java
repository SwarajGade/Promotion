package com.example.promotion.service;

import com.example.promotion.dto.PromoCodeDTO;
import com.example.promotion.dto.ValidationRequest;
import com.example.promotion.dto.ValidationResponse;
import com.example.promotion.enums.ValidationErrorType;
import com.example.promotion.model.Coupon;
import com.example.promotion.model.PromoCode;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Validated
public class ValidationService {

    public Mono<ValidationResponse> validatePromoCode(PromoCode promoCode, ValidationRequest request) {
        return Mono.just(new ValidationResponse())
                .flatMap(response -> validateBasicCriteria(promoCode, request, response))
                .flatMap(response -> validateSpecificCriteria(promoCode, request, response))
                .flatMap(response -> calculateDiscount(promoCode, request, response));
    }

    private Mono<ValidationResponse> validateBasicCriteria(PromoCode promoCode, ValidationRequest request, ValidationResponse response) {
        return Mono.fromCallable(() -> {
            if (LocalDateTime.now().isAfter(promoCode.getExpiryDate())) {
                response.setErrorType(ValidationErrorType.EXPIRED);
                return response;
            }

            if (!promoCode.getPaymentModes().contains(request.getPaymentMode())) {
                response.setErrorType(ValidationErrorType.INVALID_PAYMENT_MODE);
                return response;
            }

            if (!promoCode.getVehicleTypes().contains(request.getVehicleType())) {
                response.setErrorType(ValidationErrorType.INVALID_VEHICLE_TYPE);
                return response;
            }

            if (request.getRideFare() < promoCode.getMinRideFare()) {
                response.setErrorType(ValidationErrorType.MINIMUM_FARE_NOT_MET);
                return response;
            }

            response.setValid(true);
            return response;
        });
    }

    private Mono<ValidationResponse> validateSpecificCriteria(PromoCode promoCode, ValidationRequest request, ValidationResponse response) {
        return Mono.fromCallable(() -> {
            if (!response.isValid()) {
                return response;
            }

            // Validate time windows if specified
            if (promoCode.getValidTimeWindowStart() != null &&
                    !isWithinTimeWindow(request.getRideTime(),
                            promoCode.getValidTimeWindowStart(),
                            promoCode.getValidTimeWindowEnd())) {
                response.setErrorType(ValidationErrorType.INVALID_TIME_WINDOW);
                response.setValid(false);
                return response;
            }

            // Validate areas if specified
            if (promoCode.getValidAreas() != null && !promoCode.getValidAreas().isEmpty() &&
                    !promoCode.getValidAreas().contains(request.getSourceArea()) &&
                    !promoCode.getValidAreas().contains(request.getDestinationArea())) {
                response.setErrorType(ValidationErrorType.INVALID_AREA);
                response.setValid(false);
                return response;
            }

            // Validate ride type
            if (promoCode.getRideType() != null &&
                    !promoCode.getRideType().equals(request.getRideType())) {
                response.setErrorType(ValidationErrorType.INVALID_RIDE_TYPE);
                response.setValid(false);
                return response;
            }

            // Validate consecutive rides if required
            if (promoCode.getMinimumRides() != null &&
                    request.getConsecutiveRides() < promoCode.getMinimumRides()) {
                response.setErrorType(ValidationErrorType.INSUFFICIENT_CONSECUTIVE_RIDES);
                response.setValid(false);
                return response;
            }

            return response;
        });
    }

    private Mono<ValidationResponse> calculateDiscount(PromoCode promoCode, ValidationRequest request, ValidationResponse response) {
        return Mono.fromCallable(() -> {
            if (!response.isValid()) {
                response.setDiscountAmount(0.0);
                return response;
            }

            double discount;
            if (promoCode.getIsPercentage()) {
                discount = request.getRideFare() * (promoCode.getDiscountValue() / 100);
                if (promoCode.getMaxDiscountAmount() != null) {
                    discount = Math.min(discount, promoCode.getMaxDiscountAmount());
                }
            } else {
                discount = promoCode.getDiscountValue();
            }

            response.setDiscountAmount(discount);
            response.setMessage("Promotion applied successfully");
            return response;
        });
    }

    private boolean isWithinTimeWindow(LocalDateTime rideTime, String startWindow, String endWindow) {
        LocalTime time = rideTime.toLocalTime();
        LocalTime start = LocalTime.parse(startWindow);
        LocalTime end = LocalTime.parse(endWindow);

        if (end.isBefore(start)) {
            // Handles overnight windows (e.g., 22:00 - 05:00)
            return !time.isAfter(end) || !time.isBefore(start);
        }

        return !time.isBefore(start) && !time.isAfter(end);
    }

    public boolean validateDriverWalletRules(PromoCodeDTO dto) {
        // Implement your validation logic here
        return true;
    }

    public boolean validateCouponRules(Coupon coupon, ValidationRequest request) {
        // Implement your validation logic here
        return true;
    }
}