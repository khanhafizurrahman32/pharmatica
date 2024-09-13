package org.example.pharmaticb.Models.Request.auth;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.pharmaticb.Models.Request.CommonRequest;
import org.example.pharmaticb.dto.enums.Role;
import org.example.pharmaticb.utilities.validation.PasswordConstraint;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RegistrationRequest extends CommonRequest implements Serializable {
    @PasswordConstraint(message = "Your password must contain uppercase, lowercase, special character, number and must be of 6 digit length")
    private String password;
//    @ValidEnum(enumClass = Role.class, message = ServiceError.INVALID_REQUEST)
    private Role role;
}
