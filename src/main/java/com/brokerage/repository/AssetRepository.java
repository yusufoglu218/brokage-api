package com.brokerage.repository;

import com.brokerage.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByCustomerId(Long customerId);

    Optional<Asset> findByCustomerIdAndAssetName(Long customerId, String assetName);
}

