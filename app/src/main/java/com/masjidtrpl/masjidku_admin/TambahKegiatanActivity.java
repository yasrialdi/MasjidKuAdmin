package com.masjidtrpl.masjidku_admin;

import androidx.annotation.NonNull;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
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
import java.util.UUID;

public class TambahKegiatanActivity extends AppCompatActivity {
    EditText judul, deskripsi;
    ImageView foto, selectedImage;
    Button submit;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    StorageReference reference;
    ProgressBar progressBar;

    String[] url = new String[3];
    int x=1;
    int y=0;

    private LinearLayout parentLinearLayout;
    private static final int REQ_CODE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kegiatan);
        judul = findViewById(R.id.tambahkegiatan_editnajudulkegiatan);
        deskripsi = findViewById(R.id.tambahkegiatan_editdeskripsi);
        foto = findViewById(R.id.tambahkegiatan_fotokegiatan);
        submit = findViewById(R.id.tambahkegiatan_btnsubmit);
        progressBar = findViewById(R.id.tambahkegiatan_progressBar);
        parentLinearLayout = findViewById(R.id.tambahkegiatan_parentLinearLayout);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        reference = FirebaseStorage.getInstance().getReference();

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail();
            }
        });
    }

    private void detail(){
        String title = judul.getText().toString();
        String desc = deskripsi.getText().toString();
        String url1 = url[0];
        String url2 = url[1];
        String url3 = url[2];

        databaseReference.child("Admin").child(auth.getUid()).child("Kegiatan").push()
                .setValue(new ModelsKegiatan(title, desc, url1, url2, url3))
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(TambahKegiatanActivity.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TambahKegiatanActivity.this, MainMasjidActivity.class));
                finish();
            }
        });
    }

    public void addImage() {
        if (x<=3){
            LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView=inflater.inflate(R.layout.image, null);
            // Add the new row before the add field button.
            parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
            parentLinearLayout.isFocusable();

            selectedImage = rowView.findViewById(R.id.number_edit_text);
            getImage(TambahKegiatanActivity.this);
            x++;
        } else{
            Toast.makeText(this, "Jumlah foto telah mencapai maks", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == REQ_CODE_GALLERY) {
                if (resultCode == RESULT_OK && data != null) {
                    Uri img = data.getData();
                    Picasso.get().load(img).into(selectedImage);
                    uploadImage(data);
                }
            }
        }
    }

    public void uploadImage(Intent data){
        if(data.getData() != null){
            Uri fileUri = data.getData();
            String fileName = getfilenamefromuri(fileUri);
            String pathFile = "Admin/"+auth.getUid()+"/Kegiatan/"+fileName;

            StorageReference fileToUpload = reference.child(pathFile);

            fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.child(pathFile).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String img = uri.toString();
                            url[y] = img;
                            y++;
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(TambahKegiatanActivity.this, "Upload File Berhasil", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "uploading Gagal! -> "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);
                }
            });
        } else{
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    public String getfilenamefromuri(Uri filepath) {
        String result = null;
        if (filepath.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(filepath, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = filepath.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void getImage(Context context){
        CharSequence[] menu = {"Galery", "Kembali"};
        AlertDialog.Builder dialogAlert = new AlertDialog.Builder(this).setTitle("Upload Image").
                setItems(menu, (dialog, which) -> {
            switch (which){
                case 0:
                    Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(pickPhoto, REQ_CODE_GALLERY);
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
                case 1:
                    dialog.dismiss();
                    break;
            }
        });
        dialogAlert.create();
        dialogAlert.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TambahKegiatanActivity.this, MainMasjidActivity.class));
        finish();
    }
}