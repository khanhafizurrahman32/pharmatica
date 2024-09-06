package org.example.pharmaticb.Models.Request;

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
public class SmsRequest implements Serializable {
    @Builder.Default
    @JsonProperty("api_key")
    private String apiKey = "7CeQZ5zUMpk5ULRZD8ne";
    @Builder.Default
    @JsonProperty("senderid")
    private String senderId = "8809617620211";
    private String number;
    private String message;
}
