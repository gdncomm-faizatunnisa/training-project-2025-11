package com.ecommerce.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for paginated product responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedProductResponse {

    private List<ProductResponse> content;
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Boolean last;
}
