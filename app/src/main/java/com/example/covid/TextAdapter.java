package com.example.covid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.MyHolder> {

    Context context;
    ArrayList<Upload> arrayList;

    public TextAdapter(Context context, ArrayList<Upload> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.text_item, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        Upload upload = arrayList.get(position);
        holder.textView.setText("ФИО КОНТАКТА: " + upload.getText());
        holder.textView1.setText("НОМЕР ТЕЛЕФОНА: " + upload.getText1());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textView1;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.download_text_view);
            textView1 = itemView.findViewById(R.id.download_text_view1);
        }
    }
}
