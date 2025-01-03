package com.example.promotion.controller.router;

import com.example.promotion.controller.PromotionController;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@EnableWebFlux
@Configuration
public class PromotionRouter {

    @SneakyThrows
    @Bean
    public RouterFunction<ServerResponse> promotionRoutes(PromotionController promotionController) {
        return route(POST("/api/promotions/promo-codes"), promotionController::createPromoCode)
                                .andRoute(POST("/api/promotions/coupons"), promotionController::createCoupon)
                                .andRoute(POST("/api/promotions/discounts"), promotionController::createDiscount)
                                .andRoute(GET("/api/promotions/validate/{type}/{code}"), promotionController::validatePromotion);

    }


}
