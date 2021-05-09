package com.masjidtrpl.masjidku_admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class ProfilMasjidActivity extends AppCompatActivity {
    Button login;
    ImageButton profile, galery;
    EditText name, address, contact, desc;
    CheckBox agree;

    private StorageReference reference;
    private DatabaseReference databaseReference;

    private static final int REQ_CODE_CAMERA = 1;
    private static final int REQ_CODE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_masjid);
        name = findViewById(R.id.profilmasjid_editnamamasjid);
        address = findViewById(R.id.profilmasjid_editalamatmasjid);
        contact = findViewById(R.id.profilmasjid_editnohp);
        desc = findViewById(R.id.profilmasjid_editdeskripsi);
        profile = findViewById(R.id.profilmasjid_btnimageprofil);
        galery = findViewById(R.id.profilmasjid_btnimagegaleri);
        login = findViewById(R.id.profilmasjid_btnsubmit);
        agree = findViewById(R.id.profilmasjid_chkbox);

        reference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();


    }

    private void getImage(){
        CharSequence[] menu = {"Kamera", "Galeri"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("Upload Image").setItems(menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent imageIntentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(imageIntentCamera, REQ_CODE_CAMERA);
                        break;
                    case 1:
                        Intent imageIntentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(imageIntentGallery, REQ_CODE_GALLERY);
                        break;
                }
            }
        });
        dialog.create();
        dialog.show();
    }
}