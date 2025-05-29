package com.school.dto;

import com.school.model.Announcement;

import java.time.LocalDateTime;

public record AnnouncementDto(Long id, String title, String content,
                              LocalDateTime publishedAt, Long classId) { // data for clients
    public static AnnouncementDto from(Announcement announcement) {
        Long cid = (announcement.getSchoolClass() != null) ? announcement.getSchoolClass().getId() : null;
        return new AnnouncementDto(announcement.getId(), announcement.getTitle(), announcement.getContent(),
                announcement.getPublishedAt(), cid);
    }
}
