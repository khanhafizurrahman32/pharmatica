package org.example.pharmaticb.service.country;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.DB.Country;
import org.example.pharmaticb.Models.Request.CountryRequest;
import org.example.pharmaticb.Models.Response.CountryResponse;
import org.example.pharmaticb.repositories.CountryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;
    private final ModelMapper mapper;

    @Override
    public Mono<CountryResponse> createCategory(CountryRequest request) {
        return countryRepository.save(mapper.map(request, Country.class))
                .map(country -> mapper.map(country, CountryResponse.class));
    }

    @Override
    public Flux<CountryResponse> getAllCategories() {
        return countryRepository.findAll()
                .map(country -> mapper.map(country, CountryResponse.class));
    }

    @Override
    public Mono<CountryResponse> getCategoryById(Long id) {
        return countryRepository.findById(id)
                .map(country -> mapper.map(country, CountryResponse.class));
    }

    @Override
    public Mono<CountryResponse> updateCategory(long id, CountryRequest request) {
        return countryRepository.findById(id)
                .flatMap(country -> {
                    mapper.map(request, country);
                    return countryRepository.save(country)
                            .map(updateCountry -> mapper.map(updateCountry, CountryResponse.class));
                });
    }

    @Override
    public Mono<Void> deleteCategory(long id) {
        return countryRepository.deleteById(id);
    }
}