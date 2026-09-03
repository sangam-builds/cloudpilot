package com.cloudpilot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequestDto {

    @NotBlank(message = "Username/Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String name; // for registration

    private String role; // CUSTOMER, AGENT, ADMIN
}
