package com.example.dispo_deportessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.dispo_deportessr.Admins.TurnRequestedActivity;
import com.example.dispo_deportessr.Admins.UserSantionedActivity;
import com.example.dispo_deportessr.Utils.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SelectSearchActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private Toolbar mTopToolbar;
    //SHARED PREFERENCES PARA EL BUSCADOR
    private SharedPreferences searchForDate, tokenAndRole, placeAndSport;
    private SharedPreferences.Editor EditorsearchForDate;
    private int numberPlace;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_search);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tokenAndRole = getSharedPreferences("tokenAndRole",Context.MODE_PRIVATE);
        String roleAdmin = Util.getUserRolePrefs(tokenAndRole);

        //BUSCADOR POR FECHA
        searchForDate = getSharedPreferences("searchForDate", Context.MODE_PRIVATE);
        EditorsearchForDate = searchForDate.edit();
        EditorsearchForDate.clear();
        EditorsearchForDate.commit();

        placeAndSport = getSharedPreferences("placeAndSport", Context.MODE_PRIVATE);
        numberPlace = Util.getIdPlacePrefs(placeAndSport);


        //BOTONES DE NAVEGACION
        bottomNavigation = findViewById(R.id.bottomNavigation);
        if (roleAdmin.equals("admin")){
            bottomNavigation.inflateMenu(R.menu.menu_admin);
            bottomNavigation.setSelectedItemId(R.id.turns);
            bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.turns:
                            return true;

                        case R.id.turnsRequested:
                            startActivity(new Intent(getApplicationContext(), TurnRequestedActivity.class));
                            overridePendingTransition(0,0);
                            return true;

                        case R.id.userSantioned:
                            startActivity(new Intent(getApplicationContext(), UserSantionedActivity.class));
                            overridePendingTransition(0,0);
                            return true;

                        case R.id.admin:
                            startActivity(new Intent(getApplicationContext(), UserAdminActivity.class));
                            overridePendingTransition(0,0);
                            return true;
                    }
                    return false;
                }
            });
        }else {
            bottomNavigation.inflateMenu(R.menu.menu);
            bottomNavigation.setSelectedItemId(R.id.home);

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
                            startActivity(new Intent(getApplicationContext(), UserActivity.class));
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


    public void ForHour(View view) {
        Intent intent = new Intent(SelectSearchActivity.this,SelectHourActivity.class);
        intent.putExtra("num_id_place",numberPlace);
        startActivity(intent);
    }

    public void ForDate(View view) {
        Intent intent = new Intent(SelectSearchActivity.this,SelectDateActivity.class);
        startActivity(intent);
    }
}