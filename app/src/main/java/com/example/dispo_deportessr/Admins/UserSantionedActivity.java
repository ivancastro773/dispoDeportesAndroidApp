package com.example.dispo_deportessr.Admins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dispo_deportessr.Adapters.SanctionListAdapter;
import com.example.dispo_deportessr.UserAdminActivity;
import com.example.dispo_deportessr.UserMainActivity;
import com.example.dispo_deportessr.R;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.LoginResponse;
import com.example.dispo_deportessr.Models.SanctionList;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSantionedActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private ListView listViewSanctionList;
    private List<SanctionList> sanctions;
    private SharedPreferences tokenAndRole,placeAndSport;
    private ProgressBar progressUserSanctioned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_santioned);

        listViewSanctionList = findViewById(R.id.listViewSanctionList);
        progressUserSanctioned = findViewById(R.id.progressUserSanctioned);
        progressUserSanctioned.setVisibility(View.VISIBLE);

        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        final String token = tokenAndRole.getString("token","null");
        placeAndSport = getSharedPreferences("placeAndSport", Context.MODE_PRIVATE);
        final int id_place = placeAndSport.getInt("id_place",0);

            //LLAMADA A LA API - TRAE LISTA DE LOS SANCIONADOS
            Call<List<SanctionList>> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .getSanctionList(token,id_place);

            call.enqueue(new Callback<List<SanctionList>>() {
                @Override
                public void onResponse(Call<List<SanctionList>> call, Response<List<SanctionList>> response) {
                    sanctions = (List<SanctionList>) response.body();
                    listViewSanctionList.setAdapter(new SanctionListAdapter(getApplicationContext(),sanctions));
                    progressUserSanctioned.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<List<SanctionList>> call, Throwable t) {
                    Toast.makeText(UserSantionedActivity.this,"error", Toast.LENGTH_LONG).show();
                }
            });

            //FUNCIONES DE CADA ITEM
            listViewSanctionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {


                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(UserSantionedActivity.this);
                    builder.setTitle("Desea sacar la sanción?");

                    builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {

                            SanctionList userData = sanctions.get(position);

                            //LIBERA AL USUARIO DE LA SANCION
                            Call<LoginResponse> callToFreeUser = RetrofitClient
                                    .getInstance()
                                    .getApi()
                                    .userFreeSanction(token,id_place, userData.getId_users());

                            callToFreeUser.enqueue(new Callback<LoginResponse>() {
                                @Override
                                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                    LoginResponse message = response.body();
                                    Toasty.success(UserSantionedActivity.this,message.getMessage(),Toast.LENGTH_SHORT,true).show();
                                }

                                @Override
                                public void onFailure(Call<LoginResponse> call, Throwable t) {
                                    Toast.makeText(UserSantionedActivity.this,"error al liberar sancion", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.userSantioned);

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
                        startActivity(new Intent(getApplicationContext(), TurnsBusyActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.userSantioned:
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
}