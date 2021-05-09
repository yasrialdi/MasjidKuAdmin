package com.masjidtrpl.masjidku_admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

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
//                        Intent imageIntentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(imageIntentCamera, REQ_CODE_CAMERA);
                        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                        Intent imageIntentCamera = new Intent();
                                        imageIntentCamera.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                                        imageIntentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        intent.setAction(Intent.ACTION_GET_CONTENT);
                                        startActivityForResult(imageIntentCamera, REQ_CODE_GALLERY);
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                        permissionToken.continuePermissionRequest();
                                    }
                                }).check();
                        break;
                    case 1:
//                        Intent imageIntentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        startActivityForResult(imageIntentGallery, REQ_CODE_GALLERY);
                        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                        Intent imageIntentGallery = new Intent();
                                        imageIntentGallery.setType("image/*");
                                        imageIntentGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                                        imageIntentGallery.putExtra(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                        intent.setAction(Intent.ACTION_GET_CONTENT);
                                        startActivityForResult(Intent.createChooser(imageIntentGallery,"Please Select Multiple Files"), REQ_CODE_GALLERY);
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                        permissionToken.continuePermissionRequest();
                                    }
                                }).check();
                        break;
                }
            }
        });
        dialog.create();
        dialog.show();
    }
}