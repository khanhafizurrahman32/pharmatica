package org.example.pharmaticb.Models.DB;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("announcement")
public class Announcement implements Serializable {
    @Id
    @Generated
    private Long id;
    private String description;
    private boolean enabled;
}
