package br.com.petize.todolist.dtos;

import br.com.petize.todolist.model.Task;
import br.com.petize.todolist.model.enums.Priority;
import br.com.petize.todolist.model.enums.Status;

import java.time.LocalDate;

public record TaskResponseDTO(Long id, String title, String description, LocalDate dueDate, Status status, Priority priority, UserResponseDTO user) {
    public TaskResponseDTO(Task task) {
        this(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus(),
                task.getPriority(),
                task.getUser() != null ? new UserResponseDTO(task.getUser()) : null
        );
    }
}
