package org.example.pharmaticb.Models.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmsResponse implements Serializable {
    @JsonProperty("response_code")
    private String responseCode;
    @JsonProperty("message_id")
    private String messageId;
    @JsonProperty("success_message")
    private String successMessage;
    @JsonProperty("error_message")
    private String errorMessage;
}
