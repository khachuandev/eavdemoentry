package spring.EAVDemoPrice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.EAVDemoPrice.dto.ProductDto;
import spring.EAVDemoPrice.dto.ProductResponseDto;
import spring.EAVDemoPrice.service.IProductService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final IProductService productService;

    @GetMapping
    public List<ProductResponseDto> getProducts() {
        return productService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDto productDto) {
        try {
            ProductResponseDto newProduct = productService.addProduct(productDto);
            return ResponseEntity.ok(newProduct);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        try {
            ProductResponseDto existingProduct = productService.updateProduct(id, productDto);
            return ResponseEntity.ok(existingProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
