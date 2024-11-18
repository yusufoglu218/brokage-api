package com.brokerage.service;

import com.brokerage.constant.AssetConstants;
import com.brokerage.entity.Asset;
import com.brokerage.entity.Order;
import com.brokerage.entity.OrderSide;
import com.brokerage.entity.OrderStatus;
import com.brokerage.repository.AssetRepository;
import com.brokerage.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AdminService adminService;

    private Order pendingOrder;
    private Asset tryAsset;
    private Asset actualAsset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        pendingOrder = Order.builder()
                .id(1L)
                .customerId(1L)
                .assetName("AAPL")
                .side(OrderSide.BUY)
                .size(BigDecimal.valueOf(100))
                .price(BigDecimal.valueOf(150))
                .status(OrderStatus.PENDING)
                .build();

        tryAsset = Asset.builder()
                .customerId(1L)
                .assetName("TRY")
                .size(BigDecimal.valueOf(10000))
                .usableSize(BigDecimal.valueOf(10000))
                .build();

        actualAsset = Asset.builder()
                .customerId(1L)
                .assetName("AAPL")
                .size(BigDecimal.valueOf(200))
                .usableSize(BigDecimal.valueOf(200))
                .build();
    }

    @Test
    void testMatchPendingOrders_SuccessBuyOrder() {
        List<Order> pendingOrders = new ArrayList<>();
        pendingOrders.add(pendingOrder);

        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(pendingOrders);
        when(assetRepository.findByCustomerIdAndAssetName(1L, AssetConstants.TRY)).thenReturn(Optional.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(1L, "AAPL")).thenReturn(Optional.of(actualAsset));
        when(assetRepository.save(any(Asset.class))).thenReturn(tryAsset);
        when(orderRepository.save(any(Order.class))).thenReturn(pendingOrder);

        List<Order> result = adminService.matchPendingOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(OrderStatus.MATCHED, result.get(0).getStatus());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(assetRepository, times(2)).save(any(Asset.class));
    }

    @Test
    void testMatchPendingOrders_SuccessSellOrder() {
        pendingOrder.setSide(OrderSide.SELL);
        pendingOrder.setAssetName("AAPL");

        List<Order> pendingOrders = new ArrayList<>();
        pendingOrders.add(pendingOrder);

        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(pendingOrders);
        when(assetRepository.findByCustomerIdAndAssetName(1L, AssetConstants.TRY)).thenReturn(Optional.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(1L, "AAPL")).thenReturn(Optional.of(actualAsset));
        when(assetRepository.save(any(Asset.class))).thenReturn(tryAsset);
        when(orderRepository.save(any(Order.class))).thenReturn(pendingOrder);

        List<Order> result = adminService.matchPendingOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(OrderStatus.MATCHED, result.get(0).getStatus());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(assetRepository, times(2)).save(any(Asset.class));
    }

    @Test
    void testMatchPendingOrders_NoPendingOrders() {
        List<Order> pendingOrders = new ArrayList<>();
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(pendingOrders);

        List<Order> result = adminService.matchPendingOrders();

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(orderRepository, never()).save(any(Order.class));
        verify(assetRepository, never()).save(any(Asset.class));
    }

}
