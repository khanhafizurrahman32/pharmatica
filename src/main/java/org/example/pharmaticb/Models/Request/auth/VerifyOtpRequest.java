package org.example.pharmaticb.Models.Request.auth;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.pharmaticb.Models.Request.CommonRequest;
import org.example.pharmaticb.utilities.Exception.ServiceError;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class VerifyOtpRequest extends CommonRequest implements Serializable {
    @NotNull(message = ServiceError.INVALID_REQUEST)
    @Size(min = 6, max = 20, message = ServiceError.INVALID_REQUEST)
    private String otpCode;
}
