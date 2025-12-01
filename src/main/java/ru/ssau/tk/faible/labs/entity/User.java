// src/main/java/ru/ssau/tk/faible/labs/entity/User.java
package ru.ssau.tk.faible.labs.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "factory_type")
    private String factoryType;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FunctionEntity> functions = new ArrayList<>();

    public User() {}

    public User(String username, String passwordHash, String factoryType, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.factoryType = factoryType;
        this.role = role;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFactoryType() { return factoryType; }
    public void setFactoryType(String factoryType) { this.factoryType = factoryType; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public List<FunctionEntity> getFunctions() { return functions; }
    public void setFunctions(List<FunctionEntity> functions) { this.functions = functions; }
}