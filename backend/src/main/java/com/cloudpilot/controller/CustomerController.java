package com.cloudpilot.controller;

import com.cloudpilot.dto.Customer360Dto;
import com.cloudpilot.model.Customer;
import com.cloudpilot.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Customer profiles and Customer 360 intelligence")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get basic customer profile by ID")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/{id}/360")
    @Operation(summary = "Get consolidated Customer 360 view with spend, activity, and AI summary")
    public ResponseEntity<Customer360Dto> getCustomer360(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomer360(id));
    }

    @GetMapping
    @Operation(summary = "List all customers (paginated)")
    public ResponseEntity<Page<Customer>> listCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(customerService.getAllCustomers(PageRequest.of(page, size)));
    }
}
