package com.masjidtrpl.masjidku_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ReviewMasjidActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_masjid);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ReviewMasjidActivity.this, MainMasjidActivity.class));
        finish();
    }
}