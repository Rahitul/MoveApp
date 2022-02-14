package com.example.wub_project;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyHolder extends RecyclerView.ViewHolder {

    ImageView mImageView;
    TextView mTitle,mDes;

    public MyHolder(@NonNull View itemView) {
        super(itemView);

        this.mImageView=itemView.findViewById(R.id.imageIv);
        this.mTitle=itemView.findViewById(R.id.titleIv);
        this.mDes=itemView.findViewById(R.id.descriptionIv);
    }
}
