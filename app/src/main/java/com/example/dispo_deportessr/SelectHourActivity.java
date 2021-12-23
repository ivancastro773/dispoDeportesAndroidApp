package com.example.dispo_deportessr;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.dispo_deportessr.Admins.TurnRequestedActivity;
import com.example.dispo_deportessr.Admins.UserSantionedActivity;
import com.example.dispo_deportessr.Utils.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SelectHourActivity extends AppCompatActivity {


    AutoCompleteTextView autoCompleteTextView;
    private Button button;
    private BottomNavigationView bottomNavigation;
    private Toolbar mTopToolbar;
    private SharedPreferences tokenAndRole;
    private int id_place;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_hour);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        String roleAdmin = Util.getUserRolePrefs(tokenAndRole);
        id_place = getIntent().getIntExtra("num_id_place",0);
        autoCompleteTextView = findViewById(R.id.hourSelected);

        //horas
        String []option = {"00","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19"
                            ,"20","21","22","23"};

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.item_court_number,option);
        autoCompleteTextView.setText(arrayAdapter.getItem(0).toString(),false);
        autoCompleteTextView.setAdapter(arrayAdapter);

        button = findViewById(R.id.buttonSendSelected);
        //BOTONES DE NAVEGACION
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.home);

        //BOTONES DE NAVEGACION
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

    public void searchForHour(View view) {
        String hour = autoCompleteTextView.getText().toString().trim();

        Intent intent = new Intent(SelectHourActivity.this, SearchForHourActivity.class);
        intent.putExtra("hour",hour);
        intent.putExtra("num_id_place",id_place);
        startActivity(intent);
    }
}