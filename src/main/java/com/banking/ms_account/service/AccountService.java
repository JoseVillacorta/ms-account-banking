package com.banking.ms_account.service;

import com.banking.ms_account.entities.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {

    Flux<Account> findAll();

    Mono<Account> findById(Long id);

    Mono<Account> findByAccountNumber(String accountNumber);

    Flux<Account> findByCustomerId(Long customerId);

    // Para crear la cuenta, requerimos verificar si el cliente existe
    Mono<Account> create(Account account);

    Mono<Account> update(Long id, Account account);

    Mono<Void> delete(Long id);

    Mono<Void> transfer(Long fromAccountId, Long toAccountId, java.math.BigDecimal amount);

}
