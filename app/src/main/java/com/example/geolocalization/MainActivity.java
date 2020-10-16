package com.example.geolocalization;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText name1;
    private EditText email1;
    private EditText password1;
    private Button boton;

    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

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

        boton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                registroUsuario();
            }
        });
    }

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
}