package com.migration.domain.enums;

public enum BankAccountType {

    CC_PJ("Conta Corrente Juridica"),
    CC_PF("Conta Corrente Pessoa Fisica"),
    CP("Conta Poupança"),
    CC_JOINT_ACCOUNT("Conta Corrente conjunta"),
    CC_STUDENT("Conta Corrente estudante");

    public final String description;

    BankAccountType(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }

}