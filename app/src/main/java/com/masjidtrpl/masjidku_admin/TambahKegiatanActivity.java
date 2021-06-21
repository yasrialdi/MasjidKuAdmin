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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    ImageView foto, selectedImage;
    Button submit;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    StorageReference reference;

    Intent dataImage;
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

        databaseReference.child("Admin").child(auth.getUid()).child("Kegiatan")
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
            switch (requestCode) {
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
            String getUserID = auth.getCurrentUser().getUid();
            if (totalItemsSelected < 4){
                for(int i = 0; i < totalItemsSelected; i++){
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    String fileName = getFileName(fileUri);
                    String pathFile = "Admin/"+getUserID+"/Kegiatan/Image/"+i+"_"+fileName;

                    StorageReference fileToUpload = reference.child(pathFile);
                    UploadTask uploadTask = fileToUpload.putFile(fileUri);

                    final int finalI = i;
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.child(pathFile).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    url[y] = uri.toString();
                                    y++;
//                                    databaseReference.child("Admin/"+getUserID+"/Kegiatan/ImageUrl").setValue(new ModelsImage(url));
                                    Toast.makeText(TambahKegiatanActivity.this, "Upload File "+finalI+" Berhasil", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            } else{
                Toast.makeText(this, "Image tidak boleh dari 3", Toast.LENGTH_SHORT).show();
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

    private void getImage(Context context){
        CharSequence[] menu = {"Oke", "Kembali"};
        AlertDialog.Builder dialogAlert = new AlertDialog.Builder(this).setTitle("Upload Image").
                setMessage("Pilihlah foto yang benar, karena foto akan langsung di upload ke databasae kami").
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