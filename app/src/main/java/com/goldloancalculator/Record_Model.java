package com.goldloancalculator;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class Record_Model implements Parcelable {


    String title;
    String customerName;
    String mobileNumber;
    String itemCount;
    String totalBankGivenAmount;
    String totalApprisalAmount;
    String totalGrossWeight;
    String totalNetWeight;
    String bankName;
    String recordCount;
    String Key;
    String createdDate;
    ArrayList<Item_Model> item_model;

    public Record_Model(String title, String customerName, String mobileNumber, String itemCount, String totalBankGivenAmount, String totalApprisalAmount, String totalGrossWeight, String totalNetWeight, String bankName, String recordCount, String Key, String createdDate, ArrayList<Item_Model> item_model) {
        this.title = title;
        this.customerName = customerName;
        this.mobileNumber = mobileNumber;
        this.itemCount = itemCount;
        this.totalBankGivenAmount = totalBankGivenAmount;
        this.totalApprisalAmount = totalApprisalAmount;
        this.totalGrossWeight = totalGrossWeight;
        this.totalNetWeight = totalNetWeight;
        this.bankName = bankName;
        this.Key = Key;
        this.createdDate = createdDate;
        this.item_model = item_model;
        this.recordCount = recordCount;
    }

    public Record_Model() {
    }

    protected Record_Model(Parcel in) {
        title = in.readString();
        customerName = in.readString();
        mobileNumber = in.readString();
        itemCount = in.readString();
        totalBankGivenAmount = in.readString();
        totalApprisalAmount = in.readString();
        totalGrossWeight = in.readString();
        totalNetWeight = in.readString();
        bankName = in.readString();
        recordCount = in.readString();
        Key = in.readString();
        createdDate = in.readString();
        item_model = in.createTypedArrayList(Item_Model.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(customerName);
        dest.writeString(mobileNumber);
        dest.writeString(itemCount);
        dest.writeString(totalBankGivenAmount);
        dest.writeString(totalApprisalAmount);
        dest.writeString(totalGrossWeight);
        dest.writeString(totalNetWeight);
        dest.writeString(bankName);
        dest.writeString(recordCount);
        dest.writeString(Key);
        dest.writeString(createdDate);
        dest.writeTypedList(item_model);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Record_Model> CREATOR = new Creator<Record_Model>() {
        @Override
        public Record_Model createFromParcel(Parcel in) {
            return new Record_Model(in);
        }

        @Override
        public Record_Model[] newArray(int size) {
            return new Record_Model[size];
        }
    };

    public String getTotalGrossWeight() {
        return totalGrossWeight;
    }

    public void setTotalGrossWeight(String totalGrossWeight) {
        this.totalGrossWeight = totalGrossWeight;
    }

    public String getTotalNetWeight() {
        return totalNetWeight;
    }

    public void setTotalNetWeight(String totalNetWeight) {
        this.totalNetWeight = totalNetWeight;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getTotalBankGivenAmount() {
        return totalBankGivenAmount;
    }

    public void setTotalBankGivenAmount(String totalBankGivenAmount) {
        this.totalBankGivenAmount = totalBankGivenAmount;
    }

    public String getTotalApprisalAmount() {
        return totalApprisalAmount;
    }

    public void setTotalApprisalAmount(String totalApprisalAmount) {
        this.totalApprisalAmount = totalApprisalAmount;
    }

    public String getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(String recordCount) {
        this.recordCount = recordCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public ArrayList<Item_Model> getItem_model() {
        return item_model;
    }

    public void setItem_model(ArrayList<Item_Model> item_model) {
        this.item_model = item_model;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
