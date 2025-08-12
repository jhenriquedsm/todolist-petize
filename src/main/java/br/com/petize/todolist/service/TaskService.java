package br.com.petize.todolist.service;

import br.com.petize.todolist.dtos.TaskResponseDTO;
import br.com.petize.todolist.model.Task;
import br.com.petize.todolist.model.User;
import br.com.petize.todolist.model.enums.Priority;
import br.com.petize.todolist.model.enums.Status;
import br.com.petize.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepository;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada!"));
    }

    public Task create(Task task, User authenticatedUser) {
        task.setId(null);
        task.setUser(authenticatedUser);

        if (task.getParentTask() != null && task.getParentTask().getId() != null) {
            Task parent = findById(task.getParentTask().getId());
            task.setParentTask(parent);
        }
        return taskRepository.save(task);
    }

    public Task createSubtask(Long parentId, Task subtaskData, User authenticatedUser) {
        Task parentTask = taskRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada!"));
        subtaskData.setId(null);
        subtaskData.setParentTask(parentTask);
        subtaskData.setUser(authenticatedUser);

        return taskRepository.save(subtaskData);
    }

    public Task update(Long id, Task taskData) {
        Task foundTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada!"));

        // Impedir conclusão se houver subtarefas pendentes
        if (taskData.getStatus() == Status.COMPLETED) {
            validateSubtasks(foundTask);
        }

        foundTask.setTitle(taskData.getTitle());
        foundTask.setDescription(taskData.getDescription());
        foundTask.setDueDate(taskData.getDueDate());
        foundTask.setStatus(taskData.getStatus());
        foundTask.setPriority(taskData.getPriority());

        return taskRepository.save(foundTask);
    }

    private void validateSubtasks(Task task) {
        if (task.getSubTasks() != null) {
            boolean hasPendingSubtasks = task.getSubTasks().stream().anyMatch(subtask -> subtask.getStatus() != Status.COMPLETED);
            if (hasPendingSubtasks) {
                throw new RuntimeException("Não é possível concluir a tarefa. Existe subtarefas pendentes.");
            }
        }
    }

    public void delete(Long id) {
        var foundTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada!"));

        taskRepository.delete(foundTask);
    }

    public Task updateStatus(Long id, Status newStatus) {
        Task foundTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada!"));
        if (newStatus == Status.COMPLETED) {
            validateSubtasks(foundTask);
        }

        foundTask.setStatus(newStatus);
        return taskRepository.save(foundTask);
    }

    public Task updatePriority(Long id, Priority newPriority) {
        Task foundTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada!"));

        foundTask.setPriority(newPriority);
        return taskRepository.save(foundTask);
    }

    public List<TaskResponseDTO> findUserTasksWithFilters(User authenticatedUser, Status status, Priority priority, LocalDate dueDate) {
        Specification<Task> specification = TaskSpecification.belongsToUser(authenticatedUser);

        if (status != null) {
            specification = specification.and(TaskSpecification.hasStatus(status));
        }
        if (priority != null) {
            specification = specification.and(TaskSpecification.hasPriority(priority));
        }
        if (dueDate != null) {
            specification = specification.and(TaskSpecification.hasDueDate(dueDate));
        }

        List<Task> userTasks = taskRepository.findAll(specification);

        return userTasks.stream()
                .map(TaskResponseDTO::new)
                .collect(Collectors.toList());
    }
}