package com.example.dispo_deportessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.dispo_deportessr.Admins.TurnRequestedActivity;
import com.example.dispo_deportessr.Admins.UserSantionedActivity;
import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.Place;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoPlaceActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigation;
    private TextView namePlace,description, address,phone;
    private SharedPreferences tokenAndRole,placeAndSport;
    private Toolbar mTopToolbar;
    private ImageView imagePlaceInfo;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_place);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        namePlace = findViewById(R.id.textInfoNamePlace);
        description = findViewById(R.id.textViewDescription);
        address = findViewById(R.id.textViewAddress);
        phone = findViewById(R.id.textViewPhone);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        imagePlaceInfo = findViewById(R.id.imagePlaceInfo);
        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);

        final String token = Util.getUserTokenPrefs(tokenAndRole);
        String role = Util.getUserRolePrefs(tokenAndRole);
        int num_id_place = getIntent().getIntExtra("num_id_place",0);

        getInfoPlace(token,num_id_place);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
                startActivity(intent);
            }
        });
        if (role.equals("admin")){

            bottomNavigation.inflateMenu(R.menu.menu_admin);
            bottomNavigation.setSelectedItemId(R.id.admin);
            bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.turns:
                            startActivity(new Intent(getApplicationContext(), UserMainActivity.class));
                            overridePendingTransition(0,0);
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
                            return true;
                    }
                    return false;
                }
            });
        }else{
            bottomNavigation.inflateMenu(R.menu.menu);
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

    private void getInfoPlace(String token,int num_id_place) {
        Call<Place> call = RetrofitClient
                .getInstance()
                .getApi()
                .getInfoPlace(token,num_id_place);

        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                Place place = response.body();

                if (place.getImage_url() == null){
                    imagePlaceInfo.setImageResource(R.drawable.ic_logo_image);
                }else {
                    Glide.with(InfoPlaceActivity.this)
                            .load(place.getImage_url())
                            .transition(DrawableTransitionOptions.withCrossFade(2000))
                            .into(imagePlaceInfo);
                }

                namePlace.setText(place.getName());
                description.setText(place.getDescription());
                address.setText(place.getAddress());
                phone.setText(place.getPhone());
            }


            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(InfoPlaceActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
            }
        });
    }
}