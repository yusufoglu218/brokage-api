package com.brokerage.mapper;

import com.brokerage.dto.AssetMoneyResponseDto;
import com.brokerage.entity.Asset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssetMapper {

    @Mapping(source = "size", target = "totalAmount")
    @Mapping(source = "usableSize", target = "usableAmount")
    AssetMoneyResponseDto toMoneyDto(Asset asset);

}
