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
public class UserResponse implements Serializable {
    private String id;
    private String userName;
    private String phoneNumber;
    private String address;
    private String registrationStatus;
    private String profilePictureUrl;
    private String role;
}
