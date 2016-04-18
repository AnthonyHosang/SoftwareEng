package com.example.anthony.softwareeng;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SpotPicker extends AppCompatActivity {
int spot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_picker);
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
        final Firebase ref = new Firebase("https://carparkapp.firebaseio.com");
        ref.child("parkingSpaces").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<String> available = new ArrayList();
                for(DataSnapshot i:snapshot.getChildren()){
                    long a=0;
                    if(i.getValue().toString().compareToIgnoreCase("null")==0)
                        a = 0;
                    else if(i.getValue().toString().compareToIgnoreCase("in use")==0) {
                        Log.e("spot in use", i.getKey().toString());
                        a=new Date().getTime();
                    }
                    else
                        a =Long.parseLong(i.getValue().toString());
                    long now = new Date().getTime();
                    if(now-a>1800000){
                        available.add("Parking Space #"+i.getKey().toString());
                    }

                   /* Map<String, Object> nicknames = new HashMap<String, Object>();
                    long start = new Date().getTime();
                    nicknames.put("parkingSpaces/3", start);
                    nicknames.put("parkingSpaces/4", start);

                    ref.updateChildren(nicknames);*/


                }

                Spinner spinner = (Spinner) findViewById(R.id.spinner);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<String> adapter = new ArrayAdapter<>(SpotPicker.this,android.R.layout.simple_spinner_dropdown_item,available);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setPrompt("Select a parking position");
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String s  = parent.getSelectedItem().toString();
                        s = s.replaceAll("\\D+","");
                        int a = Integer.parseInt(s);
                        spot = a;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
    public void onClickNext(View view){
        Bundle b = getIntent().getExtras();
        String s  = b.getString("admin");
        b.putInt("spot",spot);
        Intent  i;
        if(s.compareToIgnoreCase("true")==0){
            i =  new Intent(SpotPicker.this,AdminReserveSpotActivity.class);
        }
        else
            i = new Intent(SpotPicker.this,ReservationInfoUserActivity.class);
        i.putExtras(b);
        startActivity(i);
        finish();

    }


}
