package com.goldloancalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Addbank extends AppCompatActivity {

    FloatingActionButton addBankbtn;
    //BanksAdapter mAdapter;
    BanksList_Adapter banksAdapter;
    RecyclerView bankList;
    BankDetails bankDetails;
    TextView txt_Nobanks;
    ArrayList<BankDetails> list;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    Toolbar mtoolbar;
    EditText D_Bankname, D_Bankgivenamount, D_AppraisedValue;
    BanksList_Adapter.RecyclerViewClickListener mListener;
    ProgressBar progressBar;

    boolean isAdmin;
    String bankName, Key, appraisedAmount, bankgivenAmount;
    DatabaseReference reference;
    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbank);

        addBankbtn = findViewById(R.id.addBank_btn);
        bankList = findViewById(R.id.banklist);
        txt_Nobanks = findViewById(R.id.noBanks);
        mtoolbar = (Toolbar) findViewById(R.id.toolar);
        progressBar = findViewById(R.id.progressBar);

        bankList.setLayoutManager(new LinearLayoutManager(this));
        bankDetails = new BankDetails();

        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressBar.setVisibility(View.VISIBLE);

        UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("Banks");

        //        FirebaseRecyclerOptions<BankDetails> options =
        //                new FirebaseRecyclerOptions.Builder<BankDetails>()
        //                        .setQuery(reference, BankDetails.class)
        //                        .build();

        //        //Call our reference
        //        reference.addListenerForSingleValueEvent(
        //                new ValueEventListener () {
        //                    @Override
        //                    public void onDataChange(DataSnapshot dataSnapshot) {
        //                        // ...
        //                        Log.i("succ","kjsdf");
        //                    }
        //
        //                    @Override
        //                    public void onCancelled(DatabaseError databaseError) {
        //                        // Getting Post failed, log a message
        //                        Log.i("error","kjsdf");
        //                    }
        //                });

        //list = options.getSnapshots();
        //mAdapter = new BanksAdapter(options, listner, isAdmin);
        //mAdapter.startListening();
        //bankList.setAdapter(mAdapter);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {

                    list = new ArrayList<>();

                    for (DataSnapshot formsSnapshot : snapshot.getChildren()) {
                        BankDetails model = new BankDetails();

                        Key = formsSnapshot.getKey();
                        bankName = formsSnapshot.child("bankName").getValue().toString();
                        appraisedAmount = formsSnapshot.child("appraisedAmount").getValue().toString();
                        bankgivenAmount = formsSnapshot.child("bankgivenAmount").getValue().toString();

                        model.setBankName(bankName);
                        model.setAppraisedAmount(Integer.parseInt(appraisedAmount));
                        model.setBankgivenAmount(Integer.parseInt(bankgivenAmount));
                        model.setKey(Key);

                        list.add(model);
                    }
                    if (list.size() > 0) {
                        txt_Nobanks.setVisibility(View.GONE);
                        bankList.setVisibility(View.VISIBLE);
                        txt_Nobanks.setVisibility(View.GONE);
                        onItemClick();
                        banksAdapter = new BanksList_Adapter(list, mListener, Addbank.this);
                        bankList.setHasFixedSize(false);
                        bankList.setNestedScrollingEnabled(false);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        bankList.setLayoutManager(layoutManager);
                        layoutManager.setReverseLayout(true);
                        layoutManager.setStackFromEnd(true);
                        bankList.setAdapter(banksAdapter);
                    } else {
                        bankList.setVisibility(View.GONE);
                        txt_Nobanks.setVisibility(View.VISIBLE);
                    }

                } else {
                    txt_Nobanks.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });


        addBankbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(Addbank.this);
                View mView = getLayoutInflater().inflate(R.layout.addbank_dialog, null);
                Button D_savebtn = mView.findViewById(R.id.Banksavebtn);
                D_AppraisedValue = mView.findViewById(R.id.dialog_appraisedvalue);
                D_Bankgivenamount = mView.findViewById(R.id.dialog_bankamokunt);
                D_Bankname = mView.findViewById(R.id.dialog_bankname);

                dialog.setView(mView);

                final AlertDialog alertDialog = dialog.create();
                alertDialog.setCanceledOnTouchOutside(false);

                alertDialog.show();

                D_savebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String add_bankname = D_Bankname.getText().toString();
                        int add_appraisedvalue = Integer.parseInt(D_AppraisedValue.getText().toString());
                        int add_bankgivenAmount = Integer.parseInt(D_Bankgivenamount.getText().toString());

                        Map<String, Object> map = new HashMap<>();
                        map.put("bankName", D_Bankname.getText().toString());
                        map.put("appraisedAmount", Integer.parseInt(D_AppraisedValue.getText().toString()));
                        map.put("bankgivenAmount", Integer.parseInt(D_Bankgivenamount.getText().toString()));


                        if (isAllFieldsChecked()) {

                            reference.push()
                                    .setValue(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            bankDetails.setBankName(add_bankname);
                                            bankDetails.setAppraisedAmount(add_appraisedvalue);
                                            bankDetails.setBankgivenAmount(add_bankgivenAmount);
                                            alertDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Inserted Successfully", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Could not insert", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }

                    private boolean isAllFieldsChecked() {
                        if (D_Bankname.length() == 0) {
                            D_Bankname.setError("This field is required");
                            return false;
                        }
                        if (D_AppraisedValue.length() == 0) {
                            D_AppraisedValue.setError("This field is required");
                            return false;
                        }
                        if (D_Bankgivenamount.length() == 0) {
                            D_Bankgivenamount.setError("This field is required");
                            return false;
                        }
                        return true;
                    }
                });

            }
        });
    }

    private void onItemClick() {
        mListener = new BanksList_Adapter.RecyclerViewClickListener() {
            @Override
            public void onClick(BankDetails model) {
                String bankName = model.getBankName();
                int apraisedAmount = model.getAppraisedAmount();
                int bankgivenAmount = model.getBankgivenAmount();
                Intent intnt = new Intent();
                intnt.putExtra("Bankname", bankName);
                intnt.putExtra("Apraisedamount", apraisedAmount);
                intnt.putExtra("Bankgivenamount", bankgivenAmount);
                setResult(2, intnt);
                finish();
            }

            @Override
            public void onDeleteClick(String Key) {
                reference.child(Key).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Addbank.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Addbank.this, "Error while deleting", Toast.LENGTH_SHORT).show();

                            }
                        });

            }

            @Override
            public void onUpdateClick(Map<String, Object> map, String Key) {

                reference.child(Key)
                        .updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Addbank.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Addbank.this, "Error while updating", Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }
                        });
            }
        };
    }
}