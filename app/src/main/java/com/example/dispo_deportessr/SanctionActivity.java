package com.example.dispo_deportessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.SanctionList;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SanctionActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private Toolbar mTopToolbar;
    private TextView daySanctioned,dayFree;
    private SharedPreferences userData,tokenAndRole,placeAndSport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanction);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        daySanctioned = findViewById(R.id.daySanctioned);
        dayFree = findViewById(R.id.dayFree);

        userData = getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        placeAndSport = getSharedPreferences("placeAndSport", Context.MODE_PRIVATE);
        String token = Util.getUserTokenPrefs(tokenAndRole);
        int id_place = Util.getIdPlacePrefs(placeAndSport);
        int id_users = Util.getUserIdPrefs(userData);

        Call<SanctionList> callDays = RetrofitClient
                .getInstance()
                .getApi()
                .getDays(token,id_place,id_users);

        callDays.enqueue(new Callback<SanctionList>() {
            @Override
            public void onResponse(Call<SanctionList> call, Response<SanctionList> response) {
                SanctionList days = response.body();
                SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
                String dateSaction = formater.format(days.getSanctionDay());
                String dateFree = formater.format(days.getFreeDay());
                String textSanction = getString(R.string.text_day_sanctioned);
                String textFree = getString(R.string.text_day_free);
                daySanctioned.setText(textSanction+dateSaction);
                dayFree.setText(textFree+dateFree);
            }

            @Override
            public void onFailure(Call<SanctionList> call, Throwable t) {

            }
        });

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.home);

        //BOTONES DE NAVEGACION
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        return true;

                    case R.id.list:
                        startActivity(new Intent(getApplicationContext(),WaitListActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.user:
                        startActivity(new Intent(getApplicationContext(), UserAdminActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.turnsUser:
                        startActivity(new Intent(getApplicationContext(),TurnActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

    }
}