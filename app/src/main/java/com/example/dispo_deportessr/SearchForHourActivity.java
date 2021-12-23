package com.example.dispo_deportessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import com.example.dispo_deportessr.Adapters.CourtListAdapter;
import com.example.dispo_deportessr.Admins.TurnRequestedActivity;
import com.example.dispo_deportessr.Admins.UserSantionedActivity;
import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.CodeMsjResponse;
import com.example.dispo_deportessr.Models.Court;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchForHourActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigation;
    private ListView listViewTurnsForHour;
    private List<Court> courts;
    private SharedPreferences userData, tokenAndRole;
    private SharedPreferences preferencesTokenIdPhone;
    private Toolbar mTopToolbar;
    private int numberPlace;
    private ProgressBar progressSearchHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_hour);

        progressSearchHour = findViewById(R.id.progressSearchHour);
        progressSearchHour.setVisibility(View.VISIBLE);
        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent receiveHour = getIntent();
        String hour = receiveHour.getStringExtra("hour");

        mTopToolbar.setTitle("Hora: "+hour);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        String role = tokenAndRole.getString("admin","error");
        String token = Util.getUserTokenPrefs(tokenAndRole);

        userData = getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        int id_users = Util.getUserIdPrefs(userData);
        String name = Util.getUserNamePrefs(userData);
        String status = "Ocupado";

        numberPlace = getIntent().getIntExtra("num_id_place",0);

        Toast.makeText(SearchForHourActivity.this, "Hora: "+hour, Toast.LENGTH_SHORT).show();
        getTurnForHour(token,id_users,name,status,hour);

        //BOTONES DE NAVEGACION
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.home);

        //BOTONES DE NAVEGACION
        bottomNavigation = findViewById(R.id.bottomNavigation);
        if (role.equals("admin")){
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

    public void getTurnForHour( final String token,final int id_users, final String name, final String status, final String hour) {
        listViewTurnsForHour = findViewById(R.id.listViewForhour);

        preferencesTokenIdPhone = getSharedPreferences("firebaseToken", Context.MODE_PRIVATE);

        //LLAMADA A LA API - BUSCA LOS TURNOS DEPENDIENDO LA HORA
        Call<List<Court>> call = RetrofitClient
                .getInstance()
                .getApi()
                .getTurnsForHour(token,numberPlace,hour);

        call.enqueue(new Callback<List<Court>>() {
            @Override
            public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                courts = (List<Court>) response.body();
                listViewTurnsForHour.setAdapter(new CourtListAdapter(getApplicationContext(),courts));
                progressSearchHour.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Court>> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(SearchForHourActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
            }
        });

        //REFRESH TO LAYOUT
      /*  swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        int myColor = Color.parseColor("#30E214");
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(myColor); //color de fondo
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE); //color de la flecha
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //LLAMADA A LA API - TRAE LISTA DE TURNOS DEL CHAÑARAL
                Call<List<Court>> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .getTurnsForEntryTime(1,hour);

                call.enqueue(new Callback<List<Court>>() {
                    @Override
                    public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                        courts = (List<Court>) response.body();
                        listViewTurnsForHour.setAdapter(new CourtListAdapter(getApplicationContext(),courts));
                    }

                    @Override
                    public void onFailure(Call<List<Court>> call, Throwable t) {
                        Toast.makeText(SearchForHourActivity.this,"error", Toast.LENGTH_LONG).show();
                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }
        });*/


        //FUNCIONES DE CADA ITEM
        listViewTurnsForHour.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SearchForHourActivity.this);
                builder.setTitle("Desea reservar el turno?");

                builder.setNeutralButton("Lista de espera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Court courtdata = courts.get(position);

                        String tokenIdPhone = Util.getUserTokenIdPhonePrefs(preferencesTokenIdPhone);
                        int id_court = courtdata.getId();

                        //PARSEO until.Date (fecha y hora) a sql.Date(fecha) -> porque sino no puedo guardarlo en la BD
                        java.sql.Date date2 = new java.sql.Date(courtdata.getDate().getTime());

                        //SE REGISTRA EN LA LISTA DE ESPERA
                        Call<CodeMsjResponse> call = RetrofitClient
                                .getInstance()
                                .getApi()
                                .reserveTurnWaitList(token,id_court,id_users,courtdata.getId_place(),date2,courtdata.getCourtNumber(),courtdata.getEntryTime(),courtdata.getDepartureTime(),name,tokenIdPhone);

                        call.enqueue(new Callback<CodeMsjResponse>() {
                            @Override
                            public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                                CodeMsjResponse codeMsjResponse = response.body();
                                if (codeMsjResponse.getCode() == 1){
                                    Toasty.success(SearchForHourActivity.this,codeMsjResponse.getMessage(),Toast.LENGTH_SHORT,true).show();
                                }if (codeMsjResponse.getCode() == 00){
                                    Toasty.warning(SearchForHourActivity.this,codeMsjResponse.getMessage(),Toast.LENGTH_LONG,true).show();
                                }else{
                                    Toasty.info(SearchForHourActivity.this,codeMsjResponse.getMessage(),Toast.LENGTH_LONG,true).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<CodeMsjResponse> call, Throwable t) {
                                String message_fail = getString(R.string.fail_connection_api);
                                Toasty.warning(SearchForHourActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
                            }
                        });

                    }
                });

                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Court courtdata = courts.get(position);

                        int id = courtdata.getId();

                        String date = Util.getDateToString(courtdata.getDate());
                        //RESERVA DEL TURNO
                        Call<CodeMsjResponse> call = RetrofitClient
                                .getInstance()
                                .getApi()
                                .reserveRegister(token,id_users,name,status,date,numberPlace,id,0);

                        call.enqueue(new Callback<CodeMsjResponse>() {
                            @Override
                            public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                                CodeMsjResponse codeMsjResponse = response.body();

                                if (codeMsjResponse.getCode() == 1){
                                    Toast.makeText(SearchForHourActivity.this, codeMsjResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(SearchForHourActivity.this, codeMsjResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<CodeMsjResponse> call, Throwable t) {
                                String message_fail = getString(R.string.fail_connection_api);
                                Toasty.warning(SearchForHourActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
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
    }

}