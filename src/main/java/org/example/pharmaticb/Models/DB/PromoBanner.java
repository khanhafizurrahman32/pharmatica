package org.example.pharmaticb.Models.DB;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("promo_banner")
public class PromoBanner implements Serializable {
    @Id
    @Generated
    private Long id;
    private String promoUrl;
    private String title;
    private boolean active;
}
