package com.example.dispo_deportessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dispo_deportessr.Admins.TurnsBusyActivity;
import com.example.dispo_deportessr.Admins.UserSantionedActivity;
import com.example.dispo_deportessr.Admins.HolidaysAdminActivity;
import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.CodeMsjResponse;
import com.example.dispo_deportessr.Models.Court;
import com.example.dispo_deportessr.Models.Place;
    import com.example.dispo_deportessr.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserMainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    private BottomNavigationView bottomNavigation;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listViewCourtList;
    private List<Court> courts;
    private SharedPreferences tokenAndRole,placeAndSport,adminData;
    private SharedPreferences.Editor adminDataEditor,placeAndSportEditor;
    private String role;
    private int id_sports,id_place;
    private Toolbar mTopToolbar;

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
        setContentView(R.layout.activity_main_admin);
        //DATOS NECESARIOS
        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        placeAndSport = getSharedPreferences("placeAndSport", Context.MODE_PRIVATE);
        placeAndSportEditor = placeAndSport.edit();
        String namePlace = placeAndSport.getString("name_place","error");
        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTopToolbar.setTitle(namePlace);
        setSupportActionBar(mTopToolbar);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        id_sports = Util.getIdSportsPrefs(placeAndSport);
        id_place = Util.getIdPlacePrefs(placeAndSport);


        //CREO PREFRENCES PARA GUARDAR LOS DATOS DE USUARIO
        adminData = getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        adminDataEditor = adminData.edit();


        //TOKEN DEL USUARIO
        String token = Util.getUserTokenPrefs(tokenAndRole);
        role = Util.getUserRolePrefs(tokenAndRole);

        //TABS
        tabLayout = findViewById(R.id.tab_layout);
        //BUSCO CANTIDAD DE CANCHAS
        searchCantCourts(token);

        fragment = new CourtFragment(id_place,1);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
        tabLayout.addOnTabSelectedListener(this);

        if (Util.getUserNamePrefs(adminData) == "empty") {
            //LLAMADA A LA API PARA TRAER LOS DATOS DEL USUARIO
            savedDataUser(token);
        }

        if (role.equals("admin")){
            //LLAMADA VERIFICAR SI SE ENCUENTRA DE VACACIONES
            Call<CodeMsjResponse> callVerifHoly = RetrofitClient
                    .getInstance()
                    .getApi()
                    .verifHolidays(token,id_place);

            callVerifHoly.enqueue(new Callback<CodeMsjResponse>() {
                @Override
                public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                    CodeMsjResponse message = response.body();

                    if (message.getCode() == 1){
                        Intent intent = new Intent(UserMainActivity.this, HolidaysAdminActivity.class);
                        startActivity(intent);
                    }else {
                        Log.e("holidays",message.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<CodeMsjResponse> call, Throwable t) {
                    String message_fail = getString(R.string.fail_connection_api);
                    Toasty.warning(UserMainActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
                }
            });

            bottomNavigation.inflateMenu(R.menu.menu_admin);
            bottomNavigation.setSelectedItemId(R.id.turns);
            //BOTONES DE NAVEGACION
            bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.turns:
                            return true;

                        case R.id.turnsRequested:
                            startActivity(new Intent(getApplicationContext(), TurnsBusyActivity.class));
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

        }else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            bottomNavigation.inflateMenu(R.menu.menu);
            bottomNavigation.setSelectedItemId(R.id.home);

            bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.home:
                            return true;

                        case R.id.list:
                            startActivity(new Intent(getApplicationContext(), WaitListActivity.class));
                            overridePendingTransition(0,0);
                            return true;

                        case R.id.user:
                            startActivity(new Intent(getApplicationContext(), UserActivity.class));
                            overridePendingTransition(0,0);
                            return true;
                        case R.id.turnsUser:
                            startActivity(new Intent(getApplicationContext(), TurnActivity.class));
                            overridePendingTransition(0,0);
                            return true;
                    }
                    return false;
                }
            });
        }

    }

    private void savedDataUser(String token) {
        Call<User> calldata = RetrofitClient
                .getInstance()
                .getApi()
                .getProfile(token);

        calldata.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                adminDataEditor.putInt("id", user.getId());
                adminDataEditor.putString("email", user.getEmail());
                adminDataEditor.putString("name", user.getName());
                adminDataEditor.commit();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(UserMainActivity.this, message_fail, Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void searchCantCourts(String token) {
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
                Toasty.warning(UserMainActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(role.equals("admin")){
            inflater.inflate(R.menu.menu_options_admin,menu);
        }else {
            inflater.inflate(R.menu.menu_options,menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.action_search:
                intent = new Intent(UserMainActivity.this, SelectSearchActivity.class);
                intent.putExtra("num_id_place",id_place);
                startActivity(intent);
                return true;
            case R.id.action_block:
                intent = new Intent(UserMainActivity.this, BlockActivity.class);
                intent.putExtra("num_id_place",id_place);
                intent.putExtra("num_id_sports",id_sports);
                startActivity(intent);
                return true;
            case R.id.action_info:

                Intent intent2 = new Intent(UserMainActivity.this, InfoPlaceActivity.class);
                intent2.putExtra("num_id_place",id_place);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        fragment = new CourtFragment(id_place,tab.getPosition()+1);
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