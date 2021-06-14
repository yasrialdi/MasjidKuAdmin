package com.masjidtrpl.masjidku_admin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfilMasjidActivity extends AppCompatActivity {
    Button submit;
    ImageView galery, selectedImage, profile;
    EditText name, address, contact, desc;
    CheckBox agree;
    Intent dataImageGalery, dataImageProfile;
    int cekImage;

    StorageReference reference;
    DatabaseReference databaseReference;
    FirebaseAuth auth;

    private LinearLayout parentLinearLayout;
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
        submit = findViewById(R.id.profilmasjid_btnsubmit);
        agree = findViewById(R.id.profilmasjid_chkbox);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        galery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekImage = 1;
                addImage();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekImage = 0;
                getImage(ProfilMasjidActivity.this);
            }
        });

        if (agree.isChecked()){
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadImage(dataImageProfile);
                    uploadImage(dataImageGalery);
                    detail();
                }
            });
        } else{
            Toast.makeText(ProfilMasjidActivity.this, "Data sudah benar?", Toast.LENGTH_SHORT).show();
        }

    }

    private void detail(){
        String nama = name.getText().toString();
        String alamat = address.getText().toString();
        String kontak = contact.getText().toString();
        String deskripsi = desc.getText().toString();

        databaseReference.child("Admin").child(auth.getCurrentUser().getUid()).child("Profil Masjid")
                .setValue(new ModelsProfile(nama, alamat, kontak, deskripsi))
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ProfilMasjidActivity.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addImage() {
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.image, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
        parentLinearLayout.isFocusable();

        selectedImage = rowView.findViewById(R.id.number_edit_text);
        getImage(ProfilMasjidActivity.this);
    }

    private void getImage(Context context){
        CharSequence[] menu = {"Kamera", "Galeri", "Kembali"};
        AlertDialog.Builder dialogAlert = new AlertDialog.Builder(this).setTitle("Upload Image").setItems(menu, (dialog, which) -> {
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
                                    startActivityForResult(imageIntentCamera, REQ_CODE_CAMERA);
                                }
                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                    Toast.makeText(ProfilMasjidActivity.this, "Give app permission to camera", Toast.LENGTH_SHORT).show();
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
                                    if (cekImage==1){
                                        imageIntentGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                                    }
                                    imageIntentGallery.putExtra(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                        intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(imageIntentGallery,"Please Select Image"), REQ_CODE_GALLERY);
                                }
                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                    Toast.makeText(ProfilMasjidActivity.this, "Give app permission to gallery", Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            }).check();
                    break;
                case 2:
                    dialog.dismiss();
                    break;
            }
        });
        dialogAlert.create();
        dialogAlert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case REQ_CODE_CAMERA:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap img = (Bitmap) data.getExtras().get("data");
                        if (cekImage==1){
                            selectedImage.setImageBitmap(img);
                            Picasso.get().load(getImageUri(ProfilMasjidActivity.this,img)).into(selectedImage);
                            dataImageGalery = data;
                        } else if (cekImage==0){
                            profile.setImageBitmap(img);
                            Picasso.get().load(getImageUri(ProfilMasjidActivity.this,img)).into(profile);
                            dataImageProfile = data;
                        }
//                        uploadImage(data);
                    }

                    break;
                case REQ_CODE_GALLERY:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri img = data.getData();
                        if (cekImage==1){
                            Picasso.get().load(img).into(selectedImage);
                            dataImageGalery = data;
                        } else if (cekImage==0){
                            Picasso.get().load(img).into(profile);
                            dataImageProfile = data;
                        }
//                        uploadImage(data);
                    }
                    break;
            }
        }
    }

    public void uploadImage(Intent data){
        if(data.getClipData() != null){
            int totalItemsSelected = data.getClipData().getItemCount();
            String getUserID = auth.getCurrentUser().getUid();

            for(int i = 0; i < totalItemsSelected; i++){
                Uri fileUri = data.getClipData().getItemAt(i).getUri();
                String fileName = getFileName(fileUri);
                String pathFile = "Admin/"+getUserID+"/Image/"+fileName;

                StorageReference fileToUpload = reference.child(pathFile);

                final int finalI = i;
                fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.child(pathFile).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                databaseReference.child("Admin/"+getUserID+"/ImageUrl").push().setValue(new ModelsImage(url));
                                Toast.makeText(ProfilMasjidActivity.this, "Upload File "+finalI+" Berhasil", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

            }
            //Toast.makeText(MainActivity.this, "Selected Multiple Files", Toast.LENGTH_SHORT).show();
        } else if (data.getData() != null){
            Toast.makeText(ProfilMasjidActivity.this, "Selected Single File", Toast.LENGTH_SHORT).show();
        }
    }

    //===== bitmap to Uri
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "intuenty", null);
        Log.d("image uri",path);
        return Uri.parse(path);
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}