package com.cloudpilot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Agent> agents = new ArrayList<>();

    public Team() {}

    public Team(Long id, String name, String description, ZonedDateTime createdAt, List<Agent> agents) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        if (agents != null) this.agents = agents;
    }

    public static TeamBuilder builder() { return new TeamBuilder(); }

    public static class TeamBuilder {
        private Long id;
        private String name;
        private String description;
        private ZonedDateTime createdAt;
        private List<Agent> agents = new ArrayList<>();

        public TeamBuilder id(Long id) { this.id = id; return this; }
        public TeamBuilder name(String name) { this.name = name; return this; }
        public TeamBuilder description(String description) { this.description = description; return this; }
        public TeamBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TeamBuilder agents(List<Agent> agents) { this.agents = agents; return this; }

        public Team build() {
            return new Team(id, name, description, createdAt, agents);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public List<Agent> getAgents() { return agents; }
    public void setAgents(List<Agent> agents) { this.agents = agents; }
}
