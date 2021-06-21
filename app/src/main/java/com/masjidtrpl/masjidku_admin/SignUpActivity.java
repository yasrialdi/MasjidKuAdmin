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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    EditText user,email,pass,pass1;
    Button register;
    DatabaseReference reference;
    FirebaseAuth auth;
    String getEmail, getPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        user = findViewById(R.id.signup_username);
        email = findViewById(R.id.signup_email);
        pass = findViewById(R.id.signup_password);
        pass1 = findViewById(R.id.signup_ulangipassword);
        register = findViewById(R.id.signup_signup);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(email.getText().toString()) || isEmpty(pass.getText().toString()) || isEmpty(pass1.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "Data tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                } else {
                    if (pass.getText().toString().equals(pass1.getText().toString())){
                        cekDataUser();
                    } else{
                        Toast.makeText(SignUpActivity.this, "Password Tidak boleh berbeda", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void cekDataUser() {
        getEmail = email.getText().toString();
        getPass = pass.getText().toString();

        if (getPass.length() < 8){
            Toast.makeText(this, "Panjang password kurang dari 8 karakter", Toast.LENGTH_SHORT).show();
        } else{
            createUserAccount();
        }
    }

    private void createUserAccount() {
        auth.createUserWithEmailAndPassword(getEmail, getPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            reference.child("Admin").child(auth.getUid()).child("Nama").setValue(new ModelsName(user.getText().toString()));
//                            reference.child("Admin").child(auth.getCurrentUser().getUid()).child("Nama").setValue(auth.getCurrentUser().getDisplayName());
//                            saveUsername();
                            Toast.makeText(SignUpActivity.this, "Sign Up Berhasil", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, ProfilMasjidActivity.class));
                            finish();
                        } else{
                            Toast.makeText(SignUpActivity.this, "Terjadi kesalahan!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    private void saveUsername(){
//        reference.child("Admin").push().setValue(Objects.requireNonNull(auth.getCurrentUser()).getDisplayName())
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Toast.makeText(SignUpActivity.this, "Success", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private boolean isEmpty(String s){
        return TextUtils.isEmpty(s);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
    }
}