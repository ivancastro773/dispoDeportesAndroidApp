package com.example.dispo_deportessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dispo_deportessr.Adapters.TurnsUserListAdapter;
import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.Court;
import com.example.dispo_deportessr.Models.LoginResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TurnActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private ListView listViewTurnsUser;
    private List<Court> turns;
    private SharedPreferences userData, tokenAndRole,saveActivity;
    private ProgressBar progressTurnUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn);

        listViewTurnsUser = findViewById(R.id.listViewTurnsUser);
        progressTurnUser = findViewById(R.id.progressTurnUser);
        progressTurnUser.setVisibility(View.VISIBLE);
        userData = getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        //TRAIGO EL TOKEN
        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        final String token = Util.getUserTokenPrefs(tokenAndRole);

        //TRAIGO EL ID DEL USUARIO
        final int id_users = Util.getUserIdPrefs(userData);

        //LLAMADA A LA API - TRAE LISTA DE TURNOS DEL USUARIO
        final Call<List<Court>> call = RetrofitClient
                .getInstance()
                .getApi()
                .getTurnsUser(token,id_users);

        call.enqueue(new Callback<List<Court>>() {
            @Override
            public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                turns = (List<Court>) response.body();
                listViewTurnsUser.setAdapter(new TurnsUserListAdapter(getApplicationContext(),turns));
                progressTurnUser.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Court>> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(TurnActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
            }
        });

        //FUNCIONES DE CADA ITEM
        listViewTurnsUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {


                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(TurnActivity.this);
                builder.setTitle("Desea cancelar el turno?");

                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {

                        Court courtdata = turns.get(position);

                        final int id = courtdata.getId();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                        String fechaComoCadena = sdf.format(courtdata.getDate());

                        //BUSCA SI HAY ALGUIEN EN LA LISTA Y ENVIA LA NOTIFICACION EN CASO DE QUE SI
                        Call<LoginResponse> call2 = RetrofitClient
                                .getInstance()
                                .getApi()
                                .searchwaitlist(courtdata.getId_place(),fechaComoCadena,courtdata.getEntryTime(),courtdata.getDepartureTime(),
                                        courtdata.getCourtNumber(),id);

                        call2.enqueue(new Callback<LoginResponse>() {
                            @Override
                            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                LoginResponse message = response.body();
                                Toasty.success(TurnActivity.this,"Turno Cancelado!",Toasty.LENGTH_SHORT,true).show();
                                Log.e("Search","Hay usuarios: "+message.getMessage());
                                refreshListTurnUser(token,id_users);
                            }

                            @Override
                            public void onFailure(Call<LoginResponse> call, Throwable t) {
                                String message_fail = getString(R.string.fail_connection_api);
                                Toasty.warning(TurnActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
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

        //BOTONES DE NAVEGACION
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.turnsUser);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                saveActivity = getSharedPreferences("activityPlace", Context.MODE_PRIVATE);
                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
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
                        return true;
                }
                return false;
            }
        });
    }
    private void refreshListTurnUser(String token,int id_users){
        //LLAMADA A LA API - TRAE LISTA DE TURNOS DEL USUARIO
        Call<List<Court>> call = RetrofitClient
                .getInstance()
                .getApi()
                .getTurnsUser(token,id_users);

        call.enqueue(new Callback<List<Court>>() {
            @Override
            public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                turns = (List<Court>) response.body();
                listViewTurnsUser.setAdapter(new TurnsUserListAdapter(getApplicationContext(),turns));
                progressTurnUser.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Court>> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(TurnActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
            }
        });
    }
}