package com.brokerage.service;

import com.brokerage.dto.AssetMoneyRequestDto;
import com.brokerage.dto.AssetMoneyResponseDto;
import com.brokerage.entity.Asset;
import com.brokerage.exception.InsufficientFundsException;
import com.brokerage.exception.RecordNotFoundException;
import com.brokerage.mapper.AssetMapper;
import com.brokerage.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssetServiceTest {

    @InjectMocks
    private AssetService assetService;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetMapper assetMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListAssets_Success() {
        Long customerId = 1L;

        when(assetRepository.findByCustomerId(customerId))
                .thenReturn(List.of(
                        Asset.builder()
                                .customerId(customerId)
                                .assetName("AAPL")
                                .size(BigDecimal.valueOf(50))
                                .usableSize(BigDecimal.valueOf(50))
                                .build(),
                        Asset.builder()
                                .customerId(customerId)
                                .assetName("GOOGL")
                                .size(BigDecimal.valueOf(30))
                                .usableSize(BigDecimal.valueOf(30))
                                .build()
                ));

        List<Asset> result = assetService.listAssets(customerId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(assetRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    void testDepositMoney_Success() {
        AssetMoneyRequestDto requestDto = AssetMoneyRequestDto.builder()
                .customerId(1L)
                .amount(BigDecimal.valueOf(1000))
                .build();

        Asset tryAsset = Asset.builder()
                .customerId(1L)
                .assetName("TRY")
                .size(BigDecimal.valueOf(5000))
                .usableSize(BigDecimal.valueOf(5000))
                .build();

        Asset updatedAsset = Asset.builder()
                .customerId(1L)
                .assetName("TRY")
                .size(BigDecimal.valueOf(6000))
                .usableSize(BigDecimal.valueOf(6000))
                .build();

        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.of(tryAsset));
        when(assetRepository.save(any(Asset.class))).thenReturn(updatedAsset);
        when(assetMapper.toMoneyDto(updatedAsset)).thenReturn(
                AssetMoneyResponseDto.builder()
                        .assetName("TRY")
                        .totalAmount(BigDecimal.valueOf(6000))
                        .usableAmount(BigDecimal.valueOf(6000))
                        .build()
        );

        AssetMoneyResponseDto responseDto = assetService.depositMoney(requestDto);

        assertNotNull(responseDto);
        assertEquals(BigDecimal.valueOf(6000), responseDto.getTotalAmount());
        assertEquals(BigDecimal.valueOf(6000), responseDto.getUsableAmount());
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    void testDepositMoney_RecordNotFound() {
        AssetMoneyRequestDto requestDto = AssetMoneyRequestDto.builder()
                .customerId(1L)
                .amount(BigDecimal.valueOf(1000))
                .build();

        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> assetService.depositMoney(requestDto));
    }

    @Test
    void testWithdrawMoney_Success() {
        AssetMoneyRequestDto requestDto = AssetMoneyRequestDto.builder()
                .customerId(1L)
                .amount(BigDecimal.valueOf(1000))
                .build();

        Asset tryAsset = Asset.builder()
                .customerId(1L)
                .assetName("TRY")
                .size(BigDecimal.valueOf(5000))
                .usableSize(BigDecimal.valueOf(2000))
                .build();

        Asset updatedAsset = Asset.builder()
                .customerId(1L)
                .assetName("TRY")
                .size(BigDecimal.valueOf(4000))
                .usableSize(BigDecimal.valueOf(1000))
                .build();

        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.of(tryAsset));
        when(assetRepository.save(any(Asset.class))).thenReturn(updatedAsset);
        when(assetMapper.toMoneyDto(updatedAsset)).thenReturn(
                AssetMoneyResponseDto.builder()
                        .assetName("TRY")
                        .totalAmount(BigDecimal.valueOf(1000))
                        .usableAmount(BigDecimal.valueOf(1000))
                        .build()
        );

        AssetMoneyResponseDto responseDto = assetService.withdrawMoney(requestDto);

        assertNotNull(responseDto);
        assertEquals(BigDecimal.valueOf(1000), responseDto.getUsableAmount());
        assertEquals(BigDecimal.valueOf(1000), responseDto.getTotalAmount());
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    void testWithdrawMoney_InsufficientFunds() {
        AssetMoneyRequestDto requestDto = AssetMoneyRequestDto.builder()
                .customerId(1L)
                .amount(BigDecimal.valueOf(3000))
                .build();

        Asset tryAsset = Asset.builder()
                .customerId(1L)
                .assetName("TRY")
                .size(BigDecimal.valueOf(5000))
                .usableSize(BigDecimal.valueOf(1000))
                .build();

        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        assertThrows(InsufficientFundsException.class, () -> assetService.withdrawMoney(requestDto));
    }

    @Test
    void testWithdrawMoney_RecordNotFound() {
        AssetMoneyRequestDto requestDto = AssetMoneyRequestDto.builder()
                .customerId(1L)
                .amount(BigDecimal.valueOf(1000))
                .build();

        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY"))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> assetService.withdrawMoney(requestDto));
    }
}
