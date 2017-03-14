package com.pc.praful.student;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.util.List;

/**
 * Created by praful on 12/3/17.
 */
public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private final List<StudentPojo> studentPojoList;
    private final Context context;

    public StudentAdapter(List<StudentPojo> studentPojoList, Context context) {
        this.studentPojoList = studentPojoList;
        this.context = context;
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_student, null);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StudentViewHolder holder, int position) {
        StudentPojo studentPojo = studentPojoList.get(position);
        holder.name.setText(studentPojo.getName());
        holder.mobile.setText(studentPojo.getMobileNum());
        holder.email.setText(studentPojo.getEmail());
        holder.address.setText(studentPojo.getAddress());
        holder.profileImage.setImageBitmap(studentPojo.getImage());

        holder.mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Mobile Numeber", "Clicked");

                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + holder.mobile.getText().toString()));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    context.startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }

            }
        });

        holder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Email Id", "Clicked");
                String toEmail = holder.email.getText().toString();
                shareToGMail(new String[]{toEmail});

            }
        });

        holder.address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Address", "Clicked");
                String address = holder.address.getText().toString();
                showMap(address);
            }
        });
    }

    private void showMap(String geoLocation) {
        Uri gmmIntentUri = null;

        Geocoder coder = new Geocoder(context);
        List<Address> addresses;
        try {
            addresses = coder.getFromLocationName(geoLocation, 5);
            if (addresses == null) {
            }
            Address location = addresses.get(0);
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            String Location = "geo:"+lat+","+lng;
            gmmIntentUri = Uri.parse(Location);
            Log.i("Lat",""+lat);
            Log.i("Lng",""+lng);

        } catch (IOException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }


    @Override
    public int getItemCount() {
        return studentPojoList.size();
    }

    private void shareToGMail(String[] email) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("text/plain");
        final PackageManager pm = context.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        context.startActivity(emailIntent);
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {

        final CardView cv;
        final TextView name;
        final TextView mobile;
        final TextView email;
        final TextView address;
        final ImageView profileImage;

        public StudentViewHolder(View itemView) {
            super(itemView);

            cv = (CardView) itemView.findViewById(R.id.cv_single_student);
            name = (TextView) itemView.findViewById(R.id.tvName);
            mobile = (TextView) itemView.findViewById(R.id.tvMobileNum);
            address = (TextView) itemView.findViewById(R.id.tvAddress);
            email = (TextView) itemView.findViewById(R.id.tvEmailId);
            profileImage = (ImageView) itemView.findViewById(R.id.iv_Profile);
        }
    }
}
