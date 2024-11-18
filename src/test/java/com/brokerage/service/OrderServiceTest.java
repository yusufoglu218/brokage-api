package com.brokerage.service;

import com.brokerage.constant.AssetConstants;
import com.brokerage.dto.OrderRequestDto;
import com.brokerage.dto.OrderResponseDto;
import com.brokerage.entity.Asset;
import com.brokerage.entity.Order;
import com.brokerage.entity.OrderSide;
import com.brokerage.entity.OrderStatus;
import com.brokerage.exception.InsufficientFundsException;
import com.brokerage.exception.RecordNotFoundException;
import com.brokerage.mapper.OrderMapper;
import com.brokerage.repository.AssetRepository;
import com.brokerage.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder_BuyOrder_Success() {
        OrderRequestDto requestDto = OrderRequestDto.builder()
                .customerId(1L)
                .assetName("AAPL")
                .side(OrderSide.BUY)
                .size(BigDecimal.valueOf(10))
                .price(BigDecimal.valueOf(150))
                .build();

        Order order = Order.builder()
                .customerId(1L)
                .assetName("AAPL")
                .side(OrderSide.BUY)
                .size(BigDecimal.valueOf(10))
                .price(BigDecimal.valueOf(150))
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();

        when(orderMapper.toEntity(requestDto)).thenReturn(order);
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.of(Asset.builder()
                        .customerId(1L)
                        .assetName("TRY")
                        .size(BigDecimal.valueOf(5000))
                        .usableSize(BigDecimal.valueOf(5000))
                        .build()));

        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.createOrder(requestDto);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(assetRepository, times(1)).save(any(Asset.class));
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testCreateOrder_Sell_Success() {
        OrderRequestDto requestDto = OrderRequestDto.builder()
                .customerId(1L)
                .assetName("AAPL")
                .side(OrderSide.SELL)
                .size(BigDecimal.valueOf(10))
                .price(BigDecimal.valueOf(150))
                .build();

        Order order = Order.builder()
                .customerId(1L)
                .assetName("AAPL")
                .side(OrderSide.SELL)
                .size(BigDecimal.valueOf(10))
                .price(BigDecimal.valueOf(150))
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();

        when(orderMapper.toEntity(requestDto)).thenReturn(order);
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.of(Asset.builder()
                        .customerId(1L)
                        .assetName("TRY")
                        .size(BigDecimal.valueOf(5000))
                        .usableSize(BigDecimal.valueOf(5000))
                        .build()));
        when(assetRepository.findByCustomerIdAndAssetName(1L, "AAPL"))
                .thenReturn(Optional.of(Asset.builder()
                        .customerId(1L)
                        .assetName("AAPL")
                        .size(BigDecimal.valueOf(5000))
                        .usableSize(BigDecimal.valueOf(5000))
                        .build()));

        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.createOrder(requestDto);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(assetRepository, times(1)).save(any(Asset.class));
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testCreateOrder_RecordNotFoundException_BuyOrder() {
        OrderRequestDto requestDto = OrderRequestDto.builder()
                .customerId(1L)
                .assetName("AAPL")
                .side(OrderSide.BUY)
                .size(BigDecimal.valueOf(10))
                .price(BigDecimal.valueOf(150))
                .build();

        when(orderMapper.toEntity(requestDto)).thenReturn(Order.builder().build());
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> orderService.createOrder(requestDto));
    }

    @Test
    void testCreateOrder_InsufficientFundsException_BuyOrder() {
        OrderRequestDto requestDto = OrderRequestDto.builder()
                .customerId(1L)
                .assetName("AAPL")
                .side(OrderSide.BUY)
                .size(BigDecimal.valueOf(10))
                .price(BigDecimal.valueOf(150))
                .build();

        when(orderMapper.toEntity(requestDto)).thenReturn(Order.builder()
                .customerId(1L).assetName("AAPL").side(OrderSide.BUY).size(BigDecimal.valueOf(100)).price(BigDecimal.valueOf(100)).build());
        when(assetRepository.findByCustomerIdAndAssetName(1L, AssetConstants.TRY))
                .thenReturn(Optional.of(Asset.builder()
                        .customerId(1L)
                        .assetName("TRY")
                        .size(BigDecimal.valueOf(5000))
                        .usableSize(BigDecimal.valueOf(5000))
                        .build()));

        assertThrows(InsufficientFundsException.class, () -> orderService.createOrder(requestDto));
    }

    @Test
    void testListOrders_Success() {
        Long customerId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        when(orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate))
                .thenReturn(List.of(Order.builder().build()));

        List<Order> result = orderService.listOrders(customerId, startDate, endDate);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(orderRepository, times(1)).findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate);
    }

    @Test
    void testCancelOrderBuy_Success() {
        Long orderId = 1L;

        Order order = Order.builder()
                .id(orderId)
                .customerId(1L)
                .assetName("AAPL")
                .side(OrderSide.BUY)
                .size(BigDecimal.valueOf(10))
                .price(BigDecimal.valueOf(150))
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.of(Asset.builder()
                        .customerId(1L)
                        .assetName("TRY")
                        .size(BigDecimal.valueOf(5000))
                        .usableSize(BigDecimal.valueOf(4500))
                        .build()));

        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponseDto(order)).thenReturn(OrderResponseDto.builder().build());

        OrderResponseDto result = orderService.cancelOrder(orderId);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELED, order.getStatus());
        verify(orderRepository, times(1)).save(order);
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    void testCancelOrderSell_Success() {
        Long orderId = 1L;

        Order order = Order.builder()
                .id(orderId)
                .customerId(1L)
                .assetName("AAPL")
                .side(OrderSide.SELL)
                .size(BigDecimal.valueOf(10))
                .price(BigDecimal.valueOf(150))
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.of(Asset.builder()
                        .customerId(1L)
                        .assetName("TRY")
                        .size(BigDecimal.valueOf(5000))
                        .usableSize(BigDecimal.valueOf(4500))
                        .build()));

        when(assetRepository.findByCustomerIdAndAssetName(1L, "AAPL"))
                .thenReturn(Optional.of(Asset.builder()
                        .customerId(1L)
                        .assetName("AAPL")
                        .size(BigDecimal.valueOf(5000))
                        .usableSize(BigDecimal.valueOf(4500))
                        .build()));

        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponseDto(order)).thenReturn(OrderResponseDto.builder().build());

        OrderResponseDto result = orderService.cancelOrder(orderId);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELED, order.getStatus());
        verify(orderRepository, times(1)).save(order);
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    void testCancelOrder_NonPendingOrder_Failure() {
        Long orderId = 1L;

        Order order = Order.builder()
                .id(orderId)
                .customerId(1L)
                .assetName("AAPL")
                .side(OrderSide.BUY)
                .size(BigDecimal.valueOf(10))
                .price(BigDecimal.valueOf(150))
                .status(OrderStatus.MATCHED)
                .createDate(LocalDateTime.now())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.cancelOrder(orderId));
    }

    @Test
    void testCancelOrder_OrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> orderService.cancelOrder(1L));
    }
}
