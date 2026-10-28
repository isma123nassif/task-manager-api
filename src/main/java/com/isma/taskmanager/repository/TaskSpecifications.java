package com.isma.taskmanager.repository;

import com.isma.taskmanager.domain.Task;
import com.isma.taskmanager.domain.TaskPriority;
import com.isma.taskmanager.domain.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

/**
 * Reusable query predicates for filtering {@link Task} entities.
 */
public final class TaskSpecifications {

    private TaskSpecifications() {
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, criteriaBuilder) -> status == null
                ? null
                : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(TaskPriority priority) {
        return (root, query, criteriaBuilder) -> priority == null
                ? null
                : criteriaBuilder.equal(root.get("priority"), priority);
    }

    public static Specification<Task> belongsToProject(Long projectId) {
        return (root, query, criteriaBuilder) -> projectId == null
                ? null
                : criteriaBuilder.equal(root.get("project").get("id"), projectId);
    }

    public static Specification<Task> withFilters(TaskStatus status, TaskPriority priority, Long projectId) {
        return Specification.allOf(hasStatus(status), hasPriority(priority), belongsToProject(projectId));
    }
}
