package org.example.pharmaticb.Models.Request.auth;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.pharmaticb.Models.Request.CommonRequest;
import org.example.pharmaticb.utilities.Exception.ServiceError;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LoginRequest extends CommonRequest implements Serializable {
    @NotBlank(message = ServiceError.INVALID_REQUEST)
    private String password;
}
