package com.brokerage.controller;

import com.brokerage.dto.AssetMoneyRequestDto;
import com.brokerage.dto.AssetMoneyResponseDto;
import com.brokerage.entity.Asset;
import com.brokerage.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
@Tag(name = "Assets", description = "Endpoints for managing assets")
public class AssetController {

    @Autowired
    private final AssetService assetService;

    @Autowired
    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    @Operation(summary = "List assets", description = "List assets by customer ID")
    public List<Asset> listAssets(@RequestParam Long customerId) {
        return assetService.listAssets(customerId);
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit money", description = "Deposit TRY for a given customer and amount and return current TRY asset")
    public AssetMoneyResponseDto depositMoney(@Valid @RequestBody AssetMoneyRequestDto assetMoneyRequestDto) {
        return assetService.depositMoney(assetMoneyRequestDto);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw money", description = "Withdraw TRY for a given customer and amount and return current TRY asset")
    public AssetMoneyResponseDto withdrawMoney(@Valid @RequestBody AssetMoneyRequestDto assetMoneyRequestDto) {
        return assetService.withdrawMoney(assetMoneyRequestDto);
    }

}
