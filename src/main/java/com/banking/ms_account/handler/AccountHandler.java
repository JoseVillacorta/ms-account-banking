package com.banking.ms_account.handler;

import com.banking.ms_account.dto.ApiResponse;
import com.banking.ms_account.dto.TransferRequest;
import com.banking.ms_account.entities.Account;
import com.banking.ms_account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class AccountHandler {

        private final AccountService accountService;

        // Obtener todas las cuentas
        public Mono<ServerResponse> findAll(ServerRequest request) {
                return accountService.findAll()
                                .collectList()
                                .flatMap(accounts -> ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(ApiResponse.success(accounts, "Ok", HttpStatus.OK.value())));
        }

        // Obtener cuenta especifica por ID
        public Mono<ServerResponse> findById(ServerRequest request) {
                Long id = Long.valueOf(request.pathVariable("id"));
                return accountService.findById(id)
                                .flatMap(a -> ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(ApiResponse.success(a, "Ok", HttpStatus.OK.value())))
                                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                                                .bodyValue(ApiResponse.error("Account not found",
                                                                HttpStatus.NOT_FOUND.value())));
        }

        // Traer las cuentas de un cliente ("Mis cuentas")
        public Mono<ServerResponse> findByCustomerId(ServerRequest request) {
                Long customerId = Long.valueOf(request.pathVariable("customerId"));
                return accountService.findByCustomerId(customerId)
                                .collectList()
                                .flatMap(accounts -> ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(ApiResponse.success(accounts, "Ok", HttpStatus.OK.value())));
        }

        // Crear cuenta nueva
        public Mono<ServerResponse> create(ServerRequest request) {
                return request.bodyToMono(Account.class)
                                .flatMap(accountService::create)
                                .flatMap(a -> ServerResponse
                                                .created(URI.create("/accounts/" + a.getId()))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(ApiResponse.success(a, "Account created successfully",
                                                                HttpStatus.CREATED.value())))
                                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                                                .bodyValue(ApiResponse.error(e.getMessage(),
                                                                HttpStatus.BAD_REQUEST.value())));
        }

        // Ejecutar transferencia mediante stored procedure
        public Mono<ServerResponse> transfer(ServerRequest request) {
                return request.bodyToMono(TransferRequest.class)
                                .flatMap(req -> accountService.transfer(req.getFromAccountId(), req.getToAccountId(),
                                                req.getAmount()))
                                .then(ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(ApiResponse.success(null,
                                                                "Transferencia realizada exitosamente (ACID)",
                                                                HttpStatus.OK.value())))
                                // Si el stored Procedure escupe un RAISE EXCEPTION lo atajamos aqui
                                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                                                .bodyValue(ApiResponse.error(
                                                                "Error en transferencia: " + e.getMessage(),
                                                                HttpStatus.BAD_REQUEST.value())));
        }

}
