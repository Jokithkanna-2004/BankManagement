package com.securebank.bank.repository;

import com.securebank.bank.model.Account;
import com.securebank.bank.model.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findByAccountOrderByDateDescIdDesc(Account account);
}

