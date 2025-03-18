package es.codeurjc.global_mart.dto;

import org.mapstruct.Mapper;

import es.codeurjc.global_mart.model.Order;
@Mapper (componentModel = "spring")
public interface OrderMapper {
    OrderDTO toOrderDTO(Order order);
}
