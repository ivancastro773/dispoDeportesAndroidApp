package com.example.dispo_deportessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.dispo_deportessr.Utils.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class UserActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private TextView profileName, profileEmail;
    private MaterialButton buttonChangePassword;
    private MaterialButton finishSession;
    private SharedPreferences tokenAndRole, dataUser;
    private SharedPreferences.Editor tokenAndRoleEditor, dataUserEditor;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileMail);
        finishSession = findViewById(R.id.finishSession);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);

        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        tokenAndRoleEditor = tokenAndRole.edit();
        //CREO SHARED PREFERENCES PARA GUARDAR LOS DATOS DE PERFIL DEL USUARIO
        dataUser = getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        dataUserEditor = dataUser.edit();
        firebaseAuth = FirebaseAuth.getInstance();
        profileName.setText(Util.getUserNamePrefs(dataUser));
        profileEmail.setText(Util.getUserEmailPrefs(dataUser));

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://dispo-deportes-sr.herokuapp.com/auth/form"));
                startActivity(intent);
            }
        });
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.user);

        //BOTONES DE NAVEGACION
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.list:
                        startActivity(new Intent(getApplicationContext(), WaitListActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.user:
                        return true;


                    case R.id.turnsUser:
                        startActivity(new Intent(getApplicationContext(), TurnActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }
    public void finishSession(View view) {
        tokenAndRoleEditor.clear();
        tokenAndRoleEditor.commit();
        dataUserEditor.clear();
        dataUserEditor.commit();
        firebaseAuth.signOut();
        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}