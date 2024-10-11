package spring.EAVDemoPrice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.EAVDemoPrice.dto.ProductDto;
import spring.EAVDemoPrice.dto.ProductEntryDto;
import spring.EAVDemoPrice.dto.ProductResponseDto;
import spring.EAVDemoPrice.entity.Color;
import spring.EAVDemoPrice.entity.Product;
import spring.EAVDemoPrice.entity.ProductEntry;
import spring.EAVDemoPrice.entity.Size;
import spring.EAVDemoPrice.exception.DataNotFoundException;
import spring.EAVDemoPrice.repository.ColorRepository;
import spring.EAVDemoPrice.repository.ProductRepository;
import spring.EAVDemoPrice.repository.SizeRepository;
import spring.EAVDemoPrice.service.IProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;

    @Override
    public List<ProductResponseDto> findAll() {
        List<Product> products = productRepository.findAll();
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for (Product product : products) {
            ProductResponseDto responseDto = ProductResponseDto.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .productEntries(product.getProductEntries().stream()
                            .map(productPrice -> ProductEntryDto.builder()
                                    .size(productPrice.getSize().getName())
                                    .color(productPrice.getColor().getName())
                                    .price(productPrice.getPrice())
                                    .salePrice(productPrice.getSalePrice())
                                    .quantity(productPrice.getQuantity())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
            productResponseDtos.add(responseDto);
        }
        return productResponseDtos;
    }

    @Override
    public ProductResponseDto findProductById(Long id) throws DataNotFoundException {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Cannot find product with id: " + id));

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .productEntries(product.getProductEntries().stream()
                        .map(productEntry -> ProductEntryDto.builder()
                                .size(productEntry.getSize().getName())
                                .color(productEntry.getColor().getName())
                                .price(productEntry.getPrice())
                                .salePrice(productEntry.getSalePrice())
                                .quantity(productEntry.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public ProductResponseDto addProduct(ProductDto productDto) {
        Product newProduct = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .isDelete(Boolean.FALSE)
                .build();

        List<Color> colors = new ArrayList<>();
        List<Size> sizes = new ArrayList<>();
        List<ProductEntry> productEntries = new ArrayList<>();

        for (ProductEntryDto productEntryDto : productDto.getProductEntries()){
            String colorName = productEntryDto.getColor().trim();
            String sizeName = productEntryDto.getSize().trim();

            Color color = colorRepository.findByName(colorName).orElseGet(() -> {
                Color newColor = Color.builder().name(colorName).build();
                return colorRepository.save(newColor);
            });
            colors.add(color);

            Size size = sizeRepository.findByName(sizeName).orElseGet(() -> {
                Size newSize = Size.builder().name(sizeName).build();
                return sizeRepository.save(newSize);
            });
            sizes.add(size);

            ProductEntry productEntry = ProductEntry.builder()
                    .product(newProduct)
                    .color(color)
                    .size(size)
                    .price(productEntryDto.getPrice())
                    .salePrice(productEntryDto.getSalePrice())
                    .quantity(productEntryDto.getQuantity())
                    .build();
            productEntries.add(productEntry);
        }
        newProduct.setProductEntries(productEntries);
        productRepository.save(newProduct);

        return ProductResponseDto.builder()
                .id(newProduct.getId())
                .name(newProduct.getName())
                .description(newProduct.getDescription())
                .productEntries(productDto.getProductEntries())
                .build();
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductDto productDto) throws DataNotFoundException {
        Product existingProduct = productRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Cannot find product with id: " + id));

        existingProduct.setName(productDto.getName());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setIsDelete(Boolean.FALSE);

        // Lưu danh sách giá cũ
        List<ProductEntry> currentEntries = existingProduct.getProductEntries();
        List<ProductEntry> newEntries = new ArrayList<>();
        List<ProductEntry> updatedEntries = new ArrayList<>();

        // Duyệt qua danh sách giá mới từ DTO
        for (ProductEntryDto productEntryDto : productDto.getProductEntries()) {
            String colorName = productEntryDto.getColor().trim();
            String sizeName = productEntryDto.getSize().trim();

            // Tìm hoặc tạo màu sắc mới
            Color color = colorRepository.findByName(colorName).orElseGet(() -> {
                Color newColor = Color.builder().name(colorName).build();
                return colorRepository.save(newColor);
            });

            // Tìm hoặc tạo kích thước mới
            Size size = sizeRepository.findByName(sizeName).orElseGet(() -> {
                Size newSize = Size.builder().name(sizeName).build();
                return sizeRepository.save(newSize);
            });

            // Kiểm tra xem ProductEntry đã tồn tại hay chưa
            ProductEntry existingEntries = currentEntries.stream()
                    .filter(price -> price.getColor().equals(color) && price.getSize().equals(size))
                    .findFirst()
                    .orElse(null);

            if (existingEntries != null) {
                // Cập nhật giá nếu đã tồn tại
                existingEntries.setPrice(productEntryDto.getPrice());
                existingEntries.setSalePrice(productEntryDto.getSalePrice());
                existingEntries.setQuantity(productEntryDto.getQuantity());
                updatedEntries.add(existingEntries);
            } else {
                // Tạo một ProductEntry mới nếu chưa tồn tại
                ProductEntry newPrice = ProductEntry.builder()
                        .product(existingProduct)
                        .color(color)
                        .size(size)
                        .price(productEntryDto.getPrice())
                        .salePrice(productEntryDto.getSalePrice())
                        .quantity(productEntryDto.getQuantity())
                        .build();
                newEntries.add(newPrice);
            }
        }

        // Cập nhật danh sách ProductEntry của sản phẩm
        existingProduct.setProductEntries(newEntries);

        // Lưu sản phẩm đã cập nhật
        productRepository.save(existingProduct);

        // Tạo ProductResponseDto từ existingProduct và productPrices
        List<ProductEntryDto> responsePrices = updatedEntries.stream()
                .map(price -> ProductEntryDto.builder()
                        .size(price.getSize().getName())
                        .color(price.getColor().getName())
                        .price(price.getPrice())
                        .salePrice(price.getSalePrice())
                        .quantity(price.getQuantity())
                        .build())
                .collect(Collectors.toList());

        // Thêm các ProductEntry mới vào response
        for (ProductEntry newEntry : newEntries) {
            responsePrices.add(ProductEntryDto.builder()
                    .size(newEntry.getSize().getName())
                    .color(newEntry.getColor().getName())
                    .price(newEntry.getPrice())
                    .salePrice(newEntry.getSalePrice())
                    .quantity(newEntry.getQuantity())
                    .build());
        }

        return ProductResponseDto.builder()
                .id(existingProduct.getId())
                .name(existingProduct.getName())
                .description(existingProduct.getDescription())
                .productEntries(responsePrices)
                .build();
    }
}
