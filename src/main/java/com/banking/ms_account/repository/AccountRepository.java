package com.banking.ms_account.repository;

import com.banking.ms_account.entities.Account;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
    // Metodo para buscar si existe una cuenta por su número (Sirve para transferencias o pagos)
    Mono<Account> findByAccountNumber(String accountNumber);

    // Traer TODAS las cuentas que le pertenecen a un solo cliente (usando su customer_id)
    Flux<Account> findByCustomerId(Long customerId);

    @Modifying
    @Query("CALL sp_process_transfer(:fromAccountId, :toAccountId, :amount)")
    Mono<Void> executeTransfer(@Param("fromAccountId") Long fromAccountId,
                               @Param("toAccountId") Long toAccountId,
                               @Param("amount") BigDecimal amount);
}
