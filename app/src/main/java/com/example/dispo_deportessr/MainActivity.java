package com.example.dispo_deportessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispo_deportessr.Adapters.SportsListAdapter;
import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.User;
import com.example.dispo_deportessr.Models.Sports;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private BottomNavigationView bottomNavigation;
    private ListView listViewSports;
    private List<Sports> sports;
    private SharedPreferences userData, tokenAndRole, placeAndSport;
    private SharedPreferences.Editor userDataEditor, placeAndSportEditor;
    private int invalidToken = 401;
    private SharedPreferences.Editor NSportEditor;
    private ProgressBar progressMain;
    private boolean wifiConnected = false;
    private boolean mobileConnected = false;
    private TextView messageConection;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewSports = (ListView) findViewById(R.id.listViewSports);
        progressMain = findViewById(R.id.progressMain);
        messageConection = findViewById(R.id.messageConection);
        progressMain.setVisibility(View.VISIBLE);

        //VERIFICO SI HAY CONEXION A INTERNET
        checkNetworkConnection();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


        //CREO PREFRENCES PARA GUARDAR LOS DATOS DE USUARIO
        userData = getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        userDataEditor = userData.edit();

        placeAndSport = getSharedPreferences("placeAndSport", Context.MODE_PRIVATE);
        placeAndSportEditor = placeAndSport.edit();

        //CREO EL METODO SHARED PREFERENCES PARA GUARDAR DATOS DEL LOGIN
        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);

        //TRAIGO EL TOKEN
        String token = Util.getUserTokenPrefs(tokenAndRole);
        role = Util.getUserRolePrefs(tokenAndRole);
        if (Util.getUserNamePrefs(userData) == "empty") {
            getDataUser(token);
        }

        //LLAMADA A LA API PARA TRAER LOS DEPORTES
        showSports(token);

        //ACCION DE CADA ITEM DE LA LISTA
        listViewSports.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //otras canchas
                Intent intent = new Intent(MainActivity.this, PlaceActivity.class);
                placeAndSportEditor.putInt("id_sports", sports.get(position).getId());
                placeAndSportEditor.commit();
                startActivity(intent);
            }
        });

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.home);
        //BOTONES DE NAVEGACION
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

                        if (role == "admin"){
                            startActivity(new Intent(getApplicationContext(), UserAdminActivity.class));
                            overridePendingTransition(0, 0);
                            return true;
                        }else{
                            startActivity(new Intent(getApplicationContext(), UserActivity.class));
                            overridePendingTransition(0, 0);
                            return true;
                        }

                    case R.id.turnsUser:
                        startActivity(new Intent(getApplicationContext(), TurnActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    private void showSports(String token) {
        Call<List<Sports>> call = RetrofitClient
                .getInstance()
                .getApi()
                .getSports(token);

        call.enqueue(new Callback<List<Sports>>() {
            @Override
            public void onResponse(Call<List<Sports>> call, Response<List<Sports>> response) {

                if (response.code() == invalidToken) {
                    Intent intent = new Intent(MainActivity.this, TokenInvalidActivity.class);
                    startActivity(intent);
                } else {
                    sports = (List<Sports>) response.body();
                    listViewSports.setAdapter(new SportsListAdapter(getApplicationContext(), sports));
                    progressMain.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<List<Sports>> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(MainActivity.this, message_fail, Toast.LENGTH_SHORT, true).show();
            }
        });

    }

    private void getDataUser(String token) {
        Call<User> calldata = RetrofitClient
                .getInstance()
                .getApi()
                .getProfile(token);

        calldata.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.code() == invalidToken) {
                    Intent intent = new Intent(MainActivity.this, TokenInvalidActivity.class);
                    startActivity(intent);
                } else {
                    User user = response.body();
                    userDataEditor.putInt("id", user.getId());
                    userDataEditor.putString("email", user.getEmail());
                    userDataEditor.putString("name", user.getName());
                    userDataEditor.commit();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(MainActivity.this, message_fail, Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            wifiConnected = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;

            if (wifiConnected) {
                messageConection.setVisibility(View.GONE);
            } else if (mobileConnected) {
                messageConection.setVisibility(View.GONE);
            }
        } else {
            messageConection.setVisibility(View.VISIBLE);
        }
    }

}