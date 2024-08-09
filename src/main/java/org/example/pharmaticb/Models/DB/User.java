package org.example.pharmaticb.Models.DB;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.utilities.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user")
public class User {
    @Id
    @Transient
    private Long id;
    private String customerName;
    private String password;
    private Set<Role> roles;
}
