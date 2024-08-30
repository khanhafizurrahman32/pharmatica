package org.example.pharmaticb.Models.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandResponse implements Serializable {
    private String id;
    private String brandName;
    private String totalProductCount;
}
