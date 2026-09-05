package com.cloudpilot.security;

import com.cloudpilot.config.SecurityConfig;
import com.cloudpilot.controller.AuditLogController;
import com.cloudpilot.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuditLogController.class})
@Import({SecurityConfig.class, JwtAuthFilter.class})
class SecurityRbacTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuditLogService auditLogService;

    @Test
    @WithMockUser(username = "customer@acme.com", roles = {"CUSTOMER"})
    void testCustomerRole_DeniedAccessToAuditLogs() throws Exception {
        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "agent@cloudpilot.io", roles = {"AGENT"})
    void testAgentRole_AllowedAuditLogs() throws Exception {
        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@cloudpilot.io", roles = {"ADMIN"})
    void testAdminRole_AllowedAuditLogs() throws Exception {
        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isOk());
    }
}
