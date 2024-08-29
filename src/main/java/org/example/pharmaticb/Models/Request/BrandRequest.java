package org.example.pharmaticb.Models.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.utilities.Exception.ServiceError;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandRequest implements Serializable {
    @NotBlank(message = ServiceError.INVALID_REQUEST)
    private String brandName;
}
