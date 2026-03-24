package com.banking.ms_account.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("accounts")
public class Account {

    @Id
    private Long id;

    private Long customerId;
    private String accountNumber;
    private String accountType; // AHORRO, CORRIENTE
    private BigDecimal balance; // BigDecimal es obligatorio en bancos reales para evitar perder dinero por decimales
    private String currency; // USD, PEN
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
