package org.example.pharmaticb.Models.DB;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("delivery_type")
public class DeliveryType implements Serializable {
    @Id
    @Generated
    private Long id;
    private String title;
    private String description;
    private Double rate;
}
