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
public class CountryResponse implements Serializable {
    private String id;
    private String countryName;
    private String totalProductCount;
}

