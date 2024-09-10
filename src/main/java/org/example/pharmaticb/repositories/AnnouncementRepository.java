package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.Announcement;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface AnnouncementRepository extends R2dbcRepository<Announcement, Long> {
}
