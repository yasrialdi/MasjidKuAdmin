package com.masjidtrpl.masjidku_admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;

public class SignInActivity extends AppCompatActivity {
    private Button login, register, google;
    private EditText user, pass;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener;
    private String getEmail, getPass;

    private final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        login = findViewById(R.id.signin_signin);
        register = findViewById(R.id.signin_signup);
        google = findViewById(R.id.signin_google);
        user = findViewById(R.id.signin_username);
        pass = findViewById(R.id.signin_password);

        auth = FirebaseAuth.getInstance();

        listener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null){
                startActivity(new Intent(SignInActivity.this, MainMasjidActivity.class));
                finish();
            }
        };

        register.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish();
        });

        google.setOnClickListener(v -> startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                .setIsSmartLockEnabled(false).build(),RC_SIGN_IN));

        login.setOnClickListener(v -> {
            getEmail = user.getText().toString();
            getPass = pass.getText().toString();

            if (isEmpty(getEmail) || isEmpty(getPass)){
                Toast.makeText(SignInActivity.this, "Email atau sandi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else{
                loginUser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN adalah kode permintaan yang Anda berikan ke startActivityForResult, saat memulai masuknya arus.
        if (requestCode == RC_SIGN_IN){
            if (resultCode == RESULT_OK){

                Toast.makeText(SignInActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent());
            } else{
                Toast.makeText(SignInActivity.this, "Login Dibatalkan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginUser() {
        auth.signInWithEmailAndPassword(getEmail, getPass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(SignInActivity.this, "Login Succes", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(SignInActivity.this, "Tidak dapat login", Toast.LENGTH_SHORT).show();
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