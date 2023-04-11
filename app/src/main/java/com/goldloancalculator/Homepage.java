package com.goldloancalculator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import io.grpc.android.BuildConfig;

public class Homepage extends AppCompatActivity {
    FloatingActionButton addItems_btn;
    RecordsAdapter.RecyclerViewClickListener mlistener;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressBar progressBar;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    RecordsAdapter recordsAdapter;
    RecyclerView recordsList;
    ArrayList<Record_Model> allRecordList;
    TextView noRecords;
    String bankName, customerName, mobileNumber, totalapraisalAmount, totalbankAmount, title, itemCount, Key, recordCount, totalGrossWeight, totalNetWeight, createdDate;
    ArrayList<Item_Model> allItem_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        addItems_btn = findViewById(R.id.addItem_btn);
        recordsList = findViewById(R.id.recordList);
        recordsList.setLayoutManager(new LinearLayoutManager(this));
        noRecords = findViewById(R.id.noRecords);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        addItems_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser mUser = mAuth.getCurrentUser();
                if (mUser != null) {
                    Intent intent = new Intent(getApplicationContext(), LoanItemspage.class);
                    intent.putExtra("model", "null");
                    startActivity(intent);
                }
            }
        });

        if (mUser != null) {

            progressBar.setVisibility(View.VISIBLE);

            String UID = mUser.getUid();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(UID).child("Records");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {

                        allRecordList = new ArrayList<>();

                        for (DataSnapshot formsSnapshot : snapshot.getChildren()) {
                            Record_Model model = new Record_Model();
                            Key = formsSnapshot.getKey();
                            bankName = formsSnapshot.child("bankName").getValue().toString();
                            customerName = formsSnapshot.child("customerName").getValue().toString();
                            itemCount = formsSnapshot.child("itemCount").getValue().toString();
                            mobileNumber = formsSnapshot.child("mobileNumber").getValue().toString();
                            totalapraisalAmount = formsSnapshot.child("totalApprisalAmount").getValue().toString();
                            totalbankAmount = formsSnapshot.child("totalBankGivenAmount").getValue().toString();
                            recordCount = formsSnapshot.child("recordCount").getValue().toString();
                            title = formsSnapshot.child("title").getValue().toString();
                            totalGrossWeight = formsSnapshot.child("totalGrossWeight").getValue().toString();
                            totalNetWeight = formsSnapshot.child("totalNetWeight").getValue().toString();
                            try {
                                createdDate = formsSnapshot.child("createdDate").getValue().toString();
                            }catch (Exception e){
                                createdDate = "";
                            }


                            model.setBankName(bankName);
                            model.setCustomerName(customerName);
                            model.setItemCount(itemCount);
                            model.setMobileNumber(mobileNumber);
                            model.setTotalApprisalAmount(String.format("%.1f", Double.parseDouble(totalapraisalAmount)));
                            model.setTotalBankGivenAmount(String.format("%.1f", Double.parseDouble(totalbankAmount)));
                            model.setTitle(title);
                            model.setTotalGrossWeight(String.format("%.1f", Double.parseDouble(totalGrossWeight)));
                            model.setTotalNetWeight(String.format("%.1f", Double.parseDouble(totalNetWeight)));
                            model.setRecordCount(recordCount);
                            model.setCreatedDate(createdDate);
                            model.setKey(Key);

                            allItem_list = new ArrayList<>();
                            for (DataSnapshot snap : formsSnapshot.child("item_model").getChildren()) {
                                Item_Model childModel = new Item_Model(
                                       snap.child("itemName").getValue().toString(),
                                        String.format("%.1f", Double.parseDouble(snap.child("grossWeight").getValue().toString())),
                                        String.format("%.1f", Double.parseDouble(snap.child("netWeight").getValue().toString())),
                                                String.format("%.1f", Double.parseDouble(snap.child("apraisalValue").getValue().toString())),
                                        String.format("%.1f", Double.parseDouble(snap.child("bankGivenAmount").getValue().toString())),
                                        String.format("%.1f", Double.parseDouble(snap.child("karats").getValue().toString())));
                                allItem_list.add(childModel);
                            }
                            model.setItem_model(allItem_list);
                            allRecordList.add(model);
                        }
                        if (allRecordList.size() > 0) {
                            onItemClickListener();
                            noRecords.setVisibility(View.GONE);
                            recordsAdapter = new RecordsAdapter(allRecordList, mlistener, Homepage.this);
                            recordsList.setHasFixedSize(false);
                            recordsList.setNestedScrollingEnabled(false);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                            recordsList.setLayoutManager(layoutManager);
                            layoutManager.setReverseLayout(true);
                            layoutManager.setStackFromEnd(true);
                            recordsList.setAdapter(recordsAdapter);
                        } else {
                            noRecords.setVisibility(View.VISIBLE);
                        }

                    } else {
                        noRecords.setVisibility(View.VISIBLE);
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            startActivity(new Intent(Homepage.this, LoginActivity.class));
            finish();
        }

    }

    private void onItemClickListener() {
        mlistener = new RecordsAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(Record_Model model) {
                Intent i = new Intent(Homepage.this, LoanItemspage.class);
                i.putExtra("model", model);
                startActivity(i);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                Intent i = new Intent(Homepage.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            case R.id.share:
                shareApp();
                break;

            case R.id.contact:
                ContactUsDialod();
                break;
        }
        return true;
    }

    private void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Appraiser Calculator");
            String shareMessage = "Hello\n\n" + "By using this application appraiser can calculate loan amount based on gold items given by customers.\n\nCalculate loan amount by entering item name and grams\n\n Download the application\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=com.goldloancalculator";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private void ContactUsDialod() {


        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.contact_us_dialog_layout, null);

        Button closeBtn = mView.findViewById(R.id.closeBtn);
        TextView mobile = mView.findViewById(R.id.mobile);
        dialog.setView(mView);


        final AlertDialog alertDialog = dialog.create();
        alertDialog.setCanceledOnTouchOutside(false);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:8341346994"));//change the number
                startActivity(callIntent);
            }
        });

        alertDialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mUserr = mAuth.getCurrentUser();
        if (mUserr == null) {
            //startActivity(new Intent(Homepage.this,MainActivity.class));
            finish();
        }
    }
}