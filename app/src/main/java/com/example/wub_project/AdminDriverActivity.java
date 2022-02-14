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

public class AdminDriverActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    MyAdapter myAdapter;
    DatabaseReference firebaseDatabase;
    FirebaseAuth mAuth;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_driver);

        mRecyclerView=findViewById(R.id.recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAdapter=new MyAdapter(this,getMyList());
        mRecyclerView.setAdapter(myAdapter);
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
        m.setTitle("df");
        m.setDescription("84");
        m.setImg(R.drawable.logo_move);
        models.add(m);

        Model n=new Model();
        n.setTitle("Rahi");
        n.setDescription("01521320568");
        n.setImg(R.drawable.logo_move);
        models.add(n);

        Model o=new Model();
        o.setTitle("rahi");
        o.setDescription("494");
        o.setImg(R.drawable.logo_move);
        models.add(o);

        Model p=new Model();
        p.setTitle("div@div.com");
        p.setDescription("01852");
        p.setImg(R.drawable.logo_move);
        models.add(p);

        Model q=new Model();
        q.setTitle("rahitul");
        q.setDescription("01521320568");
        q.setImg(R.drawable.logo_move);
        models.add(q);

        Model r=new Model();
        r.setTitle("Rahi");
        r.setDescription("643464");
        r.setImg(R.drawable.logo_move);
        models.add(r);


        return models;
    }
}