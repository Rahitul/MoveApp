package com.example.wub_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReceiptActivity extends AppCompatActivity {

    private TextView rCost,rCostWithoutDis,rNumber;
    private Button submit;
    DatabaseReference databaseReference;
    ReceiptData receiptData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        rNumber=findViewById(R.id.number);
        rCost=findViewById(R.id.cost);
        rCostWithoutDis=findViewById(R.id.costWithoutdis);
        submit=findViewById(R.id.submit);

        receiptData=new ReceiptData();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Payment");

        final String recNumber=getIntent().getStringExtra("keydrivernumber");
        final String recCost=getIntent().getStringExtra("keycustomercost");
        final String recCostWithoutDis=getIntent().getStringExtra("keycustomercostwithoutdis");

        rNumber.setText(recNumber);
        rCost.setText(recCost);
        rCostWithoutDis.setText(recCostWithoutDis);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiptData.setShowNumber(recNumber);
                receiptData.setShowCost(recCost);
                receiptData.setShowCostWithoutDis(recCostWithoutDis);

                databaseReference.push().setValue(receiptData);

                Intent intent=new Intent(ReceiptActivity.this,LastActivity.class);
                startActivity(intent);
            }
        });

    }
}
