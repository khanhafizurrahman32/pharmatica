package org.example.pharmaticb.Models.Request.auth;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.pharmaticb.Models.Request.CommonRequest;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OtpRequest extends CommonRequest implements Serializable {
}
