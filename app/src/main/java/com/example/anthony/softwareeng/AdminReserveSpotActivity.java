package com.example.anthony.softwareeng;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminReserveSpotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reserve_spot);
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

    public void onClickStart(View view){
        final Firebase ref = new Firebase("https://carparkapp.firebaseio.com/");
        String licencenum = ((EditText)findViewById(R.id.LicencePlateNumber_et)).getText().toString();
        licencenum = licencenum.replaceAll("\\s+", "");
        Bundle b = getIntent().getExtras();
        int i = b.getInt("spot");
        String id = b.getString("ID");
        Log.e("ID",id);
        long start = new Date().getTime();

        Map<String, Object> reservation = new HashMap<String, Object>();
        reservation.put("reservation/"+licencenum+"/time", start);
        reservation.put("reservation/"+licencenum+"/spot", i);
        ref.updateChildren(reservation);
        Map<String, Object> spot = new HashMap<String, Object>();
        spot.put("parkingSpaces/"+i,"in use");
        ref.updateChildren(spot);
        finish();
    }

}
