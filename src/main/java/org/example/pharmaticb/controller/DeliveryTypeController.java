package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.CountryRequest;
import org.example.pharmaticb.Models.Request.DeliveryTypeRequest;
import org.example.pharmaticb.Models.Response.CountryResponse;
import org.example.pharmaticb.Models.Response.DeliveryTypeResponse;
import org.example.pharmaticb.service.delivery.type.DeliveryTypeService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@BaseController
@RestController
@RequiredArgsConstructor
public class DeliveryTypeController {
    private final DeliveryTypeService deliveryTypeService;

    @PostMapping("/delivery-options")
    public Mono<DeliveryTypeResponse> createDeliveryChargeType(@Valid @RequestBody DeliveryTypeRequest request) {
        return deliveryTypeService.createDeliveryChargeType(request);
    }

    @GetMapping("/delivery-options")
    public Flux<DeliveryTypeResponse> getAllDeliveryChargeType() {
        return deliveryTypeService.getAllDeliveryChargeType();
    }

    @GetMapping("/delivery-options/{id}")
    public Mono<DeliveryTypeResponse> getDeliveryChargeTypeById(@PathVariable Long id) {
        return deliveryTypeService.getDeliveryChargeTypeById(id);
    }

    @PutMapping("/delivery-options/{id}")
    public Mono<DeliveryTypeResponse> updateDeliveryChargeType(@Valid @PathVariable Long id, @Valid @RequestBody CountryRequest request) {
        return deliveryTypeService.updateDeliveryChargeType(id, request);
    }

    @DeleteMapping("/delivery-options/{id}")
    public Mono<Void> deleteDeliveryChargeType(@PathVariable Long id) {
        return deliveryTypeService.deleteDeliveryChargeType(id);
    }
}
