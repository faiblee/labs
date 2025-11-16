package ru.ssau.tk.faible.labs.database.models;

public class User {
    private int id;
    private String username;
    private String password_hash;
    private String factory_type;
    private String role;

    public User(int id, String username, String password_hash, String factory_type, String role) {
        this.id = id;
        this.username = username;
        this.password_hash = password_hash;
        this.factory_type = factory_type;
        this.role = role;
    }

    public User() {
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getFactory_type() {
        return factory_type;
    }

    public void setFactory_type(String factory_type) {
        this.factory_type = factory_type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
