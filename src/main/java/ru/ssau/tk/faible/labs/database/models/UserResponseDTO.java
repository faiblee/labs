package ru.ssau.tk.faible.labs.database.models;

import lombok.Data;

@Data
public class UserResponseDTO {
    private int id;
    private String username;
    private String factory_type;
    private String role;
}
