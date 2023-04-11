package com.goldloancalculator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BanksList_Adapter extends RecyclerView.Adapter<BanksList_Adapter.MyViewHolder> {

    ArrayList<BankDetails> data;
    private final RecyclerViewClickListener listener;
    Context context;

    public BanksList_Adapter(ArrayList<BankDetails> data, RecyclerViewClickListener listener, Context context) {
        this.data = data;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public BanksList_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banklist_layout, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BanksList_Adapter.MyViewHolder holder, int position) {
        holder.bankName_txt.setText(data.get(position).getBankName());
        holder.apraisedAmount_txt.setText("" + data.get(position).getAppraisedAmount());
        holder.bankgivenAmount_txt.setText("" + data.get(position).getBankgivenAmount());

        holder.editBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DialogPlus dialog = DialogPlus.newDialog(holder.editBank.getContext())
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(new ViewHolder(R.layout.addbank_dialog))
                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();

                View holderView = (LinearLayout) dialog.getHolderView();

                final EditText Bankname = holderView.findViewById(R.id.dialog_bankname);
                final EditText Apraisedamount = holderView.findViewById(R.id.dialog_appraisedvalue);
                final EditText Bankgivenamount = holderView.findViewById(R.id.dialog_bankamokunt);


                Bankname.setText(data.get(holder.getAdapterPosition()).getBankName());
                Apraisedamount.setText("" + data.get(holder.getAdapterPosition()).getAppraisedAmount());
                Bankgivenamount.setText("" + data.get(holder.getAdapterPosition()).getBankgivenAmount());
                Button update = holderView.findViewById(R.id.Banksavebtn);
                update.setText("Update");

                dialog.show();

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("bankName", Bankname.getText().toString());
                        map.put("appraisedAmount", Integer.parseInt(String.valueOf(Apraisedamount.getText())));
                        map.put("bankgivenAmount", Integer.parseInt(String.valueOf(Bankgivenamount.getText())));

                        listener.onUpdateClick(map, data.get(holder.getAdapterPosition()).Key);
                        dialog.dismiss();
//                        FirebaseDatabase.getInstance().getReference("Users").child(UID).child("Banks").getRef(Key)
//                                .child(Objects.requireNonNull(getRef(holder.getAdapterPosition()).getKey())).updateChildren(map)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        dialog.dismiss();
//                                    }
//                                });
                    }
                });
            }
        });

        holder.deleteBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.deleteBank.getContext());
                builder.setTitle("Delete Panel");
                // builder.setMessage("Delete...?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDeleteClick(data.get(holder.getAdapterPosition()).Key);
                        dialogInterface.dismiss();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView bankName_txt, apraisedAmount_txt, bankgivenAmount_txt;
        ImageView editBank, deleteBank;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            bankName_txt = itemView.findViewById(R.id.Addbankname);
            apraisedAmount_txt = itemView.findViewById(R.id.bankappraisalAmount_txt);
            bankgivenAmount_txt = itemView.findViewById(R.id.bankgivenAmount_txt);
            editBank = itemView.findViewById(R.id.editBank);
            deleteBank = itemView.findViewById(R.id.deleteBank);
            itemView.setOnClickListener(this::onClick);
        }

        public void onClick(View v) {
            listener.onClick(data.get(getAdapterPosition()));
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(BankDetails model);
        void onDeleteClick(String key);
        void onUpdateClick(Map<String, Object> pos,String Key);

    }
}
