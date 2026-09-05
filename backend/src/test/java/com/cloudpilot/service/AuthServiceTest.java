package com.cloudpilot.service;

import com.cloudpilot.dto.AuthRequestDto;
import com.cloudpilot.dto.AuthResponseDto;
import com.cloudpilot.model.Customer;
import com.cloudpilot.repository.CustomerRepository;
import com.cloudpilot.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(customerRepository, jwtUtil, passwordEncoder, auditLogService);
    }

    @Test
    void testLogin_AdminEmailResolvesAdminRole() {
        AuthRequestDto request = new AuthRequestDto("admin@cloudpilot.io", "password", null, null);
        when(jwtUtil.generateToken(0L, "admin@cloudpilot.io", "ADMIN")).thenReturn("mock-admin-jwt");
        when(jwtUtil.generateRefreshToken(0L, "admin@cloudpilot.io")).thenReturn("mock-admin-refresh");

        AuthResponseDto response = authService.login(request);

        assertNotNull(response);
        assertEquals("ADMIN", response.getRole());
        assertEquals("mock-admin-jwt", response.getAccessToken());
    }

    @Test
    void testLogin_AgentEmailResolvesAgentRole() {
        AuthRequestDto request = new AuthRequestDto("alex@cloudpilot.io", "password", null, null);
        when(jwtUtil.generateToken(1L, "alex@cloudpilot.io", "AGENT")).thenReturn("mock-agent-jwt");
        when(jwtUtil.generateRefreshToken(1L, "alex@cloudpilot.io")).thenReturn("mock-agent-refresh");

        AuthResponseDto response = authService.login(request);

        assertNotNull(response);
        assertEquals("AGENT", response.getRole());
    }

    @Test
    void testRegister_HappyPath() {
        AuthRequestDto request = new AuthRequestDto("newuser@example.com", "password", "New User", null);
        when(customerRepository.findByEmailIgnoreCase("newuser@example.com")).thenReturn(Optional.empty());

        Customer savedCustomer = Customer.builder().id(99L).email("newuser@example.com").name("New User").build();
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(jwtUtil.generateToken(99L, "newuser@example.com", "CUSTOMER")).thenReturn("mock-cust-jwt");
        when(jwtUtil.generateRefreshToken(99L, "newuser@example.com")).thenReturn("mock-cust-refresh");

        AuthResponseDto response = authService.register(request);

        assertNotNull(response);
        assertEquals(99L, response.getUserId());
        assertEquals("CUSTOMER", response.getRole());
    }

    @Test
    void testRegister_DuplicateEmailThrowsException() {
        AuthRequestDto request = new AuthRequestDto("existing@example.com", "password", "Existing User", null);
        Customer existing = Customer.builder().id(1L).email("existing@example.com").build();
        when(customerRepository.findByEmailIgnoreCase("existing@example.com")).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }
}
