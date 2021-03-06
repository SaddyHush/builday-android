package com.agora;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agora.model.Event;
import com.agora.model.FCMToken;
import com.agora.model.Response;
import com.agora.model.User;
import com.agora.network.NetworkUtil;
import com.agora.utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class MapsActivity extends Activity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String mToken;
    private String mEmail;
    private String mFCMToken;
    private SharedPreferences mSharedPreferences;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private static final int INITIAL_REQUEST=1337;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+1;
    private boolean first;
    private CompositeSubscription mSubscriptions;
    private ArrayList<Event> events;
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    Bitmap imageBitmap;
    private ImageButton profileButton;
    private Marker userClick;
    private ImageButton logout;
    private FloatingActionButton fab;
    private ImageButton notification;

    private Toolbar toolbar;
    private HashMap<Marker, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();
        if (!canAccessLocation())
            checkLocationPermission();
        else {
            map = new HashMap<Marker, String>();
            init();

            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);

            mapFragment.getMapAsync(this);
            first = true;
        }
        updateTokenInServer(mEmail, mFCMToken);
    }

    private void updateTokenInServer(String mEmail, String fcmToken){
        FCMToken token = new FCMToken();
        token.setEmail(mEmail);
        token.setFcmToken(fcmToken);
        mSubscriptions.add(NetworkUtil.getRetrofit().updateFCMToken(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleTokenUpdate,this::handleTokenUpdateError));
    }

    public void handleTokenUpdate(Response response){
        Log.d("Token Refresh Success", response.getMessage());
    }

    public void handleTokenUpdateError(Throwable error){
        Log.d("Token Refresh Error", error.getMessage());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap != null) {
            initEventsOnTheMap();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        map = new HashMap<Marker, String>();
        init();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        first = true;
        if (mMap!=null) {
            initEventsOnTheMap();
        }

    }


    private void handleResponse(Event[] events) {
        if (map!= null)
            mMap.clear();
        this.events = new ArrayList<>(Arrays.asList(events));
        for (Event e: this.events){
            showEventsOnMap(e);
        }
    }
    private void showEventsOnMap(Event event){
        getUserByEmail(event.getOwnerID(), event);
    }

    private void getUserByEmail(String email, Event event){
        mSubscriptions.add(NetworkUtil.getRetrofit().getOtherUserProfile(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(v -> continueShowEvent(v, event),this::handleError));
    }


    private void continueShowEvent(User user, Event event){
        double lat = event.getLat();
        double lng = event.getLng();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lat, lng));
        markerOptions.title(user.getName() + " " + user.getSurname() + " wants to " + event.getTitle());
        markerOptions.snippet(event.getInterest());
//        MapUtils mapUtils = new MapUtils();
//        Bitmap bitmap = mapUtils.GetBitmapMarker(getApplicationContext(), R.drawable.ic_event, "1");


        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createStoreMarker(event.getTitle())));
        if(mMap != null) {
            map.put(mMap.addMarker(markerOptions), event.get_id());
        }
        mMap.setOnMarkerClickListener(marker -> {
            if (marker.equals(userClick)) {
                return true;
            }
                Intent intent = new Intent(MapsActivity.this, EventDisplayActivity.class);
                intent.putExtra("eventID", map.get(marker));
                startActivity(intent);
                return false;

        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        initEventsOnTheMap();
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style_json));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                if (userClick != null) {
                    userClick.remove();
                }
                userClick = mMap.addMarker(markerOptions);

            }
        });

        if (canAccessLocation()) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
            }
        }
        checkLocationPermission();
        initEventsOnTheMap();
        createLocationRequest();

    }
    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }
    private void checkLocationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !canAccessLocation()) {
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }
    }
    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
        }
        return true;
    }

    private void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        getSupportActionBar().setTitle(null);

        imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(String.valueOf(R.drawable.ic_event), "drawable", getPackageName()));
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 64, 64, false);
        profileButton = (ImageButton) findViewById(R.id.btnProfile);
        logout = (ImageButton) findViewById(R.id.logoutbutton);
        notification = (ImageButton) findViewById(R.id.notifications);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fab = (FloatingActionButton) findViewById(R.id.btnOk);
        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    float zoomLevel = 16.0f;
                    if(first) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel));
                        first = false;
                    }
                }
            };
        };
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userClick == null){
                    Toast toast = Toast.makeText(MapsActivity.this, "You have not selected a place!", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Intent intent = new Intent(MapsActivity.this, CreateEvent.class);
                    intent.putExtra("position", userClick.getPosition());
                    startActivity(intent);
                }
            }
        });
        notification.setOnClickListener(v -> startActivity(new Intent(MapsActivity.this, NotificationActivity.class)));

        logout.setOnClickListener(v -> logout());

        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        checkLocationPermission();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null);
    }
    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return mLocationRequest;
    }
    private Bitmap createStoreMarker(String title) {
        View markerLayout = getLayoutInflater().inflate(R.layout.marker, null);

        ImageView markerImage = (ImageView) markerLayout.findViewById(R.id.marker_image);
        TextView markerRating = (TextView) markerLayout.findViewById(R.id.marker_text);
        markerImage.setImageResource(R.drawable.ic_event);
        markerRating.setText(title);

        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
    }

    private void initEventsOnTheMap(){
        mSubscriptions.add(NetworkUtil.getRetrofit().getEvents()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleError(Throwable e) {

        if (e instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) e).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
            } catch (IOException error) {
                error.printStackTrace();
            }
        } else {

            Toast.makeText(this, "Network Error !", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    private void initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
        mFCMToken = mSharedPreferences.getString(Constants.FCMTOKEN, "");

    }


    private void logout() {

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.EMAIL,"");
        editor.putString(Constants.TOKEN,"");
        editor.apply();
        finish();
        startActivity(new Intent(MapsActivity.this, MainActivity.class));
    }

}
