package ru.ssau.tk.faible.labs.database.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithPassword {
    private int id;
    private String username;
    private String password;
    private String factory_type;
    private String role;
}
