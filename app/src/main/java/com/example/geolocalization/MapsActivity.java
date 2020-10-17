package com.example.geolocalization;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    Button bmapa;
    Button bterreno;
    Button bhibrido;
    Button binterior;
    private DatabaseReference mDatabase;
    private ArrayList<Marker> tmpReal = new ArrayList<>();
    private ArrayList<Marker> realTime = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        bmapa = (Button) findViewById(R.id.bmapa);
        bterreno = (Button) findViewById(R.id.bterreno);
        bhibrido = (Button) findViewById(R.id.bhibrido);
        binterior = (Button) findViewById(R.id.binterior);

        bmapa.setOnClickListener(this);
        bterreno.setOnClickListener(this);
        bhibrido.setOnClickListener(this);
        binterior.setOnClickListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        countDownTimer();
    }

    private void countDownTimer(){
        new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("seconds remaining", "" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                onMapReady(mMap);
            }
        }.start();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mDatabase.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (Marker marker:realTime){
                    marker.remove();
                }

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    MapsTake mt = snapshot.getValue(MapsTake.class);
                    Double latitud = mt.getLatitud();
                    Double longitud = mt.getLongitud();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitud,longitud));

                    tmpReal.add(mMap.addMarker(markerOptions));
                }

                realTime.clear();
                realTime.addAll(tmpReal);
                countDownTimer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bmapa:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.bhibrido:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.bterreno:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.binterior:
                // Algunos edificios tienen mapa de interior. Hay que ponerse sobre ellos y directamente veremos las plantas
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(-33.86997, 151.2089), 18));

                break;
            default:
                break;
        }
    }
}