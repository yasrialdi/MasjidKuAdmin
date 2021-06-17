package com.masjidtrpl.masjidku_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailKegiatanActivity extends AppCompatActivity {
    EditText judul, deskripsi;
    ImageView img1, img2, img3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kegiatan);
        judul = findViewById(R.id.detailkegiatan_txtjudul);
        deskripsi = findViewById(R.id.detailkegiatan_txtdeskripsi);
        img1 = findViewById(R.id.detailkegiatan_Image1);
        img2 = findViewById(R.id.detailkegiatan_Image2);
        img3 = findViewById(R.id.detailkegiatan_Image3);

        if (getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            judul.setText(bundle.getString("dataJudul"));
            deskripsi.setText(bundle.getString("dataDeskripsi"));
            Glide.with(DetailKegiatanActivity.this).load(bundle.getString("dataImg1")).into(img1);
            Glide.with(DetailKegiatanActivity.this).load(bundle.getString("dataImg2")).into(img2);
            Glide.with(DetailKegiatanActivity.this).load(bundle.getString("dataImg3")).into(img3);
        }else{
            Toast.makeText(this, "Gagal load kegiatan", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DetailKegiatanActivity.this, MainMasjidActivity.class));
        finish();
    }
}