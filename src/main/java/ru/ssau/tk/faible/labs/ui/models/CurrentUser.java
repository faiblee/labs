package ru.ssau.tk.faible.labs.ui.models;

import lombok.Data;

@Data
public class CurrentUser {
    private int id;
    private String username;
    private String role;
    private String factory_type;
    private String encodedCredentials;
}
