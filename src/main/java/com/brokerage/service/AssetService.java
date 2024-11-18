package com.brokerage.service;

import com.brokerage.constant.AssetConstants;
import com.brokerage.dto.AssetMoneyRequestDto;
import com.brokerage.dto.AssetMoneyResponseDto;
import com.brokerage.entity.Asset;
import com.brokerage.exception.InsufficientFundsException;
import com.brokerage.exception.RecordNotFoundException;
import com.brokerage.mapper.AssetMapper;
import com.brokerage.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;

    @Autowired
    public AssetService(AssetRepository assetRepository, AssetMapper assetMapper) {
        this.assetRepository = assetRepository;
        this.assetMapper = assetMapper;
    }

    public List<Asset> listAssets(Long customerId) {
        return assetRepository.findByCustomerId(customerId);
    }

    public AssetMoneyResponseDto depositMoney(AssetMoneyRequestDto assetMoneyRequestDto) {
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(assetMoneyRequestDto.getCustomerId(), AssetConstants.TRY)
                .orElseThrow(() -> new RecordNotFoundException("TRY Asset not found"));

        BigDecimal amountDeposit = assetMoneyRequestDto.getAmount();

        tryAsset.setSize(tryAsset.getSize().add(amountDeposit));
        tryAsset.setUsableSize(tryAsset.getUsableSize().add(amountDeposit));
        Asset updatedAsset = assetRepository.save(tryAsset);
        return assetMapper.toMoneyDto(updatedAsset);
    }

    public AssetMoneyResponseDto withdrawMoney(AssetMoneyRequestDto assetMoneyRequestDto) {
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(assetMoneyRequestDto.getCustomerId(), AssetConstants.TRY)
                .orElseThrow(() -> new RecordNotFoundException("TRY Asset not found"));

        BigDecimal amountToWithdraw = assetMoneyRequestDto.getAmount();

        if (tryAsset.getUsableSize().compareTo(amountToWithdraw) < 0) {
            throw new InsufficientFundsException("Insufficient balance for withdrawal.");
        }

        tryAsset.setSize(tryAsset.getSize().subtract(amountToWithdraw));
        tryAsset.setUsableSize(tryAsset.getUsableSize().subtract(amountToWithdraw));
        Asset updatedAsset = assetRepository.save(tryAsset);
        return assetMapper.toMoneyDto(updatedAsset);
    }
}
