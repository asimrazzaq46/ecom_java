package com.ecomerce.sb_ecom.payload.product;

import com.ecomerce.sb_ecom.payload.pagination.PageInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private List<ProductDto> content;
    private PageInformation pageDetail;
}
