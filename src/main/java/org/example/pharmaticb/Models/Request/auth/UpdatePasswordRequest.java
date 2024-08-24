package org.example.pharmaticb.Models.Request.auth;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdatePasswordRequest implements Serializable {
    private String oldPassword;
    private String newPassword;
}
