package spring.EAVDemoPrice.service;

import spring.EAVDemoPrice.dto.ProductDto;
import spring.EAVDemoPrice.dto.ProductResponseDto;

import java.util.List;

public interface IProductService {
    List<ProductResponseDto> findAll();
    ProductResponseDto findProductById(Long id) throws Exception;
    ProductResponseDto addProduct(ProductDto productDto);
    ProductResponseDto updateProduct(Long id, ProductDto productDto) throws Exception;
}
