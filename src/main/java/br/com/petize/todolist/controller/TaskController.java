package br.com.petize.todolist.controller;

import br.com.petize.todolist.dtos.StatusUpdateDTO;
import br.com.petize.todolist.dtos.TaskResponseDTO;
import br.com.petize.todolist.dtos.UpdatePriorityDTO;
import br.com.petize.todolist.model.Task;
import br.com.petize.todolist.model.User;
import br.com.petize.todolist.model.enums.Priority;
import br.com.petize.todolist.model.enums.Status;
import br.com.petize.todolist.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Cria uma tarefa", description = "Cria uma tarefa recebendo os dados da tarefa pelo JSON")
    public ResponseEntity<Task> create(@RequestBody @Valid Task task, @AuthenticationPrincipal User authenticatedUser) {
        Task createdTask = taskService.create(task, authenticatedUser); // associação de Task com User
        return ResponseEntity.ok().body(createdTask);
    }

    @PostMapping(value = "/{parentId}/subtasks", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Cria uma subtarefa", description = "Cria uma subtarefa associando ela à uma tarefa pai")
    public ResponseEntity<Task> createSubtask(@PathVariable Long parentId, @Valid @RequestBody Task subtaskData, @AuthenticationPrincipal User authenticatedUser) {
        Task createdSubtask = taskService.createSubtask(parentId, subtaskData, authenticatedUser);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/task/{id}")
                .buildAndExpand(createdSubtask.getId()).toUri();

        return ResponseEntity.created(location).body(createdSubtask);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Busca todas tarefas do Usuário com filtros opcionais", description = "Busca as tarefas do usuário persistidas no banco de dados com filtros como status, priority e dueDate")
    public ResponseEntity<Page<TaskResponseDTO>> findAll(
            @AuthenticationPrincipal User authenticatedUser,
            @PageableDefault(size = 10, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false)Status status,
            @RequestParam(required = false)Priority priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate
    ) {
        Page<TaskResponseDTO> tasks = taskService.findUserTasksWithFilters(authenticatedUser, status, priority, dueDate, pageable);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Atualiza a tarefa", description = "Atualiza a tarefa com os campos alterados")
    public ResponseEntity<Task> update(@PathVariable(value = "id") Long id, @RequestBody Task task) {
        try {
            task.setId(id);
            return ResponseEntity.ok(taskService.update(id, task));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deleta a tarefa", description = "Deleta a tarefa por id")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        try {
            taskService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Atualiza o status da tarefa", description = "Atualiza o status da tarefa passando o novo status em JSON")
    public ResponseEntity<Task> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateDTO statusUpdate) {
        Task updatedTask = taskService.updateStatus(id, statusUpdate.status());
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping(value = "/{id}/priority", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Atualiza a prioridade da tarefa", description = "Atualiza a prioridade tarefa passando a nova prioridade em JSON")
    public ResponseEntity<Task> updatePriority(@PathVariable Long id, @Valid @RequestBody UpdatePriorityDTO updatePriority) {
        Task updatedTask = taskService.updatePriority(id, updatePriority.priority());
        return ResponseEntity.ok(updatedTask);
    }
}