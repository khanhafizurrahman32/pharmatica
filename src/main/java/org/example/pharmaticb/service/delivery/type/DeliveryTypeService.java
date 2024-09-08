package org.example.pharmaticb.service.delivery.type;

import org.example.pharmaticb.Models.Request.CountryRequest;
import org.example.pharmaticb.Models.Request.DeliveryTypeRequest;
import org.example.pharmaticb.Models.Response.CountryResponse;
import org.example.pharmaticb.Models.Response.DeliveryTypeResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

public interface DeliveryTypeService {
    Mono<DeliveryTypeResponse> createDeliveryChargeType(@Valid DeliveryTypeRequest request);

    Flux<DeliveryTypeResponse> getAllDeliveryChargeType();

    Mono<DeliveryTypeResponse> getDeliveryChargeTypeById(Long id);

    Mono<DeliveryTypeResponse> updateDeliveryChargeType(@Valid Long id, @Valid CountryRequest request);

    Mono<Void> deleteDeliveryChargeType(Long id);
}
