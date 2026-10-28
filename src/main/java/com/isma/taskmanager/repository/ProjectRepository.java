package com.isma.taskmanager.repository;

import com.isma.taskmanager.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence layer for {@link Project} entities.
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
