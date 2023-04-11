package com.goldloancalculator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.orhanobut.dialogplus.OnClickListener;

import java.util.ArrayList;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.MyViewHolder> {

    ArrayList<Record_Model> data;
    private final RecyclerViewClickListener listener;
    Context context;

    public RecordsAdapter(ArrayList<Record_Model> data, RecyclerViewClickListener listener, Context context) {
        this.data = data;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public RecordsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recordlist_layout, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecordsAdapter.MyViewHolder holder, int position) {
        holder.itemCount.setText(data.get(position).getItemCount());
        holder.customername.setText(data.get(position).getCustomerName());
        holder.cust_mobilenumber.setText(data.get(position).getMobileNumber());
        holder.bankName.setText(data.get(position).getBankName());
        holder.createdDate.setText(data.get(position).getCreatedDate());

        holder.cust_mobilenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+data.get(holder.getAdapterPosition()).getMobileNumber()));//change the number
                context.startActivity(callIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView itemCount, customername, cust_mobilenumber,bankName,createdDate;
        CardView recordCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recordCard = itemView.findViewById(R.id.card_record);
            itemCount = itemView.findViewById(R.id.itemCount_txt);
            customername = itemView.findViewById(R.id.customername_txt);
            cust_mobilenumber = itemView.findViewById(R.id.mobileNumber_txt);
            bankName = itemView.findViewById(R.id.bankName);
            createdDate = itemView.findViewById(R.id.createdDate);
            itemView.setOnClickListener(this::onClick);
        }

        public void onClick(View v) {
            listener.onClick(data.get(getAdapterPosition()));
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(Record_Model model);
    }
}
