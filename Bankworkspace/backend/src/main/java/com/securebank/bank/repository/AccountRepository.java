package com.securebank.bank.repository;

import com.securebank.bank.model.Account;
import com.securebank.bank.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomer(Customer customer);
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findTopByOrderByIdDesc();
}

