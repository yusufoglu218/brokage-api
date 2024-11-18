package com.brokerage.mapper;

import com.brokerage.dto.OrderRequestDto;
import com.brokerage.dto.OrderResponseDto;
import com.brokerage.entity.Asset;
import com.brokerage.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createDate")
    @Mapping(ignore = true, target = "status")
    Order toEntity(OrderRequestDto orderRequestDto);

    OrderResponseDto toResponseDto(Order order);

    @Mapping(ignore = true, target = "id")
    @Mapping(source = "size", target = "usableSize")
    Asset toAsset(Order order);

}
