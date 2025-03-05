package com.ryvk.drifthomeadmin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static final String TAG = "UserAdapter";
    private final List<UserCard> itemlist;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public UserAdapter(List<UserCard> itemlist, Context context) {
        this.itemlist = itemlist;
        this.context = context;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        UserCard item = itemlist.get(position);

        String name = item.getNameText();;
        if(name.length() > 18){
            name = name.substring(0,18)+"...";
        }
        holder.nameView.setText(name);

        String email = item.getEmailText();
        if(email.length() > 18){
            email = email.substring(0,18)+"...";
        }
        holder.emailView.setText(email);

        if(item.isUserList()){
            if(item.isBlocked()){
                int newColor = ContextCompat.getColor(context, R.color.d_green);
                ColorStateList newColorStateList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.d_green));

                holder.buttonBlock.setText(R.string.d_userCard_btn1_unblock);
                holder.buttonBlock.setStrokeColor(newColorStateList);
                holder.buttonBlock.setTextColor(newColor);
            }else{
                int newColor = ContextCompat.getColor(context, R.color.d_red1);
                ColorStateList newColorStateList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.d_red1));

                holder.buttonBlock.setText(R.string.d_userCard_btn1_block);
                holder.buttonBlock.setStrokeColor(newColorStateList);
                holder.buttonBlock.setTextColor(newColor);
            }
            holder.buttonBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(item.isBlocked()){
                        unBlockUser(item.isDrinker(), item.getEmailText(),itemlist.indexOf(item));
                    }else{
                        blockUser(item.isDrinker(), item.getEmailText(),itemlist.indexOf(item));
                    }
                }
            });
        }else{
            holder.buttonBlock.setVisibility(View.GONE);
        }

    }

    private void blockUser(boolean isDrinker, String email, int position) {
        if (isDrinker) {
            db.collection("drinker")
                    .document(email)
                    .update("blocked", true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.i(TAG, "drinker blocking: success");
                            Toast.makeText(context, "Drinker blocked!", Toast.LENGTH_SHORT).show();
                            itemlist.get(position).setBlocked(true);
                            notifyItemChanged(position);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "drinker blocking: failure");
                            AlertUtils.showAlert(context, "Blocking Failed!", "Error: " + e);
                        }
                    });
        } else {
            db.collection("saviour")
                    .document(email)
                    .update("blocked", true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.i(TAG, "saviour blocking: success");
                            Toast.makeText(context, "Saviour blocked!", Toast.LENGTH_SHORT).show();
                            itemlist.get(position).setBlocked(true);
                            notifyItemChanged(position);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "saviour blocking: failure");
                            AlertUtils.showAlert(context, "Blocking Failed!", "Error: " + e);
                        }
                    });
        }
    }

    private void unBlockUser(boolean isDrinker, String email, int position) {
        if (isDrinker) {
            db.collection("drinker")
                    .document(email)
                    .update("blocked", false)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.i(TAG, "drinker unblocking: success");
                            Toast.makeText(context, "Drinker unblocked!", Toast.LENGTH_SHORT).show();
                            itemlist.get(position).setBlocked(false);
                            notifyItemChanged(position);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "drinker unblocking: failure");
                            AlertUtils.showAlert(context, "Unblocking Failed!", "Error: " + e);
                        }
                    });
        } else {
            db.collection("saviour")
                    .document(email)
                    .update("blocked", false)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.i(TAG, "saviour unblocking: success");
                            Toast.makeText(context, "Saviour unblocked!", Toast.LENGTH_SHORT).show();
                            itemlist.get(position).setBlocked(false);
                            notifyItemChanged(position);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "saviour unblocking: failure");
                            AlertUtils.showAlert(context, "Unblocking Failed!", "Error: " + e);
                        }
                    });
        }
    }


    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView emailView;
        MaterialButton buttonBlock;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.textView17);
            emailView = itemView.findViewById(R.id.textView50);
            buttonBlock = itemView.findViewById(R.id.button3);
        }
    }
}
