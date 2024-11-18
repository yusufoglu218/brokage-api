package com.brokerage.dto;

import com.brokerage.entity.OrderSide;
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
public class OrderRequestDto {

    @NotNull(message = "Customer ID is required.")
    private Long customerId;

    @NotNull(message = "Asset Name is required.")
    private String assetName;

    @NotNull(message = "Order Side is required.")
    private OrderSide side;

    @NotNull(message = "Size is required.")
    @Positive(message = "Size must be a positive value.")
    private BigDecimal size;

    @NotNull(message = "Price is required.")
    @Positive(message = "Price must be a positive value.")
    private BigDecimal price;
}