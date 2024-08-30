package org.example.pharmaticb.Models.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse implements Serializable {
    private String id;
    private String label;
    private String iconUrl;
    private String categorySlug;
    private String[] subCategories;
    private String[] brand;
    private String[] priceRange;
    private String totalProductCount;
}
