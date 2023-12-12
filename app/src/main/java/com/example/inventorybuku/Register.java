package com.example.inventorybuku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText username, password, email;
    Button btnregister, btnkelogin;
    boolean valid = true;
    boolean passwodVisible;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    CheckBox isAdminBox, isUserBox;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        username = findViewById(R.id.regis_usrnm);
        email = findViewById(R.id.regis_email);
        password = findViewById(R.id.regis_pw);
        btnregister = findViewById(R.id.regis_rg);
        btnkelogin = findViewById(R.id.ke_login);

        isAdminBox = findViewById(R.id.isAdmin);
        isUserBox = findViewById(R.id.isUser);

        // logika checkbox
        isUserBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isAdminBox.setChecked(false);
                }
            }
        });

        isAdminBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isUserBox.setChecked(false);
                }
            }
        });



        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkField(username);
                checkField(email);
                checkField(password);

                //checkbox validasi
                if(!(isAdminBox.isChecked() || isUserBox.isChecked())){
                    Toast.makeText(Register.this, "Pilih masuk akun sebagai apa??", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(valid){
                    //start the user regis proses
                    fAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Akun berhasil dibuat!!", Toast.LENGTH_SHORT).show();
                            DocumentReference df = fStore.collection("dblogin").document(user.getUid());
                            Map<String,Object> userInfo = new HashMap<>();
                            userInfo.put("username", username.getText().toString());
                            userInfo.put("UserEmail", email.getText().toString());

                            //spesifikm jika user = admin
                            if(isAdminBox.isChecked()){
                                userInfo.put("isAdmin", "1");
                            }
                            if(isUserBox.isChecked()){
                                userInfo.put("isUser", "1");
                            }

                            df.set(userInfo);

                            if(isAdminBox.isChecked()){
                                startActivity(new Intent(getApplicationContext(), Admin.class));
                                finish();
                            }
                            if(isUserBox.isChecked()){
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Akun gagal dibuat!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        btnkelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right=2;
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX()>=password.getRight()-password.getCompoundDrawables()[Right].getBounds().width()){
                        int selection=password.getSelectionEnd();
                        if(passwodVisible){
                            //set drawable image here
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                            //for hide pw
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwodVisible=false;
                        }else {
                            //set drawable image here
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                            //for show pw
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwodVisible=true;
                        }
                        password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private boolean checkField(EditText textField) {
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error!!");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }

}