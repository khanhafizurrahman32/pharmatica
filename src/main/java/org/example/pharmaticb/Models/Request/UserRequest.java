package org.example.pharmaticb.Models.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.utilities.Role;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest implements Serializable {
    private String userName;
    private String profilePictureUrl;
    private String email;
    private String address;
    private String bloodGroup;
    private String gender;
    private int age;
    private Set<Role> roles;
}
