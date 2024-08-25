package org.example.pharmaticb.Models.DB;

import lombok.*;
import org.example.pharmaticb.utilities.Role;
import org.springframework.data.annotation.Id;
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
    @Generated
    private long id;
    private String userName;
    private String password;
    private Set<Role> roles;
    private boolean otpStatus;
    private String otpCode;
    private String email;
    private String phoneNumber;
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
