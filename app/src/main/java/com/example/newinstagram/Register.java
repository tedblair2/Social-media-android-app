package com.example.newinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    private EditText fullname;
    private EditText username;
    private EditText email;
    private EditText password;
    private Button register;
    private TextView login;
    ProgressDialog prog;

    private DatabaseReference ref;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullname=findViewById(R.id.fullname);
        username=findViewById(R.id.username);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        register=findViewById(R.id.register_btn);
        login=findViewById(R.id.login_link);
        prog=new ProgressDialog(this);

        ref= FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this,Login.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_username=username.getText().toString();
                String txt_name=fullname.getText().toString();
                String txt_email=email.getText().toString();
                String txt_password=password.getText().toString();

                if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)
                || TextUtils.isEmpty(txt_name)){
                    Toast.makeText(Register.this, "Enter all credentials", Toast.LENGTH_SHORT).show();
                }else if(txt_password.length()<6){
                    Toast.makeText(Register.this, "Password is too short", Toast.LENGTH_SHORT).show();
                }else{
                    registeruser(txt_username,txt_name,txt_email,txt_password);
                }
            }
        });

    }

    private void registeruser(String txt_username, String txt_name, String txt_email, String txt_password) {
        prog.setMessage("Please wait...");
        prog.show();
        auth.createUserWithEmailAndPassword(txt_email,txt_password).addOnCompleteListener
                (new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        HashMap<String,Object> map=new HashMap<>();//using to add the below credentials to a realtime database
                        map.put("username",txt_username);
                        map.put("name",txt_name);
                        map.put("email",txt_email);
                        map.put("id",auth.getCurrentUser().getUid());
                        map.put("bio","");
                        map.put("imageurl","default");

                        ref.child("Users").child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener
                                (new OnCompleteListener<Void>() {//adding it to a real time database
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            prog.dismiss();
                                            Toast.makeText(Register.this, "Update your profile in settings",
                                                    Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(Register.this,MainActivity.class));
                                            finish();
                                        }
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                prog.dismiss();
                Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        }

    }