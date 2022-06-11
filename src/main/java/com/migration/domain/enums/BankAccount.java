package com.migration.domain.enums;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name="credi_account")
public class BankAccount  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "financial_institution_code")
    private String financialInstitutionCode;

    @Column(name = "account_branch")
    private String accountBranch;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_digit")
    private String accountDigit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(financialInstitutionCode, that.financialInstitutionCode) && Objects.equals(accountBranch, that.accountBranch) && Objects.equals(accountNumber, that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(financialInstitutionCode, accountBranch, accountNumber);
    }
}