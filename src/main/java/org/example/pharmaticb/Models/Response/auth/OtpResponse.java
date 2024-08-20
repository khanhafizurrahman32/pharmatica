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
public class OtpResponse implements Serializable {
    @Builder.Default
    private String updateTime = DateUtil.formattedDateTime();
}
