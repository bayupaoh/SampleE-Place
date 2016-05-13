package com.emergency.e_place;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emergency.e_place.Util.SessionManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    GoogleApiClient mGoogleApiClient;

    NavigationView navigationView;
    MapFragment mMapFragment;
    GoogleMap googleMap;
    GoogleApiClient mGoogleApiClientForPlace;
    int PLACE_PICKER_REQUEST = 1;
    FloatingActionButton fabWheels;
    FloatingActionButton fabToilets;
    FloatingActionButton fabHomes;
    ImageView btnGooglePlace;

    TextView namaUser;
    TextView emailUser;
    ImageView fotoUser;

    String name,email,foto;

    SessionManager session;
    boolean awal = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setGoogleSignInAccount();
        declarationWidget();
        setMap();
        setgooglePlace();
        setToolBar();
        settingNavBar();
        setFloatingButton();
        setMap();
        setSessionManager();
        setBtnGooglePlus();
    }

    private void setBtnGooglePlus() {
        btnGooglePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int PLACE_PICKER_REQUEST = 1;
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setSessionManager() {
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // name
        name = user.get(SessionManager.KEY_NAMAUSER);
        // email
        email = user.get(SessionManager.KEY_EMAILUSER);

        foto = user.get(SessionManager.KEY_FOTOUSER);

        namaUser.setText(name);
        emailUser.setText(email);
        Picasso.with(getApplicationContext()).load(foto).into(fotoUser);
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


    private void setgooglePlace() {
        mGoogleApiClientForPlace = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


    }

    private void setMap() {
        mMapFragment.getMapAsync(this);

    }

    private void setFloatingButton() {
        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);
        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });


    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
        session.logoutUser();
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getApplicationContext(),data);

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                googleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getAddress().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }
        }
    }
    private void settingNavBar() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        navigationView.setNavigationItemSelectedListener(navItemSelect);
    }

    private void setToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void declarationWidget() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_main_menu);
        fotoUser = (ImageView) findViewById(R.id.img_headnav_ava);
        namaUser = (TextView) findViewById(R.id.txt_headnav_name);
        emailUser = (TextView) findViewById(R.id.txt_headnav_email);
        btnGooglePlace = (ImageView) findViewById(R.id.img_main_googleplace);
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
    }

    NavigationView.OnNavigationItemSelectedListener navItemSelect = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            menuItem.setCheckable(true);
            drawerLayout.closeDrawer(GravityCompat.START);

            switch (menuItem.getItemId()){
                case R.id.id_menu_map:
                    return true;
                case R.id.id_menu_logout :
                    revokeAccess();
                    return true;
                case R.id.id_menu_setting :
                    Toast.makeText(getApplicationContext(),"Aku ditekan",Toast.LENGTH_LONG).show();
                    return true;
                default:
                    return true;
            }
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        GoogleMapOptions option = new GoogleMapOptions();
        option.compassEnabled(true);
        this.googleMap.setTrafficEnabled(true);
        googleMap.setMyLocationEnabled(true);

        googleMap.setOnMyLocationChangeListener(myLocationChangeListener);

    }

    private OnMyLocationChangeListener myLocationChangeListener = new OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            if(awal) {
                awal=false;
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            }
        }
    };

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
