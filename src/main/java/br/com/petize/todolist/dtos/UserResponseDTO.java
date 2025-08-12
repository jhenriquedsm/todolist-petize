package br.com.petize.todolist.dtos;

import br.com.petize.todolist.model.User;

public record UserResponseDTO(Long id, String email) {
    public UserResponseDTO(User user) {
        this(
                user.getId(),
                user.getEmail()
        );
    }
}
