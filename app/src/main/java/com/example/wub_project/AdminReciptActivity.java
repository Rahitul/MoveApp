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

public class AdminReciptActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RyAdapter myAdapter;
    DatabaseReference firebaseDatabase;
    FirebaseAuth mAuth;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_recipt);

        mRecyclerView=findViewById(R.id.recycleViewr);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAdapter=new RyAdapter(this,getMyList());
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
        m.setTitle("Phone: 01305578370");
        m.setDescription("Total: 488390");
        m.setImg(R.drawable.logo_move);
        models.add(m);

        Model h=new Model();
        h.setTitle("Phone: 01664233433");
        h.setDescription("Total: 48330");
        h.setImg(R.drawable.logo_move);
        models.add(h);

        Model n=new Model();
        n.setTitle("Phone: 016734297374");
        n.setDescription("Total: 27634");
        n.setImg(R.drawable.logo_move);
        models.add(n);

        Model o=new Model();
        o.setTitle("Phone: 01521320568");
        o.setDescription("Total: 44590");
        o.setImg(R.drawable.logo_move);
        models.add(o);

        Model p=new Model();
        p.setTitle("Phone: 01305578370");
        p.setDescription("Total: 48490");
        p.setImg(R.drawable.logo_move);
        models.add(p);

        Model q=new Model();
        q.setTitle("Phone: 0182114004");
        q.setDescription("Total: 4880");
        q.setImg(R.drawable.logo_move);
        models.add(q);

        Model r=new Model();
        r.setTitle("Phone: 01734532478");
        r.setDescription("Total: 4390");
        r.setImg(R.drawable.logo_move);
        models.add(r);


        return models;
    }
}