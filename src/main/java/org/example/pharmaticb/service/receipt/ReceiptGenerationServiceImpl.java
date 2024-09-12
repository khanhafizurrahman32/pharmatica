package org.example.pharmaticb.service.receipt;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.example.pharmaticb.dto.ReceiptGenerationDto;
import org.example.pharmaticb.service.file.FileUploadService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptGenerationServiceImpl implements ReceiptGenerationService {
    private final SpringTemplateEngine templateEngine;
    private final FileUploadService fileUploadService;

    @Override
    public Mono<String> generateReceiptPdf(ReceiptGenerationDto request) {
        var filePathOptional = generateReceipt(request);

        if (filePathOptional.isEmpty()) {
            log.error("Output path for receipt not found - trx id: {}", request.getTrxId());
            return Mono.empty();
        }

        return uploadFile(filePathOptional.get());
    }

    private Mono<String> uploadFile(String filePath) {
        try {
            InputStream batchInputStream = new FileInputStream(filePath);
            String key = filePath.replace("/tmp", "");
            byte[] byteArray =  IOUtils.toByteArray(batchInputStream);
            return fileUploadService.uploadFile(key, byteArray, "application/pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<String> generateReceipt(ReceiptGenerationDto request) {
        try {
            Context context = new Context();
            context.setVariable("receiptGenerationRequest", request);
            String htmlResult = templateEngine.process("receipt.html", context);
            String outputFilePath = String.format("/tmp/%s.pdf", request.getTrxId());


            File robotoRegular = File.createTempFile(UUID.randomUUID().toString(), ".txt");
            File robotoMedium = File.createTempFile(UUID.randomUUID().toString(), ".txt");

            InputStream robotoRegularIn = new ClassPathResource("fonts/Roboto-Regular.ttf").getInputStream();
            InputStream robotoMediumIn = new ClassPathResource("fonts/Roboto-Regular.ttf").getInputStream();

            FileUtils.copyInputStreamToFile(robotoRegularIn, robotoRegular);
            FileUtils.copyInputStreamToFile(robotoMediumIn, robotoMedium);

            IOUtils.closeQuietly(robotoRegularIn);
            IOUtils.closeQuietly(robotoMediumIn);

            PdfRendererBuilder pdfRendererBuilder = new PdfRendererBuilder();

            OutputStream outputStream = new FileOutputStream(outputFilePath);

            pdfRendererBuilder.useFastMode();
            pdfRendererBuilder.useFont(robotoRegular, "Roboto");
            pdfRendererBuilder.useFont(robotoMedium, "Roboto");
            pdfRendererBuilder.withHtmlContent(htmlResult, "/");
            pdfRendererBuilder.toStream(outputStream);
            pdfRendererBuilder.run();

            return Optional.of(outputFilePath);

        } catch (IOException e) {
            log.error("Error Generating receipt - trx id {}", request.getTrxId(), e);
        }

        return Optional.empty();
    }


}
