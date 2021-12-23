package com.example.dispo_deportessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dispo_deportessr.Admins.TurnRequestedActivity;
import com.example.dispo_deportessr.Admins.UserSantionedActivity;
import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.Court;
import com.example.dispo_deportessr.Models.Place;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    private BottomNavigationView bottomNavigation;
    private ListView listViewCourtList;
    private List<Court> courts;
    private SharedPreferences userData;
    private SharedPreferences preferencesTokenIdPhone;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int numberPlace;
    private Toolbar mTopToolbar;
    //SHARED PREFERENCES PARA EL BUSCADOR
    private SharedPreferences searchForDate,searchForHour, tokenAndRole,placeAndSport;
    private SharedPreferences.Editor EditorsearchForDate,EditorSearchForHour;

    //TABS
    private TabLayout tabLayout;
    private Fragment fragment = null;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent receiveIntent = getIntent();

        // DATOS PARA BUSCAR FECHA
        int forDate = receiveIntent.getIntExtra("forDate",0);
        String dateString = receiveIntent.getStringExtra("date");

        tokenAndRole = getSharedPreferences("tokenAndRole",Context.MODE_PRIVATE);
        String token = Util.getUserTokenPrefs(tokenAndRole);
        String roleAdmin = Util.getUserRolePrefs(tokenAndRole);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        //TITULO DEPENDE LA BUSQUEDA
       if (forDate == 2){
            mTopToolbar.setTitle("Fecha: "+dateString);
        }
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        placeAndSport = getSharedPreferences("placeAndSport", Context.MODE_PRIVATE);
        numberPlace = Util.getIdPlacePrefs(placeAndSport);

        //TABS
        tabLayout = findViewById(R.id.tab_layout);
        //BUSCO CANTIDAD DE CANCHAS
        Call<Place> callGetCourts = RetrofitClient
                .getInstance()
                .getApi()
                .getCantCourts(token,numberPlace);

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
                Toasty.warning(SearchActivity.this,message_fail , Toast.LENGTH_SHORT,true).show();
            }
        });

        fragment = new CourtFragment(numberPlace,1);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
        tabLayout.addOnTabSelectedListener(this);

        //BUSCADOR POR FECHA
        searchForDate = getSharedPreferences("searchForDate", Context.MODE_PRIVATE);
        EditorsearchForDate = searchForDate.edit();
        EditorsearchForDate.putInt("forDate",forDate);
        EditorsearchForDate.putString("date",dateString);
        EditorsearchForDate.commit();

        if (roleAdmin.equals("admin")){
            bottomNavigation.inflateMenu(R.menu.menu_admin);
            bottomNavigation.setSelectedItemId(R.id.turns);
            bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.turns:
                            return true;

                        case R.id.turnsRequested:
                            EditorsearchForDate.clear();
                            EditorsearchForDate.commit();
                            startActivity(new Intent(getApplicationContext(), TurnRequestedActivity.class));
                            overridePendingTransition(0,0);
                            return true;

                        case R.id.userSantioned:
                            EditorsearchForDate.clear();
                            EditorsearchForDate.commit();
                            startActivity(new Intent(getApplicationContext(), UserSantionedActivity.class));
                            overridePendingTransition(0,0);
                            return true;

                        case R.id.admin:
                            EditorsearchForDate.clear();
                            EditorsearchForDate.commit();
                            startActivity(new Intent(getApplicationContext(), UserAdminActivity.class));
                            overridePendingTransition(0,0);
                            return true;
                    }
                    return false;
                }
            });
        }else{
            //BOTONES DE NAVEGACION
            bottomNavigation.inflateMenu(R.menu.menu);
            bottomNavigation.setSelectedItemId(R.id.home);
            bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.home:
                            return true;

                        case R.id.list:
                            EditorsearchForDate.clear();
                            EditorsearchForDate.commit();
                            startActivity(new Intent(getApplicationContext(), WaitListActivity.class));
                            overridePendingTransition(0,0);
                            return true;

                        case R.id.user:
                            EditorsearchForDate.clear();
                            EditorsearchForDate.commit();
                            startActivity(new Intent(getApplicationContext(), UserAdminActivity.class));
                            overridePendingTransition(0,0);
                            return true;
                        case R.id.turnsUser:
                            EditorsearchForDate.clear();
                            EditorsearchForDate.commit();
                            startActivity(new Intent(getApplicationContext(), TurnActivity.class));
                            overridePendingTransition(0,0);
                            return true;
                    }
                    return false;
                }
            });
        }

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        fragment = new CourtFragment(numberPlace,tab.getPosition()+1);
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