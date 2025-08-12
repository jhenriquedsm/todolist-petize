package br.com.petize.todolist.service;

import br.com.petize.todolist.model.Task;
import br.com.petize.todolist.model.User;
import br.com.petize.todolist.model.enums.Status;
import br.com.petize.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Task parentTask;
    private Task pendingSubtask;
    private Task completedSubtask;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);

        parentTask = new Task();
        parentTask.setId(11L);
        parentTask.setUser(testUser);

        pendingSubtask = new Task();
        pendingSubtask.setStatus(Status.PENDING);

        completedSubtask = new Task();
        completedSubtask.setStatus(Status.COMPLETED);
    }

    @DisplayName("Quando uma tarefa com subtarefas pendentes tem o status atualizado para COMPLETED deve-se lançar uma exceção")
    @Test
    void testGivenPendingSubtasks_whenUpdateStatusToCompleted_thenThrowsException() {
        // GIVEN
        parentTask.setSubTasks(List.of(pendingSubtask));
        when(taskRepository.findById(11L)).thenReturn(Optional.of(parentTask));

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> {
            taskService.updateStatus(11L, Status.COMPLETED);
        });

        verify(taskRepository, never()).save(any(Task.class));
    }

    @DisplayName("Quando uma tarefa com subtarefas concluídas tem o status atualizado para COMPLETED a operação deve ser bem sucedida")
    @Test
    void testGivenTaskWithCompletedSubtasks_whenUpdateStatusToCompleted_thenSucceeds() {
        // GIVEN
        parentTask.setSubTasks(List.of(completedSubtask));
        when(taskRepository.findById(11L)).thenReturn(Optional.of(parentTask));

        // WHEN
        assertDoesNotThrow(() -> {
            taskService.updateStatus(11L, Status.COMPLETED);
        });

        // THEN
        verify(taskRepository, times(1)).save(parentTask);
    }
}