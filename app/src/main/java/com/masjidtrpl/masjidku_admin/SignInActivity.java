package com.masjidtrpl.masjidku_admin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

public class SignInActivity extends AppCompatActivity {
    private Button login, register;
    private EditText user, pass;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener;
    private DatabaseReference reference;
    private String getEmail, getPass;

    private final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        login = findViewById(R.id.signin_signin);
        register = findViewById(R.id.signin_signup);

        user = findViewById(R.id.signin_username);
        pass = findViewById(R.id.signin_password);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        register.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish();
        });

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

    private void loginUser() {
        auth.signInWithEmailAndPassword(getEmail, getPass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(SignInActivity.this, "Login Succes", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignInActivity.this, MainMasjidActivity.class));
                        finish();
                    } else{
                        Toast.makeText(SignInActivity.this, "Tidak dapat login, data user tidak ada", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isEmpty(String s){
        return TextUtils.isEmpty(s);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}