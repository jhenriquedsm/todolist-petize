package br.com.petize.todolist.service;

import br.com.petize.todolist.model.Task;
import br.com.petize.todolist.model.enums.Priority;
import br.com.petize.todolist.model.enums.Status;
import br.com.petize.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Task create(Task task) {
        task.setId(null);
        if (task.getParentTask() != null && task.getParentTask().getId() != null) {
            Task parent = findById(task.getParentTask().getId());
            task.setParentTask(parent);
        }
        return taskRepository.save(task);
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
}