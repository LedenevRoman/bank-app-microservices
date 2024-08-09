package com.training.rledenev.mapper;

import com.training.rledenev.dto.ProductDto;
import com.training.rledenev.entity.Product;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Named("toProductDto")
    ProductDto mapToDto(Product product);

    @IterableMapping(qualifiedByName = "toProductDto")
    List<ProductDto> mapToListDto(List<Product> products);
}
