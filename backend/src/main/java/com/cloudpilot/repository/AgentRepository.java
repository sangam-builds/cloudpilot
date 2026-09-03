package com.cloudpilot.repository;

import com.cloudpilot.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findByEmail(String email);

    List<Agent> findByTeamId(Long teamId);

    List<Agent> findByTeamIdAndIsAvailableTrue(Long teamId);

    @Query("SELECT a FROM Agent a WHERE a.isAvailable = true AND LOWER(a.team.name) LIKE LOWER(CONCAT('%', :departmentKeyword, '%'))")
    List<Agent> findAvailableAgentsByDepartment(@Param("departmentKeyword") String departmentKeyword);
}
