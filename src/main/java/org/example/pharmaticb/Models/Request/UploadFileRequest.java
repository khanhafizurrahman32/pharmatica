package org.example.pharmaticb.Models.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadFileRequest implements Serializable {

    @NotNull(message = ServiceError.INVALID_REQUEST)
    @Size(min = 1000, max = 4096000, message = ServiceError.INVALID_REQUEST)
    private byte[] file;

    @NotNull(message = ServiceError.INVALID_REQUEST)
    private String contentType;

    @NotNull(message = ServiceError.INVALID_REQUEST)
    private Boolean privacyEnabled;
}
