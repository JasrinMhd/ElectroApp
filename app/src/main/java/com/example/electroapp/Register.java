package com.example.electroapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    private EditText InputUsername, InputEmail, InputPhoneNumber, InputAddress, InputPassword, InputReConfirmPassword;
    private Button btnRegister;
    private TextView linkLogin;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InputUsername = (EditText) findViewById(R.id.reg_name);
        InputEmail = (EditText) findViewById(R.id.register_email);
        InputPhoneNumber = (EditText) findViewById(R.id.reg_phone);
        InputAddress = (EditText) findViewById(R.id.reg_address);
        InputPassword = (EditText) findViewById(R.id.reg_password);
        InputReConfirmPassword = (EditText) findViewById(R.id.con_password);
        btnRegister = (Button) findViewById(R.id.register);
        linkLogin = (TextView) findViewById(R.id.textView_login);
        loadingBar = new ProgressDialog(this);

        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,Login.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });

    }

    private void CreateAccount() {
        String username = InputUsername.getText().toString();
        String email = InputEmail.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String address = InputAddress.getText().toString();
        String password = InputPassword.getText().toString();
        String confirmpassword = InputReConfirmPassword.getText().toString();


        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Please Enter your Username", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(this, "Please Enter your Email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please Enter your Phone", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(address))
        {
            Toast.makeText(this, "Please Enter your Address", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmpassword))
        {
            Toast.makeText(this, "Please Confirm your Password", Toast.LENGTH_SHORT).show();

            if (!confirmpassword.equals(password))
            {
                Toast.makeText(Register.this, "Password do not match", Toast.LENGTH_SHORT).show();
            }

        }
        // else if(password != confirmpassword)
        //{
        //     Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
        // }
        else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are Validating your Credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUserName(username, email, phone, password, confirmpassword, address);
        }


    }

    private void ValidateUserName(final String name, final String email, final String phone, final String address, final String password, String confirmpassword)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(name).exists()))
                {
                    HashMap<String, Object> userdataMap =  new HashMap<>();
                    userdataMap.put("name", name);
                    userdataMap.put("email", email);
                    userdataMap.put("phone", phone);
                    userdataMap.put("address", address);
                    userdataMap.put("password", password);


                    RootRef.child("Users").child(name).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(Register.this, "Congratulations, your account has been created Successfully.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(Register.this,Login.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(Register.this, "Network Error, Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                else
                {
                    Toast.makeText(Register.this, "This "+ name + "already exists." , Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(Register.this, "Please try again using another Username.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}