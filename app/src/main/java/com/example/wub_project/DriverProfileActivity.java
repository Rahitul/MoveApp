package com.example.wub_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DriverProfileActivity extends AppCompatActivity {

    private EditText name_txt,phone_txt,type_txt;
    private Button confirm_btn,back_btn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;
    private String userId;
    private String mName;
    private String mPhone;
    private String mType;
    private ImageView profileimage;
    private Uri resultUri;
    private String profileImageUrl;
    private RadioGroup mRadioGroup;
    private String mServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        name_txt=findViewById(R.id.customerName);
        phone_txt=findViewById(R.id.customerPhone);
        type_txt=findViewById(R.id.typeofwork);

        confirm_btn=findViewById(R.id.confirm);
        back_btn=findViewById(R.id.back);

        profileimage=findViewById(R.id.profileimage);

        mRadioGroup=findViewById(R.id.radiogroup);

        //getting the correct database refference
        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        mDriverDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId);

        getUserInfo();

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createing gallery intent
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });

    }

    //get the user information
    private void getUserInfo(){
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map=(Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Name")!=null){
                        mName=map.get("Name").toString();
                        name_txt.setText(mName);
                    }

                    if(map.get("Phone")!=null){
                        mPhone=map.get("Phone").toString();
                        phone_txt.setText(mPhone);
                    }

                    if(map.get("TypeOfWork")!=null){
                        mType=map.get("TypeOfWork").toString();
                        type_txt.setText(mType);
                    }

                    if(map.get("Service")!=null){
                        mServices=map.get("Service").toString();
                        switch (mServices){
                            case "Home Shift":
                                mRadioGroup.check(R.id.homeshift);
                                break;

                                case "Truck":
                                mRadioGroup.check(R.id.truck);
                                break;

                                case "Labourer":
                                mRadioGroup.check(R.id.labour);
                                break;
                        }
                    }

                    if(map.get("profileImageUrl")!=null){
                        profileImageUrl=map.get("profileImageUrl").toString();
                        Picasso.get().load(profileImageUrl).into(profileimage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation() {
        mName=name_txt.getText().toString();
        mPhone=phone_txt.getText().toString();
        mType=type_txt.getText().toString();

        int selectId=mRadioGroup.getCheckedRadioButtonId();

        final RadioButton radioButton=findViewById(selectId);

        if(radioButton.getText()==null){
            return;
        }

        mServices=radioButton.getText().toString();

        //hasmap is use for enter many data
        Map userInfo=new HashMap();
        userInfo.put("Name",mName);
        userInfo.put("Phone",mPhone);
        userInfo.put("TypeOfWork",mType);
        userInfo.put("Service",mServices);

        //update the data
        mDriverDatabase.updateChildren(userInfo);

        //save image in database
        if(resultUri!=null){
            final StorageReference filePath= FirebaseStorage.getInstance().getReference().child("profile_images").child(userId);
            Bitmap bitmap=null;

            //get the image from image view
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
            } catch (IOException e){
                e.printStackTrace();
            }

            ByteArrayOutputStream boas=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,boas);

            byte[] data=boas.toByteArray();
            UploadTask uploadTask=filePath.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mDriverDatabase.updateChildren(newImage);

                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });

        }
        else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode== Activity.RESULT_OK){
            final Uri imageUri=data.getData();
            resultUri=imageUri;
            //changing the imageview
            //not save the image
            profileimage.setImageURI(resultUri);
        }
    }
}
