package org.example.pharmaticb.Models.Request;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UploadFileRequest extends CommonRequest implements Serializable {

    @NotNull(message = ServiceError.INVALID_REQUEST)
//    @Size(min = 1000, max = 4096000, message = ServiceError.INVALID_REQUEST)
    private byte[] file;

    @NotNull(message = ServiceError.INVALID_REQUEST)
    private String contentType;

    @NotNull(message = ServiceError.INVALID_REQUEST)
    private Boolean privacyEnabled;
}
