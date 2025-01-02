package com.example.promotion.controller;

import com.example.promotion.dto.*;
import com.example.promotion.enums.PromotionType;
import com.example.promotion.model.Coupon;
import com.example.promotion.model.Discount;
import com.example.promotion.model.PromoCode;
import com.example.promotion.service.CouponService;
import com.example.promotion.service.DiscountService;
import com.example.promotion.service.PromoCodeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.example.promotion.dto.ValidationResponse;
import com.example.promotion.dto.ValidationRequest;


@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    @Autowired
    private  PromoCodeService promoCodeService;
    @Autowired
    private  CouponService couponService;
    @Autowired
    private  DiscountService discountService;

    @PostMapping("/promo-codes")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PromoCode> createPromoCode(@Valid @RequestBody PromoCodeDTO dto) {
        return promoCodeService.createPromotion(dto);
    }

    @PostMapping("/coupons")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Coupon> createCoupon(@Valid @RequestBody CouponDTO dto) {
        return couponService.createPromotion(dto);
    }

    @PostMapping("/discounts")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Discount> createDiscount(@Valid @RequestBody DiscountDTO dto) {
        return discountService.createPromotion(dto);
    }

    @GetMapping("/validate/{type}/{code}")
    public Mono<ValidationResponse> validatePromotion(
            @PathVariable PromotionType type,
            @PathVariable String code,
            @RequestBody ValidationRequest request) {
        return switch (type) {
            case PROMO_CODE -> promoCodeService.validatePromoCode(code, request);
            case COUPON -> couponService.validateCoupon(code, request);
            case DISCOUNT -> discountService.validateDiscount(code, request).next();
          //  case DISCOUNT -> null;
        };
    }
}
