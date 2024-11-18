package com.brokerage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetMoneyResponseDto {
    private String assetName;
    private BigDecimal totalAmount;
    private BigDecimal usableAmount;
}