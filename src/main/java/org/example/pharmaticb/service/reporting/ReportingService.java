package org.example.pharmaticb.service.reporting;

import org.example.pharmaticb.Models.Response.SummaryResponse;
import org.example.pharmaticb.Models.Response.WeeklyOrderResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReportingService {

    Mono<SummaryResponse> getSummaries();

    Mono<List<WeeklyOrderResponse>> getWeeklySummaries();
}
