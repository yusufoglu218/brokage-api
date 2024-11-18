package com.brokerage.controller;

import com.brokerage.dto.OrderRequestDto;
import com.brokerage.dto.OrderResponseDto;
import com.brokerage.entity.Order;
import com.brokerage.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Endpoints for managing orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Create a new stock order for a customer.")
    public Order createOrder(@Valid @RequestBody OrderRequestDto orderRequestDto) {
        return orderService.createOrder(orderRequestDto);
    }

    @GetMapping
    @Operation(summary = "List orders", description = "List orders by given parameters.")
    public List<Order> listOrders(@RequestParam Long customerId,
                                  @RequestParam LocalDateTime startDate,
                                  @RequestParam LocalDateTime endDate) {
        return orderService.listOrders(customerId, startDate, endDate);
    }

    @PostMapping("{orderId}/cancel")
    @Operation(summary = "Cancel a pending order", description = "Cancel a pending order by orderId and return canceled order information")
    public OrderResponseDto cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }
}

