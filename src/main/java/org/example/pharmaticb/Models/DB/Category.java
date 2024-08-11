package org.example.pharmaticb.Models.DB;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("category")
public class Category implements Serializable {
    @Id
    @Generated
    private long id;
    private String label;
    private String iconUrl;
    private String categorySlug;
    private String[] subCategories;
    private String[] brand;
    private String[] priceRange;
}
