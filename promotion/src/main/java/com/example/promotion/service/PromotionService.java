package com.example.promotion.service;

import com.example.promotion.dto.BasePromotionDTO;
import com.example.promotion.model.BasePromotion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PromotionService<T extends BasePromotion, D extends BasePromotionDTO> {

    Mono<T> createPromotion(D dto);
    Mono<T> updatePromotion(Long id, D dto);
    Mono<T> getPromotionById(Long id);
    Flux<T> getAllPromotions();
    Mono<Void> deactivatePromotion(Long id);
    Mono<Boolean> validatePromotion(D dto);
}
