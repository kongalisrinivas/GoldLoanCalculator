package com.goldloancalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class ItemDetailspage extends AppCompatActivity {

    TextView totalAmount;
    EditText Itemname_Edt,Bankamount_Edt,Grossweight_Edt,Netweight_Edt,Appraisalvalue_Edt;
    String itemName,bankAmount,grossWeight,netWeight,appraisalValue;
    Button save_btn;
    Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detailspage);

        /* Itemname_Edt = (EditText) findViewById(R.id.edt_Itemname);
        Bankamount_Edt = (EditText)findViewById(R.id.edt_Bankamount);
        Grossweight_Edt = (EditText) findViewById(R.id.edt_Grossweight);
        Netweight_Edt = (EditText) findViewById(R.id.edt_Netweight);
        save_btn =  findViewById(R.id.saveBtn);
        Appraisalvalue_Edt = (EditText) findViewById(R.id.edt_Appraisalvalue);*/

        mtoolbar = findViewById(R.id.item_toolbar);
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemName = Itemname_Edt.getText().toString();
                bankAmount = Bankamount_Edt.getText().toString();
                grossWeight = Grossweight_Edt.getText().toString();
                netWeight = Netweight_Edt.getText().toString();
                appraisalValue = Appraisalvalue_Edt.getText().toString();

                ArrayList<String> details = new ArrayList<String>();
                details.add(itemName);
                details.add(bankAmount);
                details.add(appraisalValue);
                details.add(netWeight);
                details.add(grossWeight);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("details",details);
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        });
    }
}