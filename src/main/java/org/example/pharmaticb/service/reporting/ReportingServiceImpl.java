package org.example.pharmaticb.service.reporting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.Response.SummaryResponse;
import org.example.pharmaticb.Models.Response.WeeklyOrderResponse;
import org.example.pharmaticb.repositories.OrderRepository;
import org.example.pharmaticb.repositories.ProductRepository;
import org.example.pharmaticb.repositories.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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

    @Override
    public Mono<List<WeeklyOrderResponse>> getWeeklySummaries() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(7);
        return orderRepository.findDailyOrderCountsLastWeek(startDate, endDate)
                .collectList()
                .map(orders -> orders.stream()
                        .map(order -> WeeklyOrderResponse.builder()
                                .day(String.valueOf(order.getDate()))
                                .count(order.getOrderCount())
                                .build())
                        .collect(Collectors.toList()));
    }
}
