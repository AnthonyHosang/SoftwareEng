package com.example.anthony.softwareeng;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class ReservationInfoUserActivity extends AppCompatActivity {
    final Firebase ref = new Firebase("https://carparkapp.firebaseio.com");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_info_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




    }

    public void OnClickOkBtn(View view){
        Bundle b = getIntent().getExtras();
        int spot = b.getInt("spot");
        String lpn = ((EditText) findViewById(R.id.LicencePlateNumber_et)).getText().toString();
        if (lpn.compareTo("")==0){
            Toast.makeText(ReservationInfoUserActivity.this, "Licence plate number needed", Toast.LENGTH_SHORT).show();
        }
        else{
            lpn = lpn.replaceAll("\\s+", "");

            int hr = ((TimePicker) findViewById(R.id.timePicker)).getHour();
            int min = ((TimePicker) findViewById(R.id.timePicker)).getMinute();
            Date now = new Date();

            GregorianCalendar c = new GregorianCalendar(now.getYear() + 1900, now.getMonth(), now.getDate(), hr, min, now.getSeconds());
            if (c.getTime().getHours() < new Date().getHours() || (c.getTime().getHours() == new Date().getHours() && c.getTime().getMinutes() < new Date().getMinutes())) {
                c = new GregorianCalendar(now.getYear() + 1900, now.getMonth(), now.getDate() + 1, hr, min, now.getSeconds());
            }


            Map<String, Object> reserve = new HashMap<String, Object>();

            reserve.put("parkingSpaces/" + spot, c.getTimeInMillis());
            reserve.put("users/" + b.get("ID") + "/spaceNum", spot);
            reserve.put("users/" + b.get("ID") + "/licencePlateNum", lpn);
            ref.updateChildren(reserve);
            finish();
        }
    }

}
