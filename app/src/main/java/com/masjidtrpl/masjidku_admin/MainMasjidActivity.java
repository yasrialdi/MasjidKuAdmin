package com.masjidtrpl.masjidku_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainMasjidActivity extends AppCompatActivity {
    ImageButton tambah, review, izin, laporan;
    ImageView kegiatan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_masjid);
        tambah = findViewById(R.id.mainmasjid_tambahkegiatan);
        review = findViewById(R.id.mainmasjid_review);
        izin = findViewById(R.id.mainmasjid_izinpenggunaan);
        laporan = findViewById(R.id.mainmasjid_laporankeuangan);
        kegiatan = findViewById(R.id.mainmasjid_fotokegiatan);



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
}