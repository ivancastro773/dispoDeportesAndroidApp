package com.example.dispo_deportessr.Admins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dispo_deportessr.R;
import com.example.dispo_deportessr.TurnsBusyFragment;
import com.example.dispo_deportessr.UserAdminActivity;
import com.example.dispo_deportessr.UserMainActivity;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.Place;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TurnsBusyActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    private BottomNavigationView bottomNavigation;
    private SharedPreferences preferences,placeAndSport,adminData;
    private SharedPreferences.Editor adminDataEditor,placeAndSportEditor;
    private int id_place;
    private Toolbar mTopToolbar;

    //TABS
    private TabLayout tabLayout;
    private Fragment fragment = null;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turns_busy);

        //DATOS NECESARIOS
        preferences = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        placeAndSport = getSharedPreferences("placeAndSport", Context.MODE_PRIVATE);
        placeAndSportEditor = placeAndSport.edit();
        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        id_place = placeAndSport.getInt("id_place",0);
        //TOKEN DEL USUARIO
        String token = preferences.getString("token","error");

        //TABS
        tabLayout = findViewById(R.id.tab_layout);

        //BUSCO CANTIDAD DE CANCHAS
        Call<Place> callGetCourts = RetrofitClient
                .getInstance()
                .getApi()
                .getCantCourts(token,id_place);

        callGetCourts.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                Place place = response.body();
                int cant = place.getCant_courts();

                for (int i = 0; i < cant; ++i) {
                    int number = i+1;
                    tabLayout.addTab(tabLayout.newTab().setText("N°"+number));
                }
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(TurnsBusyActivity.this,message_fail , Toast.LENGTH_SHORT,true).show();
            }
        });

        fragment = new TurnsBusyFragment(id_place,1);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
        tabLayout.addOnTabSelectedListener(this);

        bottomNavigation.inflateMenu(R.menu.menu_admin);
        bottomNavigation.setSelectedItemId(R.id.turnsRequested);
        //BOTONES DE NAVEGACION
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.turns:
                        startActivity(new Intent(getApplicationContext(), UserMainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.turnsRequested:
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

    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        fragment = new TurnsBusyFragment(id_place,tab.getPosition()+1);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}