package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.AnnouncementRequest;
import org.example.pharmaticb.Models.Response.AnnouncementResponse;
import org.example.pharmaticb.service.announcement.AnnouncementService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@BaseController
@RestController
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PutMapping("/announcement-update/{id}")
    public Mono<AnnouncementResponse> updateAnnouncement(@PathVariable Long id, @Valid @RequestBody AnnouncementRequest request) {
        return announcementService.updateAnnouncement(id, request);
    }

    @GetMapping("/announcement/{id}")
    public Mono<AnnouncementResponse> getAnnouncement(@PathVariable Long id) {
        return announcementService.getAnnouncement(id);
    }

}
