package com.masjidtrpl.masjidku_admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

public class TambahKegiatanActivity extends AppCompatActivity {
    EditText judul, deskripsi;
    ImageButton foto;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    StorageReference reference;
    private static final int REQ_CODE_CAMERA = 1;
    private static final int REQ_CODE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kegiatan);
        judul = findViewById(R.id.tambahkegiatan_editnajudulkegiatan);
        deskripsi = findViewById(R.id.tambahkegiatan_editdeskripsi);
        foto = findViewById(R.id.tambahkegiatan_fotokegiatan);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        reference = FirebaseStorage.getInstance().getReference();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case REQ_CODE_CAMERA:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap img = (Bitmap) data.getExtras().get("data");
                        foto.setImageBitmap(img);
                        Picasso.get().load(getImageUri(TambahKegiatanActivity.this,img)).into(foto);

                        uploadImage(data);
                    }

                    break;
                case REQ_CODE_GALLERY:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri img = data.getData();
                        Picasso.get().load(img).into(foto);

                        uploadImage(data);
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
                                Toast.makeText(TambahKegiatanActivity.this, "Upload File "+finalI+" Berhasil", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

            }
            //Toast.makeText(MainActivity.this, "Selected Multiple Files", Toast.LENGTH_SHORT).show();
        } else if (data.getData() != null){
            Toast.makeText(TambahKegiatanActivity.this, "Selected Single File", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(TambahKegiatanActivity.this, "Give app permission to camera", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(TambahKegiatanActivity.this, "Give app permission to gallery", Toast.LENGTH_SHORT).show();
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
}