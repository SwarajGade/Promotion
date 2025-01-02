package com.example.promotion.repository;

import com.example.promotion.model.PromoCode;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface PromoCodeRepository extends ReactiveCrudRepository<PromoCode, Long> {

    Mono<Boolean> existsByName(String name);

    Mono<PromoCode> findByNameAndIsActiveTrue(String name);

    @Query("SELECT * FROM promo_codes p WHERE p.is_active = true AND p.start_date <= :now AND p.expiry_date >= :now")
    Flux<PromoCode> findAllActivePromoCodes(LocalDateTime now);

    @Query("SELECT * FROM promo_codes p WHERE p.is_active = true AND :vehicleType = ANY(p.vehicle_types) AND p.start_date <= :now AND p.expiry_date >= :now")
    Flux<PromoCode> findActivePromoCodesByVehicleType(String vehicleType, LocalDateTime now);

    @Query("SELECT * FROM promo_codes p WHERE p.is_active = true AND :paymentMode = ANY(p.payment_modes) AND p.start_date <= :now AND p.expiry_date >= :now")
    Flux<PromoCode> findActivePromoCodesByPaymentMode(String paymentMode, LocalDateTime now);

    @Query("UPDATE promo_codes SET is_active = false WHERE id = :id")
    Mono<Void> deactivatePromoCode(Long id);
}
