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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
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
    Button login;
    ImageButton profile;
    ImageView galery, selectedImage;
    EditText name, address, contact, desc;
    CheckBox agree;
    List<Uri> files = new ArrayList<>();

    StorageReference reference;
    DatabaseReference databaseReference;

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
                                    imageIntentGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                                    imageIntentGallery.putExtra(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                        intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(imageIntentGallery,"Please Select Multiple Files"), REQ_CODE_GALLERY);
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
                        selectedImage.setImageBitmap(img);
                        Picasso.get().load(getImageUri(ProfilMasjidActivity.this,img)).into(selectedImage);

                        uploadImage(data);
                    }

                    break;
                case REQ_CODE_GALLERY:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri img = data.getData();
                        Picasso.get().load(img).into(selectedImage);

                        uploadImage(data);
                    }
                    break;
            }
        }
    }

    public void uploadImage(Intent data){
        if(data.getClipData() != null){
            int totalItemsSelected = data.getClipData().getItemCount();

            for(int i = 0; i < totalItemsSelected; i++){
                Uri fileUri = data.getClipData().getItemAt(i).getUri();
                String fileName = getFileName(fileUri);

                StorageReference fileToUpload = reference.child("Images").child(fileName);

                final int finalI = i;
                fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ProfilMasjidActivity.this, "Upload File "+finalI+" Berhasil", Toast.LENGTH_LONG).show();
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