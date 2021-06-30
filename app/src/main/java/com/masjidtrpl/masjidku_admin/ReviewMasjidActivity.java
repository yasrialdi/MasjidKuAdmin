package com.masjidtrpl.masjidku_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReviewMasjidActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<ModelsReview> listReview;

    DatabaseReference reference;
    FirebaseUser auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_masjid);
        recyclerView = findViewById(R.id.reviewMasjid_recycler);

        reference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance().getCurrentUser();

        getData();
    }

    private void getData(){
        reference.child("User").child("Review").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listReview = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            ModelsReview modelsReview = dataSnapshot.getValue(ModelsReview.class);
                            assert modelsReview != null;
                            modelsReview.setKey(dataSnapshot.getKey());
                            listReview.add(modelsReview);
                        }
                        AdapterReview adapterReview = new AdapterReview(ReviewMasjidActivity.this, listReview);
                        recyclerView.setAdapter(adapterReview);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ReviewMasjidActivity.this, "Data gagal dimuat", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ReviewMasjidActivity.this, MainMasjidActivity.class));
        finish();
    }
}