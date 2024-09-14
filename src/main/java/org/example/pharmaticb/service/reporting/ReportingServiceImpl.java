package org.example.pharmaticb.service.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.Models.Response.SummaryResponse;
import org.example.pharmaticb.repositories.OrderRepository;
import org.example.pharmaticb.repositories.ProductRepository;
import org.example.pharmaticb.repositories.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Builder
public class ReportingServiceImpl implements ReportingService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public Mono<SummaryResponse> getSummaries() {
        return Mono.zip(userRepository.count(), orderRepository.count(), productRepository.count())
                .map(tuple3 -> SummaryResponse.builder()
                        .totalUsers(tuple3.getT1())
                        .totalOrders(tuple3.getT2())
                        .totalProducts(tuple3.getT3())
                        .build());
    }
}
