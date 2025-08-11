package br.com.petize.todolist.model;

import br.com.petize.todolist.model.enums.Priority;
import br.com.petize.todolist.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório!")
    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 500)
    private String description;

    @FutureOrPresent(message = "A data de vencimento não pode ser no passado!")
    @NotNull(message = "A data de vencimento é obrigatória!")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull(message = "O status é obrigatório!")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @NotNull(message = "A prioridade é obrigatória!")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority;

    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    @JsonBackReference
    private Task parentTask;

    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Task> subTasks;
}