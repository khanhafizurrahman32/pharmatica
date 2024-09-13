//package org.example.pharmaticb.service.email;
//
//import org.example.pharmaticb.utilities.AbstractWebClient;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@Service
//public class EmailServiceImpl extends AbstractWebClient implements EmailService {
//
//    private final String mailgunDomain;
//
//    private final String EMAIL_ENDPOINT;
//
//    protected EmailServiceImpl(@Qualifier("emailWebClient") WebClient webClient,
//                               @Value("${mailgun.domain}") String mailgunDomain) {
//        super(webClient);
//        this.mailgunDomain = mailgunDomain;
//        this.EMAIL_ENDPOINT = "/" + mailgunDomain + "/messages";
//    }
//
//    @Override
//    public Mono<String> sendEmail(String to, String subject, String text) {
//        BodyInserters.FormInserter<String> with = BodyInserters
//                .fromFormData("from", "Excited User <mailgun@" + mailgunDomain + ">")
//                .with("to", to)
//                .with("subject", subject)
//                .with("text", text);
//
//        return post(EMAIL_ENDPOINT,with, String.class);
//    }
//}
