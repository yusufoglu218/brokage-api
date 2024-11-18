package com.brokerage.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetMoneyRequestDto {

    @NotNull(message = "Customer ID is required.")
    private Long customerId;

    @NotNull(message = "Amount is required.")
    @Positive(message = "Amount must be a positive value.")
    private BigDecimal amount;
}