package com.goldloancalculator;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.goldloancalculator.interfaces.OnLoanItemClick;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.sql.DriverManager.println;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyHolderView> {

    ArrayList<Item_Model> data;
    static OnLoanItemClick onLoanItemListener;
    boolean isNewRecord;

    public ItemsAdapter(ArrayList<Item_Model> data, boolean isNewRecord) {
        this.data = data;
        this.isNewRecord = isNewRecord;
    }

    @NonNull
    @Override
    public ItemsAdapter.MyHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_adapter, parent, false);
        return new MyHolderView(view, data);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.MyHolderView holder, int position) {
        String itemName = data.get(position).getItemName().isEmpty()||data.get(position).getItemName() !=null ? data.get(position).getItemName() : "";
        String bankAmount = data.get(position).getBankGivenAmount() != null ? String.valueOf(data.get(position).getBankGivenAmount()) : "";
        String appraiserAmt = data.get(position).getApraisalValue() != null ? String.valueOf(data.get(position).getApraisalValue()) : "";
        String netWeight = data.get(position).getNetWeight()!= null ? String.valueOf(data.get(position).getNetWeight()) : "";
        String grossWeight = data.get(position).getGrossWeight()!=null?data.get(position).getGrossWeight():"";

        holder.txt_itemName.setText(itemName);
        holder.txt_bankAmount.setText(" \u20B9 " + String.format("%.1f", Double.parseDouble(bankAmount)));
        holder.txt_appraiserAmt.setText(" \u20B9 " + String.format("%.1f", Double.parseDouble(appraiserAmt)));
        holder.txt_netWeight.setText(String.format("%.1f", Double.parseDouble(netWeight)) + " gms");
        holder.txt_grossWeight.setText(Double.parseDouble(grossWeight) + " gms");

        if (isNewRecord) {
            holder.deleteItem.setVisibility(View.VISIBLE);
        } else {
            holder.deleteItem.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setClickListner(OnLoanItemClick clickListener) {
        onLoanItemListener = clickListener;
    }

    public static class MyHolderView extends RecyclerView.ViewHolder {

        TextView txt_itemName, txt_appraiserValue, txt_bankValue, txt_appraiserAmt, txt_bankAmount, txt_gross, txt_grossWeight,
                txt_net, txt_netWeight;

        ImageView deleteItem, editItem;

        public MyHolderView(@NonNull View itemView, ArrayList<Item_Model> data) {
            super(itemView);

            txt_itemName = itemView.findViewById(R.id.itemName_txt);
            txt_appraiserValue = itemView.findViewById(R.id.appraisalValue_txt);
            txt_bankValue = itemView.findViewById(R.id.bankValue_txt);
            txt_appraiserAmt = itemView.findViewById(R.id.appraisalAmount_txt);
            txt_bankAmount = itemView.findViewById(R.id.bankAmount_txt);
            //txt_gross = itemView.findViewById(R.id.grosssValue_txt);
            txt_grossWeight = itemView.findViewById(R.id.grossWeight_txt);
            txt_net = itemView.findViewById(R.id.NetweightValue_txt);
            txt_netWeight = itemView.findViewById(R.id.netWeight_txt);
            deleteItem = itemView.findViewById(R.id.deleteItem);
            //editItem = itemView.findViewById(R.id.editItem);

            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLoanItemListener.onItemDeleteClick(getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        onLoanItemListener.onItemClick(data.get(getAdapterPosition()), getAdapterPosition());
                    } catch (Exception e) {
                        println(e.getMessage());
                    }
                }
            });
        }
    }
}
