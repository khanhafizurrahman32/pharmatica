package org.example.pharmaticb.Models.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnnouncementRequest implements Serializable {
    private String description;
    private boolean enabled;
}
