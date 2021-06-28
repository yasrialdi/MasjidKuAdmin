package com.masjidtrpl.masjidku_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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

public class MainMasjidActivity extends AppCompatActivity implements AdapterKegiatan.dataListener{
    ImageButton tambah, review, izin, laporan;
    RecyclerView kegiatan;

    private DatabaseReference reference;
    private ArrayList<ModelsKegiatan> listKegiatan;
    private FirebaseAuth auth;

    int doubleTapParam = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_masjid);
        tambah = findViewById(R.id.mainmasjid_tambahkegiatan);
        review = findViewById(R.id.mainmasjid_review);
        izin = findViewById(R.id.mainmasjid_izinpenggunaan);
        laporan = findViewById(R.id.mainmasjid_laporankeuangan);
        kegiatan = findViewById(R.id.mainmasjid_fotokegiatan);

        reference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(MainMasjidActivity.this, LinearLayoutManager.HORIZONTAL, false);
        kegiatan.setLayoutManager(horizontalLayoutManager);
        kegiatan.setHasFixedSize(true);

        laporan.setEnabled(false);
        izin.setEnabled(false);
        review.setEnabled(false);
        getData();

        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMasjidActivity.this, TambahKegiatanActivity.class));
                finish();
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMasjidActivity.this, ReviewMasjidActivity.class));
                finish();
            }
        });
    }

    private void getData(){
        Toast.makeText(this, "Mohon tunggu sebentar...", Toast.LENGTH_SHORT).show();
        reference.child("Admin").child(auth.getUid()).child("Kegiatan")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listKegiatan = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            ModelsKegiatan modelsKegiatan = dataSnapshot.getValue(ModelsKegiatan.class);
                            assert modelsKegiatan != null;
                            modelsKegiatan.setKey(dataSnapshot.getKey());
                            listKegiatan.add(modelsKegiatan);
                        }
                        AdapterKegiatan adapterKegiatan = new AdapterKegiatan(MainMasjidActivity.this, listKegiatan);
                        kegiatan.setAdapter(adapterKegiatan);
                        Toast.makeText(MainMasjidActivity.this, "Data berhasil dimuat", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {
                        Toast.makeText(MainMasjidActivity.this, "Data gagal dimuat", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDeleteData(ModelsKegiatan modelsKegiatan, int position) {
        if (reference!=null){
            reference.child("Admin").child(auth.getUid()).child("Kegiatan").child(modelsKegiatan.getKey()).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainMasjidActivity.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleTapParam == 1) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(MainMasjidActivity.this, "Logout Berhasil!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        this.doubleTapParam = 1;
        Toast.makeText(this, "Tap sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleTapParam = 0, 2000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(MainMasjidActivity.this, SignInActivity.class));
                finish();
            }
        });
    }
}