package ru.ssau.tk.faible.labs.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "functions")
public class FunctionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PointEntity> points = new ArrayList<>();

    public FunctionEntity() {}

    public FunctionEntity(String name, String type, User owner) {
        this.name = name;
        this.type = type;
        this.owner = owner;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public List<PointEntity> getPoints() { return points; }
    public void setPoints(List<PointEntity> points) { this.points = points; }

    public void addPoint(PointEntity point) {
        points.add(point);
        point.setFunction(this);
    }
}