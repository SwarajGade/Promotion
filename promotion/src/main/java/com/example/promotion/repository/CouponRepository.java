package com.example.promotion.repository;

import com.example.promotion.model.Coupon;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CouponRepository  extends ReactiveCrudRepository<Coupon, Long> {
    Mono<Coupon> findByName(String name);
    Mono<Boolean> existsByName(String name);
    Mono<Coupon> findByNameAndIsActiveTrue(String name);
    Mono<Coupon> findByCode(String code);



}
