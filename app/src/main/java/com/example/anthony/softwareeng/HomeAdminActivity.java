package com.example.anthony.softwareeng;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
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
        Firebase.setAndroidContext(this);
    }
    public void makeReservationAdmin(View view){
        Toast.makeText(HomeAdminActivity.this, "make reservation admin btn clicked", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(HomeAdminActivity.this,SpotPicker.class);
        Bundle b = getIntent().getExtras();
        b.putString("admin","true");
        i.putExtras(b);
        startActivity(i);
    }

    public void searchLicence(View view) {
        if (((EditText) findViewById(R.id.SearchLicencePlate_editText)).getText().toString().compareTo("")==0) {
            Toast.makeText(HomeAdminActivity.this, "Please enter a license Plate Number", Toast.LENGTH_SHORT).show();
        } else {
            String lpn = ((EditText) findViewById(R.id.SearchLicencePlate_editText)).getText().toString();
            ((EditText) findViewById(R.id.SearchLicencePlate_editText)).setText("");
            lpn = lpn.replaceAll("\\s+", "");
            final String licencePlate = lpn;
//========Compare wit server==================================================================
            final Firebase ref = new Firebase("https://carparkapp.firebaseio.com");
            ref.child("").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    // for(DataSnapshot i:snapshot.getChildren())
                    Intent intent;
                    if (snapshot.hasChild("reservation/" + licencePlate)) {//if license exist in resavation
                        Log.e("HomeAdmin", "Licenceplate found");

                        intent = new Intent(HomeAdminActivity.this, FindReservationActivity.class);
                        Bundle b = new Bundle();
                        b.putString("lpn", licencePlate);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                     else {
                            int check =0;
                            Log.e("DEBUG", "checking users");
                            for (DataSnapshot postSnapshot : snapshot.child("users").getChildren()) {
                                User user = postSnapshot.getValue(User.class);


                                if (user.getLicencePlateNum().compareToIgnoreCase(licencePlate) == 0) {
                                    check = 1;
                                    String a = snapshot.child("parkingSpaces/"+user.getSpaceNum()).getValue().toString();
                                    long time = Long.parseLong(a);

                                    if((new Date().getTime() - time) < 1800000){
                                        intent = new Intent(HomeAdminActivity.this, FindReservationActivity.class);
                                        Bundle b = new Bundle();
                                        b.putString("lpn", licencePlate);
                                        b.putString("ID", postSnapshot.getKey());
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                    else{Map<String, Object> spot = new HashMap<String, Object>();
                                        Toast.makeText(HomeAdminActivity.this, "reservation has expired", Toast.LENGTH_SHORT).show();
                                        spot.put("parkingSpaces/" + user.getSpaceNum(), "null");
                                        spot.put("users/" + postSnapshot.getKey() + "/spaceNum", "null");
                                        spot.put("users/" + postSnapshot.getKey() + "/licencePlateNum", "null");
                                        ref.updateChildren(spot);
                                    }


                                }

                            }
                        if(check == 0){
                                Toast.makeText(HomeAdminActivity.this, "no reservations with that license plate number", Toast.LENGTH_SHORT).show();
                        }

                        }
                    }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });

        }
    }
    }


