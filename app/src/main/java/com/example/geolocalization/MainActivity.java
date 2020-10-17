package com.example.geolocalization;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText name1;
    private EditText email1;
    private EditText password1;
    private Button boton;
    private Button toMapsBtn;
    private Button notiBtn;

    NotificationCompat.Builder mBuilder;
    int id = 5463;
    String channel = "id01";

    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    private FusedLocationProviderClient mFusedLocationClient;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        name1 = findViewById(R.id.name);
        email1 = findViewById(R.id.email);
        password1 = findViewById(R.id.password);
        boton = findViewById(R.id.registro);
        toMapsBtn = findViewById(R.id.chngAct);
        notiBtn = findViewById(R.id.notif);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        subirLatLongFirebase();

        notiBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                triggerNotification();
            }
        });

        boton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                registroUsuario();
            }
        });
        toMapsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    //Validacioes para los EditText
    private void registroUsuario() {
        String correo = email1.getText().toString();
        String contraseña = password1.getText().toString();
        String nombre = name1.getText().toString();

        if (TextUtils.isEmpty(correo)) {
            Toast.makeText(this, "Ingresa un correo", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(contraseña)) {
            Toast.makeText(this, "Ingresa una contraseña", Toast.LENGTH_SHORT).show();
            return;
        } else if (contraseña.length() < 8) {
            Toast.makeText(this, "Tu contraseña debe ser mayor o igual a 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(this, "Ingresa un nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadInfo();

    }

    //Registro del usuario en Cloud Firestore
    void uploadInfo(){
        String correo = email1.getText().toString();
        String nombre = name1.getText().toString();

        Map<String, Object> map = new HashMap<>();
        map.put("Correo", correo);
        map.put("Nombre", nombre);
        mAuth.getCurrentUser();
        mFirestore.collection("perfiles").document(mAuth.getUid()).set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        createUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Registro Fallido" + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Autenticación del usuario en Firebase Auth
    void createUser(){
        String correo = email1.getText().toString();
        String contraseña = password1.getText().toString();
        mAuth.createUserWithEmailAndPassword(correo, contraseña).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Datos almacenados", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Ocurrio un error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void triggerNotification(){
        mBuilder = new NotificationCompat.Builder(MainActivity.this, channel);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Ofertas";
            String description = "Comunicación de ofertas a usuarios";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channel, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            NotificationManager mNotificationManager = getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(mChannel);

            mBuilder = new NotificationCompat.Builder(MainActivity.this, channel);
        }
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

        mBuilder.setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle("Tienes una  nueva oferta")
                .setContentText("Estrena hoy tu auto con tu crédito preaprobado por $200,000 a 60 meses con una tasa del 14.99%")
                .setTicker("Nueva Notificación")
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setDefaults(Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(MainActivity.this);
        mNotificationManager.notify(id, mBuilder.build());
    }

    private void subirLatLongFirebase() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.e("Latitud: ", +location.getLatitude()+" Longitud: "+location.getLongitude());

                            Map<String, Object> latlang = new HashMap<>();
                            latlang.put("latitud", location.getLatitude());
                            latlang.put("longitud", location.getLongitude());
                            mDatabase.child("usuarios").push().setValue(latlang);
                        }
                    }
                });
    }
}