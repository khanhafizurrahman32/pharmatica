package org.example.pharmaticb.service.delivery.type;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.DB.DeliveryType;
import org.example.pharmaticb.Models.Request.CountryRequest;
import org.example.pharmaticb.Models.Request.DeliveryTypeRequest;
import org.example.pharmaticb.Models.Response.DeliveryTypeResponse;
import org.example.pharmaticb.repositories.DeliveryTypeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeliveryTypeServiceImpl implements DeliveryTypeService {
    private final DeliveryTypeRepository deliveryTypeRepository;
    private final ModelMapper mapper;

    @Override
    public Mono<DeliveryTypeResponse> createDeliveryChargeType(DeliveryTypeRequest request) {
        return deliveryTypeRepository.save(mapper.map(request, DeliveryType.class))
                .map(deliveryType -> mapper.map(deliveryType, DeliveryTypeResponse.class));
    }

    @Override
    public Flux<DeliveryTypeResponse> getAllDeliveryChargeType() {
        return deliveryTypeRepository.findAll()
                .map(this::convertDbToDto);
    }

    private DeliveryTypeResponse convertDbToDto(DeliveryType deliveryType) {
        return DeliveryTypeResponse.builder()
                .id(String.valueOf(deliveryType.getId()))
                .title(deliveryType.getTitle())
                .description(deliveryType.getDescription())
                .rate(String.valueOf(deliveryType.getRate()))
                .build();
    }

    @Override
    public Mono<DeliveryTypeResponse> getDeliveryChargeTypeById(Long id) {
        return deliveryTypeRepository.findById(id)
                .map(this::convertDbToDto);
    }

    @Override
    public Mono<DeliveryTypeResponse> updateDeliveryChargeType(Long id, CountryRequest request) {
        return deliveryTypeRepository.findById(id)
                .flatMap(deliveryType -> {
                    mapper.map(request, deliveryType);
                    return deliveryTypeRepository.save(deliveryType)
                            .map(updateDeliveryType -> mapper.map(updateDeliveryType, DeliveryTypeResponse.class));
                });
    }

    @Override
    public Mono<Void> deleteDeliveryChargeType(Long id) {
        return deliveryTypeRepository.deleteById(id);
    }
}
