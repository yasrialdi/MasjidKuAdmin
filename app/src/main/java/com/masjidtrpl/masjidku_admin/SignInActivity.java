package com.masjidtrpl.masjidku_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private Button login, register;
    private EditText user, pass;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener;
    private String getEmail, getPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        login = findViewById(R.id.signin_signin);
        register = findViewById(R.id.signin_signup);
        user = findViewById(R.id.signin_username);
        pass = findViewById(R.id.signin_password);

        auth = FirebaseAuth.getInstance();

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                }
            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEmail = user.getText().toString();
                getPass = pass.getText().toString();

                if (isEmpty(getEmail) || isEmpty(getPass)){
                    Toast.makeText(SignInActivity.this, "Email atau sandi tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else{
                    loginUser();
                }
            }
        });
    }

    private void loginUser() {
        auth.signInWithEmailAndPassword(getEmail, getPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SignInActivity.this, "Login Succes", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(SignInActivity.this, "Tidak dapat login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null){
            auth.removeAuthStateListener(listener);
        }
    }

    private boolean isEmpty(String s){
        return TextUtils.isEmpty(s);
    }
}