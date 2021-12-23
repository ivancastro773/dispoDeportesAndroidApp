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
import android.widget.Toast;

import com.example.dispo_deportessr.Admins.TurnRequestedActivity;
import com.example.dispo_deportessr.Admins.TurnsBusyActivity;
import com.example.dispo_deportessr.Admins.UserSantionedActivity;
import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.LoginResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectCantCourtsActivity extends AppCompatActivity {
    AutoCompleteTextView autoCompleteTextView;
    private Button button;
    private BottomNavigationView bottomNavigation;
    private Toolbar mTopToolbar;
    private SharedPreferences tokenAndRole,placeAndSport;
    private int id_place;
    private String token;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cant_courts);
        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        autoCompleteTextView = findViewById(R.id.cantCourtsSelected);
        button = findViewById(R.id.buttonSendSelected);


        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        token = Util.getUserTokenPrefs(tokenAndRole);
        placeAndSport = getSharedPreferences("placeAndSport", Context.MODE_PRIVATE);
        id_place = Util.getIdPlacePrefs(placeAndSport);

        //canchas
        String []option = {"1","2","3","4","5"};

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.item_court_number,option);
        autoCompleteTextView.setText(arrayAdapter.getItem(0).toString(),false);
        autoCompleteTextView.setAdapter(arrayAdapter);

        //BOTONES DE NAVEGACION

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.admin);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.turns:
                        startActivity(new Intent(getApplicationContext(), UserMainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.turnsRequested:
                        startActivity(new Intent(getApplicationContext(), TurnsBusyActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.userSantioned:
                        startActivity(new Intent(getApplicationContext(), UserSantionedActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.admin:
                        return true;
                }
                return false;
            }
        });
    }

    public void AddNewCourt(View view) {
        String cantCourt = autoCompleteTextView.getText().toString().trim();
        int cant =Integer.parseInt(cantCourt);

        Call<LoginResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .addNewCourt(token,id_place,cant);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse message = response.body();
                Toasty.success(SelectCantCourtsActivity.this,message.getMessage(),Toasty.LENGTH_LONG,true).show();
                Intent intent = new Intent(SelectCantCourtsActivity.this, UserAdminActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(SelectCantCourtsActivity.this,message_fail , Toast.LENGTH_SHORT,true).show();
            }
        });
    }
}