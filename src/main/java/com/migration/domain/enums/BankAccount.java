package com.migration.domain.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankAccount {

    private String financialInstitutionCode;
    private String accountBranch;
    private String accountNumber;
    private String accountDigit;
}
