package com.masjidtrpl.masjidku_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class DetailProfilMasjidActivity extends AppCompatActivity {
    EditText judul, alamat, kontak, deskripsi;
    ImageView imgprofile, img1, img2, img3, img4, img5;
    TextView key, txtprofil, txtimg1, txtimg2, txtimg3, txtimg4, txtimg5;
    Button submit;

    DatabaseReference reference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_profil_masjid);
        judul = findViewById(R.id.detailprofilmasjid_txtjudul);
        alamat = findViewById(R.id.detailprofilmasjid_txtalamat);
        kontak = findViewById(R.id.detailprofilmasjid_notelpon);
        deskripsi = findViewById(R.id.detailprofilmasjid_txtdeskripsi);
        imgprofile = findViewById(R.id.detailprofilmasjid_ImageProfile);
        img1 = findViewById(R.id.detailprofilmasjid_Image1);
        img2 = findViewById(R.id.detailprofilmasjid_Image2);
        img3 = findViewById(R.id.detailprofilmasjid_Image3);
        img4 = findViewById(R.id.detailprofilmasjid_Image4);
        img5 = findViewById(R.id.detailprofilmasjid_Image5);

        key = findViewById(R.id.detailprofilmasjid_key);
        txtprofil = findViewById(R.id.detailprofilmasjid_txtprofil);
        txtimg1 = findViewById(R.id.detailprofilmasjid_txtimg1);
        txtimg2 = findViewById(R.id.detailprofilmasjid_txtimg2);
        txtimg3 = findViewById(R.id.detailprofilmasjid_txtimg3);
        txtimg4 = findViewById(R.id.detailprofilmasjid_txtimg4);
        txtimg5 = findViewById(R.id.detailprofilmasjid_txtimg5);

        submit = findViewById(R.id.detailprofilmasjid_btnsubmit);

        reference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        reference.child("Admin").child(auth.getUid()).child("ProfilMasjid")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelsProfile modelsProfile = snapshot.getValue(ModelsProfile.class);
                        assert modelsProfile != null;
                        modelsProfile.setKey(snapshot.getKey());
                        key.setText(modelsProfile.getKey());
                        judul.setText(modelsProfile.getName());
                        alamat.setText(modelsProfile.getAddress());
                        kontak.setText(modelsProfile.getContact());
                        deskripsi.setText(modelsProfile.getDesc());
                        if (modelsProfile.getImgProfil() != null){
                            imgprofile.setVisibility(View.VISIBLE);
                            txtprofil.setText(modelsProfile.getImgProfil());
                            Glide.with(DetailProfilMasjidActivity.this).load(modelsProfile.getImgProfil()).into(imgprofile);
                        }
                        if (modelsProfile.getImgUrl1() != null){
                            img1.setVisibility(View.VISIBLE);
                            txtimg1.setText(modelsProfile.getImgUrl1());
                            Glide.with(DetailProfilMasjidActivity.this).load(modelsProfile.getImgUrl1()).into(img1);
                        }
                        if (modelsProfile.getImgUrl2() != null){
                            img1.setVisibility(View.VISIBLE);
                            txtimg1.setText(modelsProfile.getImgUrl2());
                            Glide.with(DetailProfilMasjidActivity.this).load(modelsProfile.getImgUrl2()).into(img2);
                        }
                        if (modelsProfile.getImgUrl3() != null){
                            img1.setVisibility(View.VISIBLE);
                            txtimg1.setText(modelsProfile.getImgUrl3());
                            Glide.with(DetailProfilMasjidActivity.this).load(modelsProfile.getImgUrl3()).into(img3);
                        }
                        if (modelsProfile.getImgUrl4() != null){
                            img1.setVisibility(View.VISIBLE);
                            txtimg1.setText(modelsProfile.getImgUrl4());
                            Glide.with(DetailProfilMasjidActivity.this).load(modelsProfile.getImgUrl4()).into(img4);
                        }
                        if (modelsProfile.getImgUrl5() != null){
                            img1.setVisibility(View.VISIBLE);
                            txtimg1.setText(modelsProfile.getImgUrl5());
                            Glide.with(DetailProfilMasjidActivity.this).load(modelsProfile.getImgUrl5()).into(img5);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DetailProfilMasjidActivity.this, "Data gagal dimuat", Toast.LENGTH_SHORT).show();
                    }
                });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModelsProfile mdl = new ModelsProfile();
                mdl.setKey(key.getText().toString());
                mdl.setName(judul.getText().toString());
                mdl.setAddress(alamat.getText().toString());
                mdl.setDesc(deskripsi.getText().toString());
                mdl.setContact(kontak.getText().toString());
                mdl.setImgProfil(txtprofil.getText().toString());
                mdl.setImgUrl1(txtimg1.getText().toString());
                mdl.setImgUrl2(txtimg2.getText().toString());
                mdl.setImgUrl3(txtimg3.getText().toString());
                mdl.setImgUrl4(txtimg4.getText().toString());
                mdl.setImgUrl5(txtimg5.getText().toString());
                update(mdl);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DetailProfilMasjidActivity.this, MainMasjidActivity.class));
        finish();
    }

    private void update(ModelsProfile modelsProfile) {
        String UserID = auth.getUid();
        String getKey = modelsProfile.getKey();
        reference.child("Admin").child(Objects.requireNonNull(UserID)).child("ProfilMasjid").child(getKey).setValue(modelsProfile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Data berhasil diubah!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}