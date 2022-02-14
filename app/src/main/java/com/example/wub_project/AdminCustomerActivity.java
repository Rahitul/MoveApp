package com.example.wub_project;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminCustomerActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    NyAdapter nyAdapter;
    DatabaseReference firebaseDatabase;
    FirebaseAuth mAuth;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_customer);

        mRecyclerView=findViewById(R.id.recycleViewc);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        nyAdapter= new NyAdapter(this,getMyList());
        mRecyclerView.setAdapter(nyAdapter);
    }

    private ArrayList<Model> getMyList() {
        final String driverId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name=dataSnapshot.child("Name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ArrayList<Model> models=new ArrayList<>();

        Model m=new Model();
        m.setTitle("cus");
        m.setDescription("9865");
        m.setImg(R.drawable.logo_move);
        models.add(m);

        Model n=new Model();
        n.setTitle("Rahitul");
        n.setDescription("01305578370");
        n.setImg(R.drawable.logo_move);
        models.add(n);

        Model o=new Model();
        o.setTitle("");
        o.setDescription("");
        o.setImg(R.drawable.logo_move);
        models.add(o);

        Model p=new Model();
        p.setTitle("name");
        p.setDescription("0815");
        p.setImg(R.drawable.logo_move);
        models.add(p);

        return models;
    }
}