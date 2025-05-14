package project.edusphere.parent.repository;

import project.edusphere.parent.model.Parent;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentRepository extends JpaRepository<Parent, Long> { 
    Optional<Parent> findByEmail(String email); 
}