package br.com.petize.todolist.service;

import br.com.petize.todolist.model.Task;
import br.com.petize.todolist.model.enums.Priority;
import br.com.petize.todolist.model.enums.Status;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * https://docs.spring.io/spring-data/jpa/reference/jpa/specifications.html
 */

public class TaskSpecification {

    public static Specification<Task> isNull() {
        return ((root, query, builder) -> builder.conjunction());
    }

    public static Specification<Task> hasStatus(Status status) {
        return ((root, query, builder) -> builder.equal(root.get("status"), status));
    }

    public static Specification<Task> hasPriority(Priority priority) {
        return ((root, query, builder) -> builder.equal(root.get("priority"), priority));
    }

    public static Specification<Task> hasDueDate(LocalDate dueDate) {
        return ((root, query, builder) -> builder.equal(root.get("dueDate"), dueDate));
    }
}
