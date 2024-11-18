package com.brokerage.service;

import com.brokerage.constant.AssetConstants;
import com.brokerage.entity.Asset;
import com.brokerage.entity.Order;
import com.brokerage.entity.OrderSide;
import com.brokerage.entity.OrderStatus;
import com.brokerage.exception.RecordNotFoundException;
import com.brokerage.mapper.OrderMapper;
import com.brokerage.repository.AssetRepository;
import com.brokerage.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final OrderRepository orderRepository;

    private final AssetRepository assetRepository;

    private final OrderMapper orderMapper;

    @Autowired
    public AdminService(OrderRepository orderRepository, AssetRepository assetRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public List<Order> matchPendingOrders() {
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);

        for (Order pendingOrder : pendingOrders) {
            processMatchedOrders(pendingOrder);
            pendingOrder.setStatus(OrderStatus.MATCHED);
            orderRepository.save(pendingOrder);
        }

        return pendingOrders;
    }

    private void processMatchedOrders(Order order) {
        Asset assetTry = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), AssetConstants.TRY)
                .orElseThrow(() -> new RecordNotFoundException("TRY Asset not found for customer with ID: " + order.getCustomerId()));

        Optional<Asset> actualAssetOptional = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName());

        Asset actualAsset = actualAssetOptional.orElse(null);

        BigDecimal refundAmount = order.getSize().multiply(order.getPrice());

        if (order.getSide() == OrderSide.BUY) {
            assetTry.setSize(assetTry.getSize().subtract(refundAmount));
            if (actualAsset != null) {
                actualAsset.setSize(actualAsset.getSize().add(order.getSize()));
                actualAsset.setUsableSize(actualAsset.getUsableSize().add(order.getSize()));
            } else {
                actualAsset = orderMapper.toAsset(order);
            }
        } else if (order.getSide() == OrderSide.SELL) {
            assetTry.setSize(assetTry.getSize().add(refundAmount));
            assetTry.setUsableSize(assetTry.getUsableSize().add(refundAmount));

            if (actualAsset == null) {
                throw new RecordNotFoundException("Asset not found for customer with ID: " + order.getCustomerId() + " and asset name: " + order.getAssetName());
            }

            actualAsset.setSize(actualAsset.getSize().subtract(order.getSize()));
        }

        assetRepository.save(assetTry);
        if(actualAsset != null) {
            assetRepository.save(actualAsset);
        }
    }

}
