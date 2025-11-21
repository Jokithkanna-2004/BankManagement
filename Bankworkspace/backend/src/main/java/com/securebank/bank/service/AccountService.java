package com.securebank.bank.service;

import com.securebank.bank.dto.AccountRequest;
import com.securebank.bank.dto.AccountResponse;
import com.securebank.bank.dto.CustomerSummary;
import com.securebank.bank.dto.TransactionRequest;
import com.securebank.bank.dto.TransactionResponse;
import com.securebank.bank.exception.BadRequestException;
import com.securebank.bank.exception.ResourceNotFoundException;
import com.securebank.bank.model.Account;
import com.securebank.bank.model.BankTransaction;
import com.securebank.bank.model.Customer;
import com.securebank.bank.repository.AccountRepository;
import com.securebank.bank.repository.BankTransactionRepository;
import com.securebank.bank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final BankTransactionRepository transactionRepository;

    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public AccountResponse getAccount(Long id) {
        return toResponse(findAccount(id));
    }

    public List<AccountResponse> getCustomerAccounts(Long customerId) {
        Customer customer = findCustomer(customerId);
        return accountRepository.findByCustomer(customer).stream()
                .map(this::toResponse)
                .toList();
    }

    public AccountResponse createAccount(AccountRequest request) {
        Customer customer = findCustomer(request.getCustomerId());
        if (!customer.isActive()) {
            throw new BadRequestException("Cannot create account for inactive customer");
        }

        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountType(request.getAccountType().toUpperCase());
        account.setAccountNumber(generateAccountNumber());
        account.setCreatedDate(LocalDate.now());
        account.setBalance(request.getInitialDeposit());

        Account saved = accountRepository.save(account);

        BankTransaction txn = new BankTransaction();
        txn.setAccount(saved);
        populateTransaction(txn,
                BigDecimal.ZERO,
                request.getInitialDeposit(),
                BigDecimal.ZERO,
                "Initial deposit");

        transactionRepository.save(txn);
        return toResponse(saved);
    }

    public AccountResponse deposit(Long accountId, TransactionRequest request) {
        Account account = findAccount(accountId);
        BigDecimal amount = request.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }

        BankTransaction txn = new BankTransaction();
        txn.setAccount(account);
        populateTransaction(txn,
                account.getBalance(),
                amount,
                BigDecimal.ZERO,
                request.getRemark() == null || request.getRemark().isBlank() ? "Deposit" : request.getRemark());

        account.setBalance(account.getBalance().add(amount));
        txn.setClosingBalance(account.getBalance());
        transactionRepository.save(txn);
        return toResponse(account);
    }

    public AccountResponse withdraw(Long accountId, TransactionRequest request) {
        Account account = findAccount(accountId);
        BigDecimal amount = request.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        BankTransaction txn = new BankTransaction();
        txn.setAccount(account);
        populateTransaction(txn,
                account.getBalance(),
                BigDecimal.ZERO,
                amount,
                request.getRemark() == null || request.getRemark().isBlank() ? "Withdrawal" : request.getRemark());

        account.setBalance(account.getBalance().subtract(amount));
        txn.setClosingBalance(account.getBalance());
        transactionRepository.save(txn);
        return toResponse(account);
    }

    public List<TransactionResponse> getAccountTransactions(Long accountId) {
        Account account = findAccount(accountId);
        return transactionRepository.findByAccountOrderByDateDescIdDesc(account).stream()
                .map(this::toTransactionResponse)
                .toList();
    }

    private Customer findCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer %d not found".formatted(id)));
    }

    private Account findAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account %d not found".formatted(id)));
    }

    private String generateAccountNumber() {
        long nextId = accountRepository.findTopByOrderByIdDesc()
                .map(Account::getId)
                .orElse(0L) + 1;
        return "ACC" + String.format("%03d", nextId);
    }

    private String generateTxnRef() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void populateTransaction(BankTransaction txn,
                                     BigDecimal openingBalance,
                                     BigDecimal creditAmount,
                                     BigDecimal debitAmount,
                                     String remark) {
        txn.setDate(LocalDate.now());
        txn.setTxnRef(generateTxnRef());
        txn.setOpeningBalance(openingBalance);
        txn.setCreditAmount(creditAmount);
        txn.setDebitAmount(debitAmount);
        txn.setClosingBalance(openingBalance.add(creditAmount).subtract(debitAmount));
        txn.setRemark(remark);
    }

    private AccountResponse toResponse(Account account) {
        CustomerSummary customer = new CustomerSummary(
                account.getCustomer().getId(),
                account.getCustomer().getName(),
                account.getCustomer().getEmail(),
                account.getCustomer().getPhone(),
                account.getCustomer().isActive()
        );
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getCreatedDate(),
                customer
        );
    }

    private TransactionResponse toTransactionResponse(BankTransaction txn) {
        return new TransactionResponse(
                txn.getId(),
                txn.getDate(),
                txn.getTxnRef(),
                txn.getOpeningBalance(),
                txn.getCreditAmount(),
                txn.getDebitAmount(),
                txn.getClosingBalance(),
                txn.getRemark()
        );
    }
}


