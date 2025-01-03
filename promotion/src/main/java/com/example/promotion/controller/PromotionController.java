package com.example.promotion.controller;

import com.example.promotion.dto.*;
import com.example.promotion.enums.PromotionType;
import com.example.promotion.service.CouponService;
import com.example.promotion.service.DiscountService;
import com.example.promotion.service.PromoCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class PromotionController {

    @Autowired
    private PromoCodeService promoCodeService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private DiscountService discountService;

    public Mono<ServerResponse> createPromoCode(ServerRequest request) {
        return request.bodyToMono(PromoCodeDTO.class)
                .flatMap(promoCodeService::createPromotion)
                .flatMap(promoCode -> ServerResponse.status(201).bodyValue(promoCode));
    }

    public Mono<ServerResponse> createCoupon(ServerRequest request) {
        return request.bodyToMono(CouponDTO.class)
                .flatMap(couponService::createPromotion)
                .flatMap(coupon -> ServerResponse.status(201).bodyValue(coupon));
    }

    public Mono<ServerResponse> createDiscount(ServerRequest request) {
        return request.bodyToMono(DiscountDTO.class)
                .flatMap(discountService::createPromotion)
                .flatMap(discount -> ServerResponse.status(201).bodyValue(discount));
    }

    public Mono<ServerResponse> validatePromotion(ServerRequest request) {
        String type = request.pathVariable("type");
        String code = request.pathVariable("code");
        Mono<ValidationRequest> validationRequest = request.bodyToMono(ValidationRequest.class);

        return Mono.justOrEmpty(PromotionType.valueOf(type))
                .flatMap(promotionType -> switch (promotionType) {
                    case PROMO_CODE -> promoCodeService.validatePromoCode(code, validationRequest.block()).flatMap(ServerResponse.ok()::bodyValue);
                    case COUPON -> couponService.validateCoupon(code, validationRequest.block()).flatMap(ServerResponse.ok()::bodyValue);
                    case DISCOUNT -> discountService.validateDiscount(code, validationRequest.block()).next().flatMap(ServerResponse.ok()::bodyValue);
                });
    }
}
