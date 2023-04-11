package com.goldloancalculator.interfaces;

import com.goldloancalculator.Item_Model;

public interface OnLoanItemClick {
    void onItemClick(Item_Model model, int pos);
    void onItemDeleteClick(int pos);
}
