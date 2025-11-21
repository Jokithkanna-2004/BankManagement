package com.securebank.bank.service;

import com.securebank.bank.dto.CustomerRequest;
import com.securebank.bank.dto.CustomerResponse;
import com.securebank.bank.dto.CustomerStatusUpdateRequest;
import com.securebank.bank.exception.ResourceNotFoundException;
import com.securebank.bank.model.Customer;
import com.securebank.bank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<CustomerResponse> getCustomers(boolean activeOnly) {
        List<Customer> customers = activeOnly
                ? customerRepository.findByActiveTrue()
                : customerRepository.findAll();

        return customers.stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerResponse getCustomer(Long id) {
        return toResponse(findCustomer(id));
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setActive(true);
        return toResponse(customerRepository.save(customer));
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = findCustomer(id);
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        return toResponse(customer);
    }

    public CustomerResponse updateStatus(Long id, CustomerStatusUpdateRequest request) {
        Customer customer = findCustomer(id);
        customer.setActive(request.getActive());
        return toResponse(customer);
    }

    private Customer findCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer %d not found".formatted(id)));
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.isActive()
        );
    }
}


