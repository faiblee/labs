package ru.ssau.tk.faible.labs.ui.models;

import lombok.Data;

@Data
public class UserRegistrationDTO
{
    private String username;
    private String password;
    private String factory_type;
    private String role;

}
