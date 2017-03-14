package com.pc.praful.student;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ConstantConditions")
public class Student extends AppCompatActivity {

    private StudentAdapter studentAdapter;
    private final List<StudentPojo> studentListPojo = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    private Bitmap resizedBitmap;
   /* private Context context1;

    public Student(Context context1){
        this.context1 = context1;
    }*/

    public static final String sharedName = "nameKey";
    public static final String sharedMobileNum = "mobileKey";
    public static final String sharedAddress = "addressKey";
    public static final String sharedEmailId = "emailIdKey";
    public static final String sharedImagePath = "sharedImageKey";
    public static final String sharedKey = "sharedKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createCallPermissions();
            createEmailPermissions();
        }


        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvStudentList);
        studentAdapter = new StudentAdapter(studentListPojo, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(Student.this));
        recyclerView.setAdapter(studentAdapter);

        /*SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(sharedName,"Add Student");
        editor.commit();*/

//        create();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            public void onClick(View view, int position) {

                if (position == 0) {
                    Log.i("inside loop position", String.valueOf(position));
                    Intent intent = new Intent(Student.this, AddStudent.class);
                    startActivity(intent);
                }
            }

            public void onLongClick(View view, int position) {

            }
        }));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createCallPermissions(){
        String permission = Manifest.permission.CALL_PHONE;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                requestPermissions(new String[]{permission},1);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createEmailPermissions(){
        String permission = Manifest.permission.CALL_PHONE;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                requestPermissions(new String[]{permission},1);
            }
        }
    }

    public void onResume() {
        super.onResume();
        studentListPojo.clear();

        StudentPojo studentPojo = new StudentPojo();
        studentPojo.setName("Add Student");
        studentListPojo.add(studentPojo);

        String name, mobile, emailid, address;
        Uri selectedImage;

        int key = sharedPreferences.getInt(sharedKey, 0);
        for (int i = 1; i <= key; i++) {

            name = sharedPreferences.getString(sharedName + i, "");
            mobile = sharedPreferences.getString(sharedMobileNum + i, "");
            emailid = sharedPreferences.getString(sharedEmailId + i, "");
            address = sharedPreferences.getString(sharedAddress + i, "");
            selectedImage = Uri.parse(sharedPreferences.getString(sharedImagePath + i, ""));

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 120, true);

            } catch (IOException e) {
                e.printStackTrace();
            }
            StudentPojo studentPojo1 = new StudentPojo();
            studentPojo1.setName(name);
            studentPojo1.setMobileNum(mobile);
            studentPojo1.setEmail(emailid);
            studentPojo1.setAddress(address);
            studentPojo1.setImage(resizedBitmap);
            studentListPojo.add(studentPojo1);
        }


        studentAdapter.notifyDataSetChanged();


    }
}
