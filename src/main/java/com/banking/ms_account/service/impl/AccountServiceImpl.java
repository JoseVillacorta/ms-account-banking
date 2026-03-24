package com.banking.ms_account.service.impl;

import com.banking.ms_account.entities.Account;
import com.banking.ms_account.repository.AccountRepository;
import com.banking.ms_account.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final WebClient webClient;

    // Aqui inyectamos el repositorio y construimos un cliente apuntando al nombre
    // en Eureka
    public AccountServiceImpl(AccountRepository accountRepository, WebClient.Builder webClientBuilder) {
        this.accountRepository = accountRepository;
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Flux<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Mono<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public Mono<Account> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public Flux<Account> findByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    @Override
    public Mono<Account> create(Account account) {
        // Primero preguntar a customer si existe el cliente
        return webClient.get()
                .uri("http://ms-customer/api/v1/customers/" + account.getCustomerId())
                .retrieve()
                // Si el customer devuelve 404, webclientpolicy lanza un error
                // Lo atrapamos para dar un mensaje claro
                .onStatus(status -> status.value() == 404,
                        clientResponse -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Cliente no existe en el sistema padre")))
                .bodyToMono(Object.class)// Ignoramos el cuerpo de la respuesta, solo nos importa que existe (Cod 200)
                // Segundo es que si todo salio bien , guardamos la cuenta
                .flatMap(response -> {
                    account.setStatus("ACTIVE");
                    account.setCreatedAt(LocalDateTime.now());
                    account.setUpdatedAt(LocalDateTime.now());
                    return accountRepository.save(account);
                });
    }

    @Override
    public Mono<Account> update(Long id, Account account) {
        return accountRepository.findById(id)
                .flatMap(existingAccount -> {
                    existingAccount.setAccountType(account.getAccountType());
                    existingAccount.setStatus(account.getStatus());
                    existingAccount.setUpdatedAt(LocalDateTime.now());
                    return accountRepository.save(existingAccount);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return accountRepository.findById(id)
                .flatMap(accountRepository::delete);
    }

    @Override
    public Mono<Void> transfer(Long fromAccountId, Long toAccountId, java.math.BigDecimal amount) {
        return accountRepository.executeTransfer(fromAccountId, toAccountId, amount);
    }

}
