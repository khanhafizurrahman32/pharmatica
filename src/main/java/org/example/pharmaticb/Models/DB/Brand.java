package org.example.pharmaticb.Models.DB;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("brand")
public class Brand {
    @Id
    @Generated
    private Long id;
    private String brandName;
}
