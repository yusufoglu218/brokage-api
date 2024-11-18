package com.brokerage.dto;

import com.brokerage.entity.OrderSide;
import com.brokerage.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long customerId;
    private String assetName;
    private OrderSide side;
    private BigDecimal size;
    private BigDecimal price;
    private OrderStatus status;
}