package com.goldloancalculator;

public class BankDetails {
    String bankName;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    String Key;
    int appraisedAmount, bankgivenAmount;

    BankDetails() {
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public int getAppraisedAmount() {
        return appraisedAmount;
    }

    public void setAppraisedAmount(int appraisedAmount) {
        this.appraisedAmount = appraisedAmount;
    }

    public int getBankgivenAmount() {
        return bankgivenAmount;
    }

    public void setBankgivenAmount(int bankgivenAmount) {
        this.bankgivenAmount = bankgivenAmount;
    }
}
