package com.cloudpilot.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "agents")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "skill_tags", nullable = false, columnDefinition = "TEXT")
    private String skillTags = "";

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.valueOf(5.00);

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "current_workload", nullable = false)
    private Integer currentWorkload = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    public Agent() {}

    public Agent(Long id, String name, String email, Team team, String skillTags, BigDecimal rating, Boolean isAvailable, Integer currentWorkload, ZonedDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.team = team;
        this.skillTags = skillTags != null ? skillTags : "";
        this.rating = rating != null ? rating : BigDecimal.valueOf(5.00);
        this.isAvailable = isAvailable != null ? isAvailable : true;
        this.currentWorkload = currentWorkload != null ? currentWorkload : 0;
        this.createdAt = createdAt;
    }

    public static AgentBuilder builder() { return new AgentBuilder(); }

    public static class AgentBuilder {
        private Long id;
        private String name;
        private String email;
        private Team team;
        private String skillTags = "";
        private BigDecimal rating = BigDecimal.valueOf(5.00);
        private Boolean isAvailable = true;
        private Integer currentWorkload = 0;
        private ZonedDateTime createdAt;

        public AgentBuilder id(Long id) { this.id = id; return this; }
        public AgentBuilder name(String name) { this.name = name; return this; }
        public AgentBuilder email(String email) { this.email = email; return this; }
        public AgentBuilder team(Team team) { this.team = team; return this; }
        public AgentBuilder skillTags(String skillTags) { this.skillTags = skillTags; return this; }
        public AgentBuilder rating(BigDecimal rating) { this.rating = rating; return this; }
        public AgentBuilder isAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; return this; }
        public AgentBuilder currentWorkload(Integer currentWorkload) { this.currentWorkload = currentWorkload; return this; }
        public AgentBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Agent build() {
            return new Agent(id, name, email, team, skillTags, rating, isAvailable, currentWorkload, createdAt);
        }
    }

    public List<String> getSkillTagsList() {
        if (skillTags == null || skillTags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(skillTags.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public String getSkillTags() { return skillTags; }
    public void setSkillTags(String skillTags) { this.skillTags = skillTags; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Integer getCurrentWorkload() { return currentWorkload; }
    public void setCurrentWorkload(Integer currentWorkload) { this.currentWorkload = currentWorkload; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
