package com.goldloancalculator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

public class UsersAdapter extends FirebaseRecyclerAdapter<UserInfo, UsersAdapter.myviewholder> {

    Context context;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UsersAdapter(@NonNull FirebaseRecyclerOptions<UserInfo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UsersAdapter.myviewholder holder, @SuppressLint("RecyclerView") int position, @NonNull UserInfo model) {
        holder.username.setText(model.getUsername());
        holder.emailid.setText(model.getEmailid());
        holder.password.setText(model.getPassword());
        holder.mobilenumber.setText(model.getMobilenumber());
        holder.location.setText(model.getLocation());

        holder.updateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("UsersAdapter", "Update Tapped");
                final DialogPlus dialog = DialogPlus.newDialog(holder.updateUser.getContext())
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(new ViewHolder(R.layout.layout_add_user_dialog))
                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();

                View holderView = (ScrollView) dialog.getHolderView();

                final EditText userName = holderView.findViewById(R.id.username_txt);
                final EditText emailId = holderView.findViewById(R.id.emailId_txt);
                final EditText password = holderView.findViewById(R.id.createpassword_txt);
                final EditText mobileNumber = holderView.findViewById(R.id.txt_mobilenumber);
                final EditText location = holderView.findViewById(R.id.location_txt);


                userName.setText(model.getUsername());
                emailId.setText(model.getEmailid());
                password.setText(model.getPassword());
                mobileNumber.setText(model.getMobilenumber());
                location.setText(model.getLocation());

                Button updateUser = holderView.findViewById(R.id.createUser);
                updateUser.setText("Update");

                dialog.show();

                updateUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("username", userName.getText().toString());
                        map.put("emailid", emailId.getText().toString());
                        map.put("password", password.getText().toString());
                        map.put("mobilenumber", mobileNumber.getText().toString());  //8886502502
                        map.put("location", location.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(getRef(position).getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(updateUser.getContext(), "Updated User", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(updateUser.getContext(), "Failed to Update User", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

                    }
                });

            }
        });

        /*holder.delteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.delteUser.getContext());
                builder.setTitle("Delete Panel");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    FirebaseDatabase.getInstance().getReference().child("Users")
                                            .child(getRef(position).getKey()).removeValue();

                                    Toast.makeText(holder.delteUser.getContext(), " User Deleted ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        });*/
    }

    @NonNull
    @Override
    public UsersAdapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlist_layout, parent, false);
        context = view.getContext();
        return new myviewholder(view);
    }

    public class myviewholder extends RecyclerView.ViewHolder {
        TextView username, emailid, password, mobilenumber, location;
        ImageView delteUser, updateUser;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.Addusername);
            emailid = itemView.findViewById(R.id.usermail_txt);
            password = itemView.findViewById(R.id.userpassword_txt);
            mobilenumber = itemView.findViewById(R.id.usermobile_txt);
            location = itemView.findViewById(R.id.userlocation_txt);

            delteUser = itemView.findViewById(R.id.deleteUser);
            updateUser = itemView.findViewById(R.id.editUser);

            delteUser.setVisibility(View.GONE);


        }
    }
}
