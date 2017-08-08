package com.agora;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.agora.model.Event;
import com.agora.model.Response;
import com.agora.network.NetworkUtil;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private static final int LOCATION_REQUEST=3337;
    private boolean first;
    private CompositeSubscription mSubscriptions;
    private ArrayList<Event> events;
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    Bitmap imageBitmap;
    private Marker userClick;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            checkLocationPermission();
        }
        init();
        mSubscriptions = new CompositeSubscription();
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        first = true;

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

    private void handleResponse(Event[] events) {
        this.events = new ArrayList<>(Arrays.asList(events));
        for (Event e: this.events){
            showEventsOnMap(e);
        }
    }
    private void showEventsOnMap(Event event){
        double lat = event.getLat();
        double lng = event.getLng();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lat, lng));
        markerOptions.title(event.getOwnerEmail().substring(0, event.getOwnerEmail().indexOf('@')) + ": " +event.getTitle());
        markerOptions.snippet(event.getInterest()  + ". Created At: " + event.getCreated_at());
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(imageBitmap));
        mMap.addMarker(markerOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
        mMap.setMyLocationEnabled(true);
        checkLocationPermission();
        initEventsOnTheMap();
        createLocationRequest();

    }
    private void checkLocationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }
    }
    private void init(){
        imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(String.valueOf(R.drawable.ic_event), "drawable", getPackageName()));
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 64, 64, false);
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
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

}
