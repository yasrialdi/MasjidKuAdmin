package com.masjidtrpl.masjidku_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class DetailKegiatanActivity extends AppCompatActivity {
    EditText judul, deskripsi;
    ImageView img1, img2, img3;
    Button submit;

    FirebaseAuth auth;
    DatabaseReference database;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kegiatan);
        judul = findViewById(R.id.detailkegiatan_txtjudul);
        deskripsi = findViewById(R.id.detailkegiatan_txtdeskripsi);
        img1 = findViewById(R.id.detailkegiatan_Image1);
        img2 = findViewById(R.id.detailkegiatan_Image2);
        img3 = findViewById(R.id.detailkegiatan_Image3);
        submit = findViewById(R.id.detailkegiatan_btnsubmit);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        if (getIntent().getExtras() != null){
            bundle = getIntent().getExtras();
            judul.setText(bundle.getString("dataJudul"));
            deskripsi.setText(bundle.getString("dataDeskripsi"));
            Glide.with(DetailKegiatanActivity.this).load(bundle.getString("dataImg1")).into(img1);
            Glide.with(DetailKegiatanActivity.this).load(bundle.getString("dataImg2")).into(img2);
            Glide.with(DetailKegiatanActivity.this).load(bundle.getString("dataImg3")).into(img3);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ModelsKegiatan mdl = new ModelsKegiatan();
                    mdl.setTitle(judul.getText().toString());
                    mdl.setDesc(deskripsi.getText().toString());
                    mdl.setImgUrl1(bundle.getString("dataImg1"));
                    mdl.setImgUrl2(bundle.getString("dataImg2"));
                    mdl.setImgUrl3(bundle.getString("dataImg3"));
                    update(mdl);
                }
            });
        }else{
            Toast.makeText(this, "Gagal load kegiatan", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DetailKegiatanActivity.this, MainMasjidActivity.class));
        finish();
    }

    private void update(ModelsKegiatan setKegiatan) {
        String UserID = auth.getUid();
        String getKey = bundle.getString("dataKey");
        database.child("Admin").child(Objects.requireNonNull(UserID)).child("Kegiatan").child(getKey).setValue(setKegiatan)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Data berhasil diubah!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(DetailKegiatanActivity.this, SignInActivity.class));
                finish();
            }
        });
    }
}