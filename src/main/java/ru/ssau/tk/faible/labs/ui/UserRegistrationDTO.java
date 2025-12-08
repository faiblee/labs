package ru.ssau.tk.faible.labs.ui;

public class UserRegistrationDTO
{
    private String username;
    private String password;
    private String factory_type;
    private String role;

    // Геттеры и сеттеры
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFactory_type() { return factory_type; }
    public void setFactory_type(String factoryType) { this.factory_type = factoryType; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
