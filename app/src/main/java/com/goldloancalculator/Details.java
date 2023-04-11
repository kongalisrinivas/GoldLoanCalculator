package com.goldloancalculator;

import java.util.ArrayList;

public class Details {
    String Itemname, Netweight,Grossweight,itemCustomername,Cust_mobilenumber,itemCount,BankName,
     BankAmount,AppraisalValue;

    private ArrayList<SingleItem> singleItems = new ArrayList<>();

    public Details(){}


    public Details(String Itemname, String BankAmount, String AppraisalValue, String NetWeight, String Grossweight,ArrayList<SingleItem> singleItems ){
        this.Itemname = Itemname;
        this.BankAmount = BankAmount;
        this.AppraisalValue = AppraisalValue;
        this.Netweight = NetWeight;
        this.Grossweight = Grossweight;
        this.singleItems = singleItems;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getItemname() {
        return  Itemname;
    }

    public void setItemname(String Itemname) {
        this.Itemname = Itemname;
    }

    public String getBankAmount() {
        return  String.valueOf(BankAmount);
    }

    public void setBankAmount(String BankAmount) {
        this.BankAmount = BankAmount;
    }

    public String getAppraisalValue() {

        return  String.valueOf(AppraisalValue);
    }

    public void setAppraisalValue(String AppraisalValue) {
        this.AppraisalValue = AppraisalValue;
    }

    public String getNetweight() {
        return Netweight;
    }
    public void setNetweight(String Netweight){
        this.Netweight = Netweight;
    }

    public String getGrossweight() {
        return Grossweight;
    }

    public void setGrossweight(String Grossweight){
        this.Grossweight = Grossweight;
    }

    public String getItemCustomername() {
        return itemCustomername;
    }

    public void setItemCustomername(String itemCustomername) {
        this.itemCustomername = itemCustomername;
    }

    public String getCust_mobilenumber() {
        return Cust_mobilenumber;
    }

    public void setCust_mobilenumber(String cust_mobilenumber) {
        Cust_mobilenumber = cust_mobilenumber;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }


}
