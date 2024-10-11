package spring.EAVDemoPrice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductEntryDto {
    private String size;
    private String color;
    private Double price;
    private Double salePrice;
    private Integer quantity;
}
