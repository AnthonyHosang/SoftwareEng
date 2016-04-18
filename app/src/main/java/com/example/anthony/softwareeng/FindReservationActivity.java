package com.example.anthony.softwareeng;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FindReservationActivity extends AppCompatActivity {

    final Firebase ref = new Firebase("https://carparkapp.firebaseio.com");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_reservation);
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

        Bundle b = getIntent().getExtras();
        final String i = b.getString("lpn");


        ref.child("reservation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(i))   {

                    ((Button) findViewById(R.id.start_btn)).setEnabled(false);

                    ((Button) findViewById(R.id.billRequest_btn)).setEnabled(true);
                }
                    else{
                    ((Button) findViewById(R.id.start_btn)).setEnabled(true);
                    ((Button)findViewById(R.id.billRequest_btn)).setEnabled(false);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        TextView a = (TextView) findViewById(R.id.LicencePlate_txt);
        a.setText(i);

    }

    public void startTimer(View view){/////////////to change/////////////////////////////////////////////

        ref.child("").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Bundle b = getIntent().getExtras();
                String licencenum = b.getString("lpn");
                String id  = b.getString("ID");
                String i  = (snapshot.child("users/"+id+"/spaceNum").getValue().toString());


                long start = new Date().getTime();
                Toast.makeText(FindReservationActivity.this, "date started at "+start, Toast.LENGTH_SHORT).show();
                ((Button)findViewById(R.id.billRequest_btn)).setEnabled(true);
                ((Button)findViewById(R.id.start_btn)).setEnabled(false);
                final Firebase ref = new Firebase("https://carparkapp.firebaseio.com/");



                Map<String, Object> reservation = new HashMap<String, Object>();
                reservation.put("users/"+id+"/licencePlateNum", "null");
                reservation.put("users/"+id+"/spaceNum", "null");
                reservation.put("reservation/"+licencenum+"/time", start);
                reservation.put("reservation/"+licencenum+"/spot", i);
                ref.updateChildren(reservation);
                Map<String, Object> spot = new HashMap<String, Object>();
                spot.put("parkingSpaces/"+i,"in use");
                ref.updateChildren(spot);
                finish();


            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
    public void getTotal(View view){
        Bundle b = getIntent().getExtras();
        final String licenceplatenumber = b.getString("lpn");


        final Firebase ref = new Firebase("https://carparkapp.firebaseio.com");
        ref.child("").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                String s = snapshot.child("reservation/"+licenceplatenumber+"/time").getValue().toString();
                String i = snapshot.child("reservation/"+licenceplatenumber+"/spot").getValue().toString();
                Long start = Long.parseLong(s);
                long end = new Date().getTime();
                Log.e("now",end+"");
                long time = end - start;
                double hrs = (double)time/3600000;

                Toast.makeText(FindReservationActivity.this, ""+hrs, Toast.LENGTH_SHORT).show();

                Map<String, Object> spot = new HashMap<String, Object>();
                spot.put("parkingSpaces/"+i,"null");
                ref.child("reservation/"+licenceplatenumber).removeValue();
                ref.updateChildren(spot);
                ((Button)findViewById(R.id.billRequest_btn)).setEnabled(false);
                int timeINT  = (int) hrs+1;

                float price = timeINT*10;
                String strDouble = String.format("%.2f",price);

                AlertDialog.Builder builder2 = new AlertDialog.Builder(FindReservationActivity.this);

                builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
                builder2.setMessage("Bill total is "+strDouble).create().show();

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });


    }
    public void cancel(View view){
        finish();
    }

}
