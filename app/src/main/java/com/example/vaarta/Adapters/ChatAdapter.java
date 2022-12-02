package com.example.vaarta.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vaarta.Models.MessageModel;
import com.example.vaarta.R;
import com.example.vaarta.cryptography.AESCryptoChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {
    AESCryptoChat aes = new AESCryptoChat("lv39eptlvuhaqqsr");
    ArrayList<MessageModel> messageModels;
    Context context;
    String recId;

    int SENDER_VIEW_TYPE=1;
    int RECEIVER_VIEW_TYPE=2;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String recId) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW_TYPE)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent,false);
            return new SenderViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position){
        if(messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid()))
        {
            return SENDER_VIEW_TYPE;
        }
        else{
            return RECEIVER_VIEW_TYPE;
        }

    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel messageModel = messageModels.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
               new AlertDialog.Builder(context)
                       .setTitle("Delete")
                       .setMessage("Are you sure you want to delete this message?")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int which) {

                               FirebaseDatabase database= FirebaseDatabase.getInstance();
                               String senderRoom = FirebaseAuth.getInstance().getUid() +recId;
                               database.getReference().child("chats").child(senderRoom)
                                       .child(messageModel.getMessageId())
                                       .setValue(null);
                           }
                       }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int which) {
                               dialogInterface.dismiss();
                           }
                       }).show();

                return false;
            }
        });

        if(holder.getClass() == SenderViewHolder.class){
            try {
                ((SenderViewHolder)holder).senderMsg.setText(aes.decrypt(messageModel.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                ((ReceiverViewHolder)holder).receiverMsg.setText(aes.decrypt(messageModel.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView receiverMsg, receiverTime;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

        receiverMsg = itemView.findViewById(R.id.receiverText);
        receiverTime = itemView.findViewById(R.id.receiverTime);
        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView senderMsg, senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
        }
    }

}
