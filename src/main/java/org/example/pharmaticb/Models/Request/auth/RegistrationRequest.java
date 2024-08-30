package org.example.pharmaticb.Models.Request.auth;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.pharmaticb.Models.Request.CommonRequest;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.example.pharmaticb.dto.enums.Role;
import org.example.pharmaticb.utilities.validation.ValidEnum;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RegistrationRequest extends CommonRequest implements Serializable {
    @NotBlank(message = ServiceError.INVALID_REQUEST)
    private String password;
    @ValidEnum(enumClass = Role.class, message = ServiceError.INVALID_REQUEST)
    private Role role;
}
