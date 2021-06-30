package com.masjidtrpl.masjidku_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class IzinPenggunaanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izin_penggunaan);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(IzinPenggunaanActivity.this, MainMasjidActivity.class));
        finish();
    }
}