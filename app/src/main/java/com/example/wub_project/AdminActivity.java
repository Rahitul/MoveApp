package com.example.wub_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminActivity extends AppCompatActivity {

    private Button customerlist,driverlist,reciptlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        driverlist=findViewById(R.id.driverlist);
        customerlist=findViewById(R.id.customerlist);
        reciptlist=findViewById(R.id.receiptlist);

        driverlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminActivity.this,AdminDriverActivity.class);
                startActivity(intent);
            }
        });

        customerlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminActivity.this,AdminCustomerActivity.class);
                startActivity(intent);
            }
        });

        reciptlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminActivity.this,AdminReciptActivity.class);
                startActivity(intent);
            }
        });
    }
}