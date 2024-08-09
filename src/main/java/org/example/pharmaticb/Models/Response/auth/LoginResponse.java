package org.example.pharmaticb.Models.Response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.utilities.DateUtil;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements Serializable {
    private String accessToken;
    private String refreshToken;
    private int accessExpiredIn;
    private int refreshExpiredIn;
    @Builder.Default
    private String updateTime = DateUtil.formattedDateTime();
}
