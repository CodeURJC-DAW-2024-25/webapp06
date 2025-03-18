package es.codeurjc.global_mart.dto;

import org.mapstruct.Mapper;

import es.codeurjc.global_mart.model.Product;


@Mapper (componentModel = "spring")
public interface ProductMapper{
    ProductDTO toProductDTO(Product product);
}