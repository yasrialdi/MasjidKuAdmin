package com.masjidtrpl.masjidku_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public class MainMasjidActivity extends AppCompatActivity implements AdapterKegiatan.dataListener{
    ImageButton profil, tambah, review, izin, laporan;
    RecyclerView kegiatan;

    private DatabaseReference reference;
    private ArrayList<ModelsKegiatan> listKegiatan;
    private FirebaseUser auth;

    int doubleTapParam = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_masjid);
        profil = findViewById(R.id.mainmasjid_profilmasjid);
        tambah = findViewById(R.id.mainmasjid_tambahkegiatan);
        review = findViewById(R.id.mainmasjid_review);
        izin = findViewById(R.id.mainmasjid_izinpenggunaan);
        kegiatan = findViewById(R.id.mainmasjid_fotokegiatan);


        reference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance().getCurrentUser();

        assert auth != null;
        if (auth.isEmailVerified()){
            LinearLayoutManager horizontalLayoutManager
                    = new LinearLayoutManager(MainMasjidActivity.this, LinearLayoutManager.HORIZONTAL, false);
            kegiatan.setLayoutManager(horizontalLayoutManager);
            kegiatan.setHasFixedSize(true);

            laporan.setEnabled(false);
            izin.setEnabled(false);
            getData();

            profil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainMasjidActivity.this, DetailProfilMasjidActivity.class));
                    finish();
                }
            });

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
        } else{
            CharSequence[] menu = {"Verifikasi", "Kembali"};
            AlertDialog.Builder dialogAlert = new AlertDialog.Builder(this).setTitle("Verifikasi Email Anda").
                    setItems(menu, (dialog, which) -> {
                        switch (which){
                            case 0:
                                auth.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(MainMasjidActivity.this, "Email telah dikirim", Toast.LENGTH_SHORT).show();
                                            AuthUI.getInstance().signOut(MainMasjidActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        startActivity(new Intent(MainMasjidActivity.this, SignInActivity.class));
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                                break;
                            case 1:
                                dialog.dismiss();
                                AuthUI.getInstance().signOut(MainMasjidActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(MainMasjidActivity.this, SignInActivity.class));
                                            finish();
                                        }
                                    }
                                });
                                break;
                        }
                    });
            dialogAlert.create();
            dialogAlert.show();
        }
    }

    private void getData(){
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
                    startActivity(new Intent(MainMasjidActivity.this, SignInActivity.class));
                    finish();
                }
            });
        }

        this.doubleTapParam = 1;
        Toast.makeText(this, "Tap sekali lagi untuk logout", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleTapParam = 0, 2000);
    }
}