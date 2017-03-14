package com.pc.praful.student;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.pc.praful.student.Student.MyPREFERENCES;
import static com.pc.praful.student.Student.sharedAddress;
import static com.pc.praful.student.Student.sharedEmailId;
import static com.pc.praful.student.Student.sharedImagePath;
import static com.pc.praful.student.Student.sharedKey;
import static com.pc.praful.student.Student.sharedMobileNum;
import static com.pc.praful.student.Student.sharedName;

public class AddStudent extends AppCompatActivity {

    private EditText name;
    private EditText mobile;
    private EditText email;
    private EditText address;
    private TextView myImageViewText;
    private ImageView imageView;
    private Uri imageAbsolutePath;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        name = (EditText) findViewById(R.id.et_Name);
        mobile = (EditText) findViewById(R.id.et_Mobile_Number);
        email = (EditText) findViewById(R.id.et_EmailId);
        address = (EditText) findViewById(R.id.et_Address);
        imageView = (ImageView) findViewById(R.id.iv_circular_profile);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        myImageViewText = (TextView) findViewById(R.id.myImageViewText);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();

            }
        });

        final SharedPreferences.Editor editor = sharedPreferences.edit();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().length() == 0) {
                    name.setError("Enter Name");
                    name.requestFocus();
                } else if (mobile.getText().toString().length() == 0) {
                    mobile.setError("Enter Mobile Number");
                    mobile.requestFocus();
                } else if (address.getText().toString().length() == 0) {
                    address.setError("Enter Address");
                    address.requestFocus();
                } else if (email.getText().toString().length() == 0) {
                    email.setError("Enter Email Id");
                    email.requestFocus();
                } else {
                    int key = sharedPreferences.getInt(sharedKey, 0);
                    int keyvalue = key + 1;
                    editor.putInt((sharedKey), keyvalue);
                    editor.putString(sharedName + keyvalue, name.getText().toString());
                    editor.putString(sharedMobileNum + keyvalue, mobile.getText().toString());
                    editor.putString(sharedEmailId + keyvalue, email.getText().toString());
                    editor.putString(sharedAddress + keyvalue, address.getText().toString());
                    editor.putString(sharedImagePath + keyvalue, imageAbsolutePath.toString());
                    editor.apply();

                    View parentLayout = findViewById(R.id.root_view);
                    Snackbar snackbar;
                    snackbar = Snackbar.make(parentLayout, "Details Saved", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    snackbar.setAction("Action", null);
                    snackBarView.setBackgroundColor(Color.parseColor("#00C075"));
                    snackbar.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent i = new Intent(AddStudent.this, Student.class);
                            startActivity(i);
                        }
                    }, 1000);


                }


            }
        });

    }

    private void cameraIntent() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        File fileUri = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, 1);
    }


    private void loadImagefromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 5);
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddStudent.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(AddStudent.this);

                if (items[item].equals("Take Photo")) {
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    if (result)
                        loadImagefromGallery();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            myImageViewText.setText(" ");
            if (requestCode == 5 && resultCode == RESULT_OK
                    && null != data) {
                Uri selectedImage = data.getData();
                imageAbsolutePath = selectedImage;
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String img_Decodable_Str = cursor.getString(columnIndex);
                cursor.close();
                imageView.setImageBitmap(BitmapFactory
                        .decodeFile(img_Decodable_Str));

            } else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(photo, 100, 120, true);
                imageView.setImageBitmap(resizedBitmap);
            } else {
                Toast.makeText(this, "Hey pick your image first",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went embrassing", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
