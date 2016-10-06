package com.uukeshov.gpslocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private boolean mRequestingLocationUpdates = false;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private PolylineOptions mPolylineOptions;
    private LatLng mLatLng;
    private Integer busPosition = 0;
    private LatLngModel latLng;
    private MyTask mt;
    private Marker start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.buildGoogleApiClient();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkPermissionForLocation();
        latLng = new LatLngModel();
        latLng.addLatLng();
        mt = new MyTask();

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionForLocation();
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        addLine();
        addMarker();
        doSomethingRepeatedly();
        //mt.execute();
    }

    public void requestPermissionForLocation() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                Constants.PermissionCode);
    }

    private void reloadActivity() {
        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        } else {
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        updateCamera();
        updatePolyline();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mRequestingLocationUpdates) {
            LocationRequest mLocationRequest = createLocationRequest();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionForLocation();
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            initializePolyline();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    private void initializePolyline() {
        mGoogleMap.clear();
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(Color.BLUE).width(10);
        mGoogleMap.addPolyline(mPolylineOptions);
    }

    private void updatePolyline() {
        mPolylineOptions.add(mLatLng);
        mGoogleMap.clear();
        mGoogleMap.addPolyline(mPolylineOptions);
    }

    private void updateCamera() {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 14));
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this).addApi(LocationServices.API)
                .build();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.TIMEINTERVAL);
        mLocationRequest.setFastestInterval(Constants.DISTANCENTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public void checkPermissionForLocation() {
        boolean isGranted = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionForLocation();
            isGranted = true;
        }

        if (isGranted) {
            reloadActivity();
        }
    }

    private void addLine() {
        Polyline line = mGoogleMap.addPolyline(new PolylineOptions()
                .add(new LatLng(42.877526, 74.574913), new LatLng(42.876331, 74.596027))
                .add(new LatLng(42.876331, 74.596027), new LatLng(42.874885, 74.637054))
                .add(new LatLng(42.874885, 74.637054), new LatLng(42.858047, 74.636623))
                .add(new LatLng(42.858047, 74.636623), new LatLng(42.854985, 74.634992))
                .add(new LatLng(42.854985, 74.634992), new LatLng(42.853288, 74.633903))
                .add(new LatLng(42.853288, 74.633903), new LatLng(42.846029, 74.634771))
                .add(new LatLng(42.846029, 74.634771), new LatLng(42.841651, 74.636404))
                .width(10)
                .color(Color.RED));
    }

    private void addMarker() {
        Marker start = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(42.877526, 74.574913))
                .title("Начало маршрута")
                .snippet("Fake route: 1232"));
        Marker end = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(42.841651, 74.636404))
                .title("Конец маршрута")
                .snippet("Fake route: 1232"));
    }

    private void busMarker() {
        if (getBusPosition() <= 5) {
            mGoogleMap.clear();
            addLine();
            addMarker();
            start = mGoogleMap.addMarker(new MarkerOptions()
                    .position(latLng.getLatLngItem(getBusPosition()))
                   .icon(BitmapDescriptorFactory.fromResource(R.drawable.quantum_ic_play_arrow_grey600_36))
                    .title("Маршрутка тут!")
                    .snippet("Fake route: 1232"));
            setBusPosition(getBusPosition() + 1);
        } else {
            setBusPosition(0);
        }
    }

    public Integer getBusPosition() {
        return busPosition;
    }

    public void setBusPosition(Integer busPosition) {
        this.busPosition = busPosition;
    }

    private void doSomethingRepeatedly() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    new MyTask().execute();
                } catch (Exception e) {
                }
            }
        }, 0, 10000);
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            busMarker();
        }
    }
}