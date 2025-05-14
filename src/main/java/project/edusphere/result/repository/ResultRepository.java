package project.edusphere.result.repository;

import project.edusphere.result.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<Result, Long> { }
