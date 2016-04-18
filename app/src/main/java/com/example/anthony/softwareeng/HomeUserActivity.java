package com.example.anthony.softwareeng;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeUserActivity extends AppCompatActivity {
    final Firebase ref = new Firebase("https://carparkapp.firebaseio.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);
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
        ref.child("").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Bundle b = getIntent().getExtras();
                String id = b.getString("ID");
                String spot = snapshot.child("users/"+id+"/spaceNum").getValue().toString();


                if(spot.compareToIgnoreCase("null")==0) {
                    ((Button) findViewById(R.id.MakeReservaion_btn)).setEnabled(true);

                    ((Button) findViewById(R.id.reservationCancel_btn)).setEnabled(false);
                }
                else{
                    ((Button) findViewById(R.id.MakeReservaion_btn)).setEnabled(false);

                    ((Button) findViewById(R.id.reservationCancel_btn)).setEnabled(true);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void makeReservationuser(View view){
        Toast.makeText(HomeUserActivity.this, "make reservation user btn clicked", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(HomeUserActivity.this,SpotPicker.class);
            Bundle b = getIntent().getExtras();
            b.putString("admin","false");
            i.putExtras(b);
            startActivity(i);
       ((Button) findViewById(R.id.MakeReservaion_btn)).setEnabled(false);

        ((Button) findViewById(R.id.reservationCancel_btn)).setEnabled(true);
    }



    public void cancelReservation(View view){
        Bundle b = getIntent().getExtras();
        final String id = b.getString("ID");
                Log.e("id",id);

        ref.child("").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                String i = snapshot.child("users/"+id+"/spaceNum").getValue().toString();
                Map<String, Object> spot = new HashMap<String, Object>();
                spot.put("parkingSpaces/"+i,"null");
                spot.put("users/"+id+"/licencePlateNum","null");
                spot.put("users/"+id+"/spaceNum","null");
                ref.updateChildren(spot);

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        ((Button) findViewById(R.id.MakeReservaion_btn)).setEnabled(true);

        ((Button) findViewById(R.id.reservationCancel_btn)).setEnabled(false);

    }

}
