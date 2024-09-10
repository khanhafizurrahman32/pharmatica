package org.example.pharmaticb.service.announcement;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.AnnouncementRequest;
import org.example.pharmaticb.Models.Response.AnnouncementResponse;
import org.example.pharmaticb.repositories.AnnouncementRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final ModelMapper modelMapper;
    @Override
    public Mono<AnnouncementResponse> updateAnnouncement(Long id, AnnouncementRequest request) {
        return announcementRepository.findById(id)
                .flatMap(announcement -> {
                    modelMapper.map(request, announcement);
                    return announcementRepository.save(announcement)
                            .map(updateBrand -> modelMapper.map(updateBrand, AnnouncementResponse.class));
                });
    }

    @Override
    public Mono<AnnouncementResponse> getAnnouncement(Long id) {
        return announcementRepository.findById(id)
                .map(announcement -> modelMapper.map(announcement, AnnouncementResponse.class));
    }
}
