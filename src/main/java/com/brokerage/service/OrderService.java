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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final AssetRepository assetRepository;

    private final OrderMapper orderMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository, AssetRepository assetRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
        this.orderMapper = orderMapper;
    }

    public Order createOrder(OrderRequestDto orderRequestDto) {
        Order order = orderMapper.toEntity(orderRequestDto);

        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), AssetConstants.TRY)
                .orElseThrow(() -> new RecordNotFoundException("TRY Asset not found"));

        if (order.getSide() == OrderSide.BUY) {
            BigDecimal requiredAmount = order.getSize().multiply(order.getPrice());
            if (tryAsset.getUsableSize().compareTo(requiredAmount) < 0) {
                throw new InsufficientFundsException("Insufficient TRY balance.");
            }
            tryAsset.setUsableSize(tryAsset.getUsableSize().subtract(requiredAmount));
            assetRepository.save(tryAsset);
        } else if(order.getSide() == OrderSide.SELL) {
            Asset actualAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName())
                    .orElseThrow(() -> new RecordNotFoundException("Asset not found"));
            if (actualAsset.getUsableSize().compareTo(order.getSize()) < 0) {
                throw new InsufficientFundsException("Insufficient asset size.");
            }
            actualAsset.setUsableSize(actualAsset.getUsableSize().subtract(order.getSize()));
            assetRepository.save(actualAsset);
        }

        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now());

        return orderRepository.save(order);
    }

    public List<Order> listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate);
    }

    public OrderResponseDto cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RecordNotFoundException("Order not found."));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Only pending orders can be canceled.");
        }

        if (order.getSide() == OrderSide.BUY) {
            BigDecimal refundAmount = order.getSize().multiply(order.getPrice());
            Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), AssetConstants.TRY)
                    .orElseThrow(() -> new RecordNotFoundException("TRY Asset not found"));
            tryAsset.setUsableSize(tryAsset.getUsableSize().add(refundAmount));
            assetRepository.save(tryAsset);
        } else if(order.getSide() == OrderSide.SELL) {
            Asset actualAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName())
                    .orElseThrow(() -> new RecordNotFoundException("Asset not found"));
            actualAsset.setUsableSize(actualAsset.getUsableSize().add(order.getSize()));
            assetRepository.save(actualAsset);
        }

        order.setStatus(OrderStatus.CANCELED);
        Order canceledOrder = orderRepository.save(order);
        return orderMapper.toResponseDto(canceledOrder);
    }

}
