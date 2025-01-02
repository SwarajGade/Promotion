package com.example.promotion.repository;

import com.example.promotion.model.Discount;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DiscountRepository extends ReactiveCrudRepository<Discount, Long> {


    Mono<Discount> findByName(String name);
    Mono<Boolean> existsByName(String name);
    Mono<Discount> findByNameAndIsActiveTrue(String name);

    Flux<Object> findByCode(String code);
}
