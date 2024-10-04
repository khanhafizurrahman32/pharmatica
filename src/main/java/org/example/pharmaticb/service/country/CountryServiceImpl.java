package org.example.pharmaticb.service.country;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.DB.Country;
import org.example.pharmaticb.Models.Request.CountryRequest;
import org.example.pharmaticb.Models.Response.CountryResponse;
import org.example.pharmaticb.repositories.CountryRepository;
import org.example.pharmaticb.repositories.ProductRepository;
import org.example.pharmaticb.utilities.log.Loggable;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;
    private final ModelMapper mapper;
    private final ProductRepository productRepository;

    @Override
    @Loggable
    public Mono<CountryResponse> createCategory(CountryRequest request) {
        return countryRepository.save(mapper.map(request, Country.class))
                .map(country -> mapper.map(country, CountryResponse.class));
    }

    @Override
    @Loggable
    public Flux<CountryResponse> getAllCategories() {
        return countryRepository.findAll()
                .flatMap(country -> productRepository.countProductsByCategoryId(country.getId())
                        .map(count -> convertDbToDto(country, count)));
    }

    private CountryResponse convertDbToDto(Country country, Long count) {
        return CountryResponse.builder()
                .id(String.valueOf(country.getId()))
                .countryName(country.getCountryName())
                .totalProductCount(String.valueOf(count))
                .build();
    }

    @Override
    @Loggable
    public Mono<CountryResponse> getCategoryById(Long id) {
        return countryRepository.findById(id)
                .map(country -> mapper.map(country, CountryResponse.class));
    }

    @Override
    @Loggable
    public Mono<CountryResponse> updateCategory(long id, CountryRequest request) {
        return countryRepository.findById(id)
                .flatMap(country -> {
                    mapper.map(request, country);
                    return countryRepository.save(country)
                            .map(updateCountry -> mapper.map(updateCountry, CountryResponse.class));
                });
    }

    @Override
    @Loggable
    public Mono<Void> deleteCategory(long id) {
        return countryRepository.deleteById(id);
    }
}
