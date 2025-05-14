package project.edusphere.announcement.repository;

import project.edusphere.announcement.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> { }
