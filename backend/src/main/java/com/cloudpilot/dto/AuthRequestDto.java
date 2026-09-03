package com.cloudpilot.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequestDto {

    @NotBlank(message = "Username/Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String name;
    private String role;

    public AuthRequestDto() {}

    public AuthRequestDto(String email, String password, String name, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
