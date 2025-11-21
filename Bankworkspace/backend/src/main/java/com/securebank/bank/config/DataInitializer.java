package com.securebank.bank.config;

import com.securebank.bank.model.Account;
import com.securebank.bank.model.BankTransaction;
import com.securebank.bank.model.Customer;
import com.securebank.bank.repository.AccountRepository;
import com.securebank.bank.repository.BankTransactionRepository;
import com.securebank.bank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@Profile("!prod")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final BankTransactionRepository transactionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            return;
        }

        Customer john = createCustomer("John Doe", "john@example.com", "555-0101");
        Customer jane = createCustomer("Jane Smith", "jane@example.com", "555-0102");
        Customer bob = createCustomer("Bob Johnson", "bob@example.com", "555-0103");

        customerRepository.saveAll(List.of(john, jane, bob));

        Account acc001 = createAccount(john, "ACC001", "SAVINGS", new BigDecimal("5000.00"), LocalDate.of(2024, 1, 15));
        Account acc002 = createAccount(john, "ACC002", "CURRENT", new BigDecimal("3000.00"), LocalDate.of(2024, 2, 20));
        Account acc003 = createAccount(jane, "ACC003", "SAVINGS", new BigDecimal("10000.00"), LocalDate.of(2024, 1, 10));
        Account acc004 = createAccount(bob, "ACC004", "CURRENT", new BigDecimal("7500.00"), LocalDate.of(2024, 3, 5));

        accountRepository.saveAll(List.of(acc001, acc002, acc003, acc004));

        createSampleTransactions(acc001, "TXN0001", LocalDate.of(2024, 1, 16), new BigDecimal("5000.00"), new BigDecimal("1000.00"), BigDecimal.ZERO, "Salary credit");
        createSampleTransactions(acc001, "TXN0002", LocalDate.of(2024, 1, 20), new BigDecimal("6000.00"), BigDecimal.ZERO, new BigDecimal("500.00"), "ATM withdrawal");

        createSampleTransactions(acc002, "TXN0003", LocalDate.of(2024, 2, 22), new BigDecimal("3000.00"), BigDecimal.ZERO, new BigDecimal("250.00"), "Bill payment");

        createSampleTransactions(acc003, "TXN0004", LocalDate.of(2024, 1, 12), new BigDecimal("10000.00"), new BigDecimal("2000.00"), BigDecimal.ZERO, "Bonus credit");

        createSampleTransactions(acc004, "TXN0005", LocalDate.of(2024, 3, 7), new BigDecimal("7500.00"), BigDecimal.ZERO, new BigDecimal("1000.00"), "Rent transfer");
    }

    private Customer createCustomer(String name, String email, String phone) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setActive(true);
        return customer;
    }

    private Account createAccount(Customer customer, String number, String type, BigDecimal balance, LocalDate createdDate) {
        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountNumber(number);
        account.setAccountType(type);
        account.setBalance(balance);
        account.setCreatedDate(createdDate);
        return account;
    }

    private void createSampleTransactions(Account account,
                                          String txnRef,
                                          LocalDate date,
                                          BigDecimal opening,
                                          BigDecimal credit,
                                          BigDecimal debit,
                                          String remark) {
        BankTransaction txn = new BankTransaction();
        txn.setAccount(account);
        txn.setTxnRef(txnRef);
        txn.setDate(date);
        txn.setOpeningBalance(opening);
        txn.setCreditAmount(credit);
        txn.setDebitAmount(debit);
        BigDecimal closing = opening.add(credit).subtract(debit);
        txn.setClosingBalance(closing);
        txn.setRemark(remark);
        transactionRepository.save(txn);
    }
}


