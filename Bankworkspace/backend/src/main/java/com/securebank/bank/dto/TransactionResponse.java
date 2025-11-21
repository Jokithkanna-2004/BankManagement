package com.securebank.bank.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private LocalDate date;
    private String txnRef;
    private BigDecimal openingBalance;
    private BigDecimal creditAmount;
    private BigDecimal debitAmount;
    private BigDecimal closingBalance;
    private String remark;
}


