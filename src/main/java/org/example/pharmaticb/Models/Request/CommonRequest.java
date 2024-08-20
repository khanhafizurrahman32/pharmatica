package org.example.pharmaticb.Models.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.example.pharmaticb.utilities.Utility;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommonRequest implements Serializable {
    @NotBlank(message = ServiceError.INVALID_REQUEST)
    private String userName;

//    @NotBlank(message = ServiceError.INVALID_REQUEST)
    @Pattern(regexp = Utility.BD_MSISDN_REGEX, message = ServiceError.INVALID_REQUEST)
    private String phone;
}
