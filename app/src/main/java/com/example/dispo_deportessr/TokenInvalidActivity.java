package com.example.dispo_deportessr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class TokenInvalidActivity extends AppCompatActivity {

    private SharedPreferences userData,sharedLogin;
    private SharedPreferences.Editor userDataEditor,sharedLoginEditor;
    private TextView textView1,textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_invalid);

        textView1 = findViewById(R.id.text1);
        textView2 = findViewById(R.id.textSecond);
        //LLAMO A PREFERENCES PARA ACCEDER AL TOKEN
        sharedLogin = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        sharedLoginEditor = sharedLogin.edit();

        //CREO SHARED PREFERENCES PARA GUARDAR LOS DATOS DE PERFIL DEL USUARIO
        userData = getSharedPreferences("dataUser",Context.MODE_PRIVATE);
        userDataEditor = userData.edit();

        textView1.setText("Token inválido o token expiró!");
        textView2.setText("En segundos se redirigirá al login para iniciar sesión nuevamente.");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                userDataEditor.clear();
                userDataEditor.commit();
                sharedLoginEditor.clear();
                sharedLoginEditor.commit();

                Intent intent = new Intent(TokenInvalidActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 10000);

    }
}