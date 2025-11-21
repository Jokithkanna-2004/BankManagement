package com.securebank.bank.controller;

import com.securebank.bank.dto.CustomerRequest;
import com.securebank.bank.dto.CustomerResponse;
import com.securebank.bank.dto.CustomerStatusUpdateRequest;
import com.securebank.bank.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Validated
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public List<CustomerResponse> getCustomers(@RequestParam(defaultValue = "false") boolean activeOnly) {
        return customerService.getCustomers(activeOnly);
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomer(@PathVariable Long id) {
        return customerService.getCustomer(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(@Valid @RequestBody CustomerRequest request) {
        return customerService.createCustomer(request);
    }

    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return customerService.updateCustomer(id, request);
    }

    @PatchMapping("/{id}/status")
    public CustomerResponse updateStatus(@PathVariable Long id,
                                         @Valid @RequestBody CustomerStatusUpdateRequest request) {
        return customerService.updateStatus(id, request);
    }
}


