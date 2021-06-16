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
    private Button login, register, google;
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
        google = findViewById(R.id.signin_google);
        user = findViewById(R.id.signin_username);
        pass = findViewById(R.id.signin_password);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        register.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish();
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(false).build(),RC_SIGN_IN);
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN adalah kode permintaan yang Anda berikan ke startActivityForResult, saat memulai masuknya arus.
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                reference.child("Admin").child(auth.getCurrentUser().getUid()).child("Nama").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelsName name = snapshot.getValue(ModelsName.class);
                        if (name.getName().equals(auth.getCurrentUser().getDisplayName())) {
                            Toast.makeText(SignInActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignInActivity.this, MainMasjidActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SignInActivity.this, "?????", Toast.LENGTH_SHORT).show();
                    }
                });
//                Toast.makeText(SignInActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(SignInActivity.this, MainMasjidActivity.class));
//                finish();
            } else {
                Toast.makeText(SignInActivity.this, "Login Dibatalkan", Toast.LENGTH_SHORT).show();
            }
        }
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
}