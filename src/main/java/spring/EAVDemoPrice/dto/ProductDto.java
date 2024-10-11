package spring.EAVDemoPrice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    private String name;
    private String description;
    private List<ProductEntryDto> productEntries = new ArrayList<>();
    private Boolean isDelete = Boolean.FALSE;
}
