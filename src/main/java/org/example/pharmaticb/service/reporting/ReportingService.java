package org.example.pharmaticb.service.reporting;

import org.example.pharmaticb.Models.Response.SummaryResponse;
import reactor.core.publisher.Mono;

public interface ReportingService {

    Mono<SummaryResponse> getSummaries();
}
