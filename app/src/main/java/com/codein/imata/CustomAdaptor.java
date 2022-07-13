package com.codein.imata;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList id, date, customer, amount;

    CustomAdapter(Activity activity, Context context, ArrayList id, ArrayList date, ArrayList customer,
                  ArrayList amount){
        this.activity = activity;
        this.context = context;
        this.id = id;
        this.date = date;
        this.customer = customer;
        this.amount = amount;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.id_txt.setText(String.valueOf(id.get(position)));
        holder.customer_txt.setText(String.valueOf(customer.get(position)));
        holder.date_txt.setText(String.valueOf(date.get(position)));
        holder.amount_txt.setText(String.valueOf(amount.get(position)));
        //Recyclerview onClickListener
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(id.get(position)));
                intent.putExtra("date", String.valueOf(date.get(position)));
                intent.putExtra("customer", String.valueOf(customer.get(position)));
                intent.putExtra("amount", String.valueOf(amount.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });


    }

    @Override
    public int getItemCount() {
        return id.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView id_txt, customer_txt, date_txt, amount_txt;
        LinearLayout mainLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id_txt = itemView.findViewById(R.id.id_text);
            customer_txt = itemView.findViewById(R.id.customer_text);
            date_txt = itemView.findViewById(R.id.date_text);
            amount_txt = itemView.findViewById(R.id.amount_text);
            mainLayout = itemView.findViewById(R.id.mainLayout);
//            Animate Recyclerview
            Animation translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            mainLayout.setAnimation(translate_anim);
        }

    }

}
