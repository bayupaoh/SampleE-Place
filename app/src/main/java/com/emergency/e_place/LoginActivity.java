package com.emergency.e_place;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.emergency.e_place.Util.SessionManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    SignInButton signInButton;
    GoogleApiClient mGoogleApiClient;
    private final int RC_SIGN_IN = 100;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setSession();
        setGoogleSignInAccount();
        declareWidget();
        setClickWidget();

    }

    private void setSession() {
        session = new SessionManager(LoginActivity.this);
    }


    private void setButtonGooglesignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Hasil", "handleSignInResult:" + result.isSuccess());
        Log.d("Hasil", "handleSignInResult:" + result.toString());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String nama = acct.getDisplayName();
            String email = acct.getEmail();
            String id = acct.getId();
            String foto = String.valueOf(acct.getPhotoUrl());

            Log.d("Info",nama);

            session.createLoginSession(id,nama,email,foto);


            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Snackbar.make(getCurrentFocus(),"Maaf Anda Gagal Login",Snackbar.LENGTH_LONG).show();
        }
    }

    private void setGoogleSignInAccount() {
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }



    private void setClickWidget() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonGooglesignIn();
            }
        });
    }

    private void declareWidget() {
        signInButton = (SignInButton) findViewById(R.id.signin_login_signin);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),connectionResult.getErrorMessage(),Toast.LENGTH_LONG).show();
    }
}
