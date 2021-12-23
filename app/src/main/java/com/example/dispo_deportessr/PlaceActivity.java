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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dispo_deportessr.Adapters.PlaceListAdapter;
import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.Place;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private ListView listViewPlaceList;
    private List<Place> places;
    private Toolbar mTopToolbar;
    private ProgressBar progressPlace;
    private SharedPreferences tokenAndRole, placeAndSport;
    private SharedPreferences.Editor placeAndSportEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewPlaceList = findViewById(R.id.listViewPlaces);
        progressPlace = findViewById(R.id.progressPlace);
        progressPlace.setVisibility(View.VISIBLE);

        //TRAIGO EL TOKEN
        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        String token = Util.getUserTokenPrefs(tokenAndRole);
        placeAndSport = getSharedPreferences("placeAndSport", Context.MODE_PRIVATE);
        placeAndSportEditor = placeAndSport.edit();
        int id_sports = Util.getIdSportsPrefs(placeAndSport);

        //LLAMADA PARA TRAER LOS COMPLEJOS DE FUTBOL
       getPlaces(token,id_sports);

        //ACCION DE CADA ITEM
        listViewPlaceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PlaceActivity.this, UserMainActivity.class);
                placeAndSportEditor.putInt("id_place", places.get(position).getId());
                placeAndSportEditor.putString("name_place", places.get(position).getName());
                placeAndSportEditor.commit();
                startActivity(intent);
            }
        });

        //BOTONES DE NAVEGACION
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.home);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        return true;

                    case R.id.list:
                        startActivity(new Intent(getApplicationContext(), WaitListActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.user:
                        startActivity(new Intent(getApplicationContext(), UserActivity.class));
                        overridePendingTransition(0, 0);
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

    private void getPlaces(String token, int id_sports) {
        Call<List<Place>> call = RetrofitClient
                .getInstance()
                .getApi()
                .getPlace(token, id_sports);
        call.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                places = (List<Place>) response.body();
                if (places.isEmpty()) {
                    Intent intent2 = new Intent(PlaceActivity.this, InProcessActivity.class);
                    startActivity(intent2);
                } else {
                    listViewPlaceList.setAdapter(new PlaceListAdapter(getApplicationContext(), places));
                    progressPlace.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(PlaceActivity.this, message_fail, Toast.LENGTH_SHORT, true).show();
            }
        });
    }
}