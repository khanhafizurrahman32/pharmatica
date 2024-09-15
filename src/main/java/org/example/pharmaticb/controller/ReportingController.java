package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Response.SummaryResponse;
import org.example.pharmaticb.Models.Response.WeeklyOrderResponse;
import org.example.pharmaticb.service.reporting.ReportingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@BaseController
@RestController
@RequiredArgsConstructor
public class ReportingController {
    private final ReportingService reportingService;

    @GetMapping("/report/summary-details")
    public Mono<SummaryResponse> getSummaries() {
        return reportingService.getSummaries();
    }

    @GetMapping("/report/order/weekly-details")
    public Mono<List<WeeklyOrderResponse>> getWeeklySummaries() {
        return reportingService.getWeeklySummaries();
    }
}
