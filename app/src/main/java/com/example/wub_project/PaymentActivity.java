package com.example.wub_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private AutoCompleteTextView firstDestination,secondDestination;
    private TextView cost,discount,total;
    private Button confirm,cancel,submit;
    private LinearLayout cost_show,button_show;
    private ImageView firstDes,secondDes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        cost=findViewById(R.id.cost_result);
        discount=findViewById(R.id.discount_result);
        total=findViewById(R.id.total_result);

        confirm=findViewById(R.id.confirm);
        submit=findViewById(R.id.submit);
        cancel=findViewById(R.id.cancel);

        cost_show=findViewById(R.id.cost_show);
        button_show=findViewById(R.id.button_show);

        firstDes=findViewById(R.id.firstDestinationDropDown);
        secondDes=findViewById(R.id.secondDestinationDropDown);

        firstDestination=findViewById(R.id.firstDestination);
        secondDestination=findViewById(R.id.secondDestination);

        firstDestination.setThreshold(2);
        secondDestination.setThreshold(1);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,starting);
        firstDestination.setAdapter(adapter);

        ArrayAdapter<String> adapter1=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,destination);
        secondDestination.setAdapter(adapter1);

        firstDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstDestination.showDropDown();
            }
        });

        secondDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondDestination.showDropDown();
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                button_show.setVisibility(View.VISIBLE);
                cost_show.setVisibility(View.VISIBLE);
                submit.setVisibility(View.GONE);

                String s=firstDestination.getText().toString();
                String s1=secondDestination.getText().toString();

                cost.setText(s+" to "+s1);

                if(firstDestination.getText().toString()=="Mirpur" && secondDestination.getText().toString()=="Dhanmondi"){
                    total.setText("hello");
                }

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String coSt=cost.getText().toString();
                String totalWithDiscount=total.getText().toString();

                Intent intent=new Intent(PaymentActivity.this,CustomerMapActivity.class);
                intent.putExtra("keycost",coSt);
                intent.putExtra("keytotal",totalWithDiscount);
                startActivity(intent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });
    }

    private static final String[] starting= new String[]{"Mirpur","Dhanmondi"};
    private static final String[] destination= new String[]{"Dhanmondi","Mirpur"};
}
