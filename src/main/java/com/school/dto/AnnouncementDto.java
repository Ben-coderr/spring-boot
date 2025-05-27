package com.school.dto;

import com.school.model.Announcement;

import java.time.LocalDateTime;

public record AnnouncementDto(Long id, String title, String content,
                              LocalDateTime publishedAt, Long classId) {
    public static AnnouncementDto from(Announcement a) {
        Long cid = (a.getSchoolClass() != null) ? a.getSchoolClass().getId() : null;
        return new AnnouncementDto(a.getId(), a.getTitle(), a.getContent(),
                a.getPublishedAt(), cid);
    }
}
