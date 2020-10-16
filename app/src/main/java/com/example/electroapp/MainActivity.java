package com.example.electroapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ImageButton backButtonEp;
    private EditText nameEp, emailEp, phoneEp, addressEp;
    private Button updateButtonEp;


    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init ui views
        backButtonEp = findViewById(R.id.backButtonEp);
        nameEp = findViewById(R.id.nameEp);
        emailEp = findViewById(R.id.emailEp);
        phoneEp = findViewById(R.id.phoneEp);
        addressEp = findViewById(R.id.addressEp);
        updateButtonEp = findViewById(R.id.updateButtonEp);

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();



        backButtonEp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        updateButtonEp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();

            }
        });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
        else {

            loadMyInfo();
        }

    }

    private void loadMyInfo() {
        //load user info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("name").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            String email = ""+ds.child("email").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String address = ""+ds.child("address").getValue();


                            nameEp.setText(name);
                            emailEp.setText(email);
                            phoneEp.setText(phone);
                            addressEp.setText(address);
                        }
                    }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}

    private String name, email, phone, address;
    private void inputData() {

        name = nameEp.getText().toString().trim();
        email = emailEp.getText().toString().trim();
        phone = phoneEp.getText().toString().trim();
        address = addressEp.getText().toString().trim();

        updateProfile();

    }

    private void updateProfile() {
        progressDialog.setMessage("Updating Profile...");
        progressDialog.show();

        //setup data to save
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("name", ""+ firebaseAuth.getUid());
        hashMap.put("email", ""+ email);
        hashMap.put("phone", ""+ phone);
        hashMap.put("address", ""+ address);

        //update to database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Updated
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Update fail
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}