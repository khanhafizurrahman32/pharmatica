package org.example.pharmaticb.service.announcement;

import org.example.pharmaticb.Models.Request.AnnouncementRequest;
import org.example.pharmaticb.Models.Response.AnnouncementResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

public interface AnnouncementService {
    Mono<AnnouncementResponse> updateAnnouncement(@PathVariable Long id, @Valid @RequestBody AnnouncementRequest request);

    Mono<AnnouncementResponse> getAnnouncement(Long id);
}
