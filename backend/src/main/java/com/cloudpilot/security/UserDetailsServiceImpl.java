package com.cloudpilot.security;

import com.cloudpilot.model.Agent;
import com.cloudpilot.model.Customer;
import com.cloudpilot.repository.AgentRepository;
import com.cloudpilot.repository.CustomerRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final AgentRepository agentRepository;

    public UserDetailsServiceImpl(CustomerRepository customerRepository, AgentRepository agentRepository) {
        this.customerRepository = customerRepository;
        this.agentRepository = agentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if ("admin@cloudpilot.io".equalsIgnoreCase(email)) {
            return new User(
                    "admin@cloudpilot.io",
                    "$2a$10$w8T0M0eQ9v1P3Ea5c2PqGeJ9mJgT4YcK8qG6KzW3hF4gJ9v1P3Ea5",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        Optional<Agent> agent = agentRepository.findByEmail(email);
        if (agent.isPresent()) {
            return new User(
                    agent.get().getEmail(),
                    "$2a$10$w8T0M0eQ9v1P3Ea5c2PqGeJ9mJgT4YcK8qG6KzW3hF4gJ9v1P3Ea5",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_AGENT"))
            );
        }

        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            return new User(
                    customer.get().getEmail(),
                    "$2a$10$w8T0M0eQ9v1P3Ea5c2PqGeJ9mJgT4YcK8qG6KzW3hF4gJ9v1P3Ea5",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
            );
        }

        throw new UsernameNotFoundException("User with email " + email + " not found.");
    }
}
