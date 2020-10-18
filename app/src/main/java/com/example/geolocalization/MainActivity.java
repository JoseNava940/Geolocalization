package com.example.geolocalization;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.collect.Maps;

public class MainActivity extends AppCompatActivity {

    Button IrLogin;
    Button IrRegistro;
    Button IrMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IrLogin = findViewById(R.id.IrLogin);
        IrRegistro = findViewById(R.id.IrRegistro);
        IrMapa = findViewById(R.id.IrMaps);
        IrLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }
        });
        IrRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Registro.class));
                finish();
            }
        });

        IrMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
                finish();
            }
        });
    }
}
