package es.codeurjc.global_mart.dto.Product;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.global_mart.model.Product;


@Mapper (componentModel = "spring")
public interface ProductMapper{
    ProductDTO toProductDTO(Product product);

    List<ProductDTO> toProductsDTO(Collection<Product> products);

    Product toProduct(ProductDTO productDTO);

    List<Product> toProducts(List<ProductDTO> productDTOs);

    
}