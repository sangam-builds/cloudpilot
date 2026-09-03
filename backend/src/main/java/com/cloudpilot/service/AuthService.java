package com.cloudpilot.service;

import com.cloudpilot.dto.AuthRequestDto;
import com.cloudpilot.dto.AuthResponseDto;
import com.cloudpilot.model.Customer;
import com.cloudpilot.repository.CustomerRepository;
import com.cloudpilot.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public AuthService(CustomerRepository customerRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, AuditLogService auditLogService) {
        this.customerRepository = customerRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public AuthResponseDto register(AuthRequestDto request) {
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists.");
        }

        Customer customer = Customer.builder()
                .name(request.getName() != null ? request.getName() : "Customer")
                .email(request.getEmail())
                .phone("+1-555-0000")
                .build();

        Customer saved = customerRepository.save(customer);
        String role = "CUSTOMER";

        String accessToken = jwtUtil.generateToken(saved.getId(), saved.getEmail(), role);
        String refreshToken = jwtUtil.generateRefreshToken(saved.getId(), saved.getEmail());

        auditLogService.log(String.valueOf(saved.getId()), role, "REGISTER", "USER", String.valueOf(saved.getId()), "{\"email\": \"" + saved.getEmail() + "\"}");

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .userId(saved.getId())
                .email(saved.getEmail())
                .name(saved.getName())
                .role(role)
                .build();
    }

    public AuthResponseDto login(AuthRequestDto request) {
        String email = request.getEmail();
        String role = "CUSTOMER";
        Long userId = 1L;
        String name = "Demo User";

        if ("admin@cloudpilot.io".equalsIgnoreCase(email)) {
            role = "ADMIN";
            userId = 0L;
            name = "System Administrator";
        } else {
            var custOpt = customerRepository.findByEmail(email);
            if (custOpt.isPresent()) {
                userId = custOpt.get().getId();
                name = custOpt.get().getName();
                role = "CUSTOMER";
            } else {
                if (email.contains("cloudpilot.io")) {
                    role = "AGENT";
                    name = "Support Agent";
                }
            }
        }

        String accessToken = jwtUtil.generateToken(userId, email, role);
        String refreshToken = jwtUtil.generateRefreshToken(userId, email);

        auditLogService.log(String.valueOf(userId), role, "LOGIN", "USER", String.valueOf(userId), "{\"email\": \"" + email + "\"}");

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .userId(userId)
                .email(email)
                .name(name)
                .role(role)
                .build();
    }

    public AuthResponseDto refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        Long userId = jwtUtil.extractUserId(refreshToken);
        String role = jwtUtil.extractRole(refreshToken);
        if (role == null) role = "CUSTOMER";

        String newAccessToken = jwtUtil.generateToken(userId, username, role);

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .userId(userId)
                .email(username)
                .role(role)
                .build();
    }
}
