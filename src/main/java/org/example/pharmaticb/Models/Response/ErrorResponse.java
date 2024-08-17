package org.example.pharmaticb.Models.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.pharmaticb.utilities.DateUtil;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse implements Serializable {
    private String message;
    private String code;
    @Builder.Default
    private String updateTime = DateUtil.formattedDateTime();
}
