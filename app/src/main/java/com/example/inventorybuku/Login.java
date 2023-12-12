package com.example.inventorybuku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    EditText email, password;
    private Button btnregister, btnlogin;
    CheckBox remember;
    boolean valid = true;
    boolean passwodVisible;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        email= findViewById(R.id.email);
        password = findViewById(R.id.pw);
        btnlogin = findViewById(R.id.login);
        btnregister = findViewById(R.id.register);
        remember = findViewById(R.id.rememberMe);
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");

        if(checkbox.equals("true")){
            Intent intent = new Intent(Login.this, Admin.class);
            startActivity(intent);
        }else if(checkbox.equals("false")) {
            Toast.makeText(this, "Please Sign In", Toast.LENGTH_SHORT).show();
        }

////
       btnlogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               checkField(email);
               checkField(password);
               Log.d("TAG", "onClick: " + email.getText().toString());

               if(valid){
                   fAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                       @Override
                       public void onSuccess(AuthResult authResult) {
                           Toast.makeText(Login.this, "Login berhasil!!", Toast.LENGTH_SHORT).show();
                           checkUserAccessLevel(authResult.getUser().getUid());
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   });
               }
           }
       });
//
//
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regis = new Intent(Login.this, Register.class);
                startActivity(regis);


            }
        });

        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    Toast.makeText(Login.this, "Checked", Toast.LENGTH_SHORT).show();
                }else if(!compoundButton.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    Toast.makeText(Login.this, "Unchecked", Toast.LENGTH_SHORT).show();
                }
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
//
    private void checkUserAccessLevel(String uid) {
        DocumentReference df = fStore.collection("dblogin").document(uid);
        //extract data ke dokumen
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "onSuccess: " + documentSnapshot.getData());
                //identifikasi akses level
                if(documentSnapshot.getString("isAdmin") != null){
                    //user = admin
                    startActivity(new Intent(getApplicationContext(), Admin.class));
                    finish();
                }

                if(documentSnapshot.getString("isUser") != null){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        });
    }

    private boolean checkField(EditText textField) {
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
            DocumentReference df = FirebaseFirestore.getInstance().collection("dblogin").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getString("isAdmin") != null){
                        startActivity(new Intent(getApplicationContext(),Admin.class));
                        finish();
                    }

                    if(documentSnapshot.getString("isUser") != null){
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            });
        }
     }
}
