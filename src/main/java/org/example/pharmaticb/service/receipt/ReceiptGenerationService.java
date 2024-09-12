package org.example.pharmaticb.service.receipt;

import org.example.pharmaticb.dto.ReceiptGenerationDto;
import reactor.core.publisher.Mono;

public interface ReceiptGenerationService {
    Mono<String> generateReceiptPdf(ReceiptGenerationDto request);
}
