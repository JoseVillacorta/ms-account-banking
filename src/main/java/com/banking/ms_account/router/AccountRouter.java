package com.banking.ms_account.router;

import com.banking.ms_account.handler.AccountHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AccountRouter {

    @Bean
    public RouterFunction<ServerResponse> accountRoutes(AccountHandler handler) {
        return route(
                GET("/api/v1/accounts"), handler::findAll)
                .andRoute(GET("/api/v1/accounts/{id}"), handler::findById)
                .andRoute(GET("/api/v1/accounts/customer/{customerId}"), handler::findByCustomerId)
                .andRoute(POST("/api/v1/accounts"), handler::create)
                .andRoute(POST("/api/v1/accounts/transfer"), handler::transfer);
    }

}
