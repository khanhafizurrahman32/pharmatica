package org.example.pharmaticb.Models.DB;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.utilities.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User implements Serializable {
    @Id
    @Transient
    private long id;
    private String customerName;
    private String password;
    private Set<Role> roles;
    private boolean otpStatus;
    private String otpCode;
    private String email;
    private String phone;
    private String address;
    private String bloodGroup;
    private String gender;
    private int age;
    private String imageUniqueId;
    private String profilePictureUrl;
    private String registrationStatus;
    private long otpExpirationTime;
    private String pinNonce;
}
