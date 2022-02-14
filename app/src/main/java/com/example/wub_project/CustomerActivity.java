package com.example.wub_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerActivity extends AppCompatActivity {

    private EditText email_txt,password_txt;
    private Button login_btn,reg_btn;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //get the current user
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent=new Intent(CustomerActivity.this,CustomerHomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        email_txt=findViewById(R.id.email);
        password_txt=findViewById(R.id.password);
        login_btn=findViewById(R.id.login);
        reg_btn=findViewById(R.id.reg);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email=email_txt.getText().toString();
                final String password=password_txt.getText().toString();
                //create email and password function
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CustomerActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(CustomerActivity.this,"Sign Up Error",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String user_id=firebaseAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);
                            current_user_db.setValue(true);
                        }
                    }
                });
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email=email_txt.getText().toString();
                final String password=password_txt.getText().toString();
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(CustomerActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(CustomerActivity.this,"Sign Up Error",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent=new Intent(CustomerActivity.this,CustomerHomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
