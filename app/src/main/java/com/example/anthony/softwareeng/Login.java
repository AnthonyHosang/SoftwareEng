package com.example.anthony.softwareeng;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.api.Auth;

public class Login extends  AppCompatActivity  implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private TextView userName;
    private SharedPreferences sharedPreferences;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        //Once the token exists, there is no need to login the user.
        sharedPreferences = this.getApplicationContext().getSharedPreferences("google_token_info", MODE_PRIVATE);
      /*  if (sharedPreferences.contains("token")) {
            Intent intent = new Intent(Login.this, HomeUserActivity.class);
            startActivity(intent);
        }*/

        //setting application context
        Firebase.setAndroidContext(this);

        //find the userID text view
        findViewById(R.id.Login_btn).setOnClickListener(this);
        //userName = (TextView) findViewById(R.id.userId);

        //set up google sign-in options and the apiClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        progress = ProgressDialog.show(this, "Signing in...",
                "Please wait...", true);
        Log.e("Debug","handleSignIn");
        final Context ctx = this;
        //userName.setText(result.getSignInAccount().getDisplayName());
        if (result.isSuccess()) {
            Log.e("Debug","was successful");
            final GoogleSignInResult gsir = result;
            //create thread to obtain the authentication token which will be used for firebase login.
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Signed in successfully, show authenticated UI.
                    final GoogleSignInAccount acct = gsir.getSignInAccount();

                    String scopes = "oauth2:profile email";
                    try {
                        final String token = GoogleAuthUtil.getToken(getApplicationContext(), acct.getEmail(), scopes);
                        final Firebase ref = new Firebase("https://carparkapp.firebaseio.com");
                        ref.authWithOAuthToken("google", token, new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(final AuthData authData) {
                                // the Google user is now authenticated with your Firebase app
                                // we can now put the token into the shared preferences.
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("token", token);
                                editor.commit();
                                Log.i("AUTH DATA", ">>>> " + authData.getUid());
                                //store user data to firebase

                                //checking to see if the user exsit in the database



                                ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                       // for(DataSnapshot i:snapshot.getChildren())
                                        Intent intent;
                                            if(snapshot.hasChild(authData.getUid())){//if user exist
                                                String isAdmin = snapshot.child(authData.getUid().toString()+"/admin").getValue().toString();

                                                if(isAdmin.compareToIgnoreCase("true")==0){//id user is an admin
                                                     intent = new Intent(Login.this, HomeAdminActivity.class);


                                                }
                                                else{//if user is not an admin
                                                     intent = new Intent(Login.this, HomeUserActivity.class);
                                                }

                                             }
                                        else{//user does not exist
                                                Log.e("debug","creating user");
                                                ref.child("/users/" + authData.getUid()).setValue(new User(acct.getDisplayName(), authData.getProviderData().get("profileImageURL").toString(),"false"));
                                                intent = new Intent(Login.this, HomeUserActivity.class);
                                                Toast.makeText(Login.this, "Cashiers need to contact ur administrators to continue to admin ", Toast.LENGTH_SHORT).show();
                                            }
                                        Bundle b = new Bundle();
                                        b.putString("ID",authData.getUid());
                                        intent.putExtras(b);
                                        progress.dismiss();
                                        ctx.startActivity(intent);

                                    }
                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                    }
                                });


                             /*   ref.child("user/"+authData.getUid()+"/admin").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        Log.e("Value",snapshot.getValue()+"");  //prints "Do you have data? You'll love Firebase."

                                        Intent intent = new Intent(Login.this, HomeAdminActivity.class);
                                        ctx.startActivity(intent);
                                    }
                                    @Override public void onCancelled(FirebaseError error) { }
                                });*/

                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                // there was an error
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    private void signIn() {
        Log.e("Debug","SignIn");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View v) {
        Log.e("Debug","onCLick");
        switch (v.getId()) {
            case R.id.Login_btn:
                signIn();
                break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}

