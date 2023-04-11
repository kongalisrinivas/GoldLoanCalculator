package com.goldloancalculator;

import android.os.Parcel;
import android.os.Parcelable;

public class Item_Model implements Parcelable {

    String itemName;
    String grossWeight;
    String netWeight;
    String ApraisalValue;
    String bankGivenAmount;
    String Karats;

    protected Item_Model(Parcel in) {
        itemName = in.readString();
        grossWeight = in.readString();
        netWeight = in.readString();
        ApraisalValue = in.readString();
        bankGivenAmount = in.readString();
        Karats = in.readString();
    }

    public static final Creator<Item_Model> CREATOR = new Creator<Item_Model>() {
        @Override
        public Item_Model createFromParcel(Parcel in) {
            return new Item_Model(in);
        }

        @Override
        public Item_Model[] newArray(int size) {
            return new Item_Model[size];
        }
    };

    public String getItemName() {
        return itemName;
    }

    public Item_Model(String itemName, String grossWeight, String netWeight, String apraisalValue, String bankGivenAmount, String Karats) {
        this.itemName = itemName;
        this.grossWeight = grossWeight;
        this.netWeight = netWeight;
        this.ApraisalValue = apraisalValue;
        this.bankGivenAmount = bankGivenAmount;
        this.Karats = Karats;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }

    public String getApraisalValue() {
        return ApraisalValue;
    }

    public void setApraisalValue(String apraisalValue) {
        ApraisalValue = apraisalValue;
    }

    public String getBankGivenAmount() {
        return bankGivenAmount;
    }

    public void setBankGivenAmount(String bankGivenAmount) {
        this.bankGivenAmount = bankGivenAmount;
    }
    public String getKarats() {
        return Karats;
    }

    public void setKarats(String karats) {
        Karats = karats;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemName);
        dest.writeString(grossWeight);
        dest.writeString(netWeight);
        dest.writeString(ApraisalValue);
        dest.writeString(bankGivenAmount);
        dest.writeString(Karats);
    }
}