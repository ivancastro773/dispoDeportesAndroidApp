package com.example.dispo_deportessr.Admins;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dispo_deportessr.Adapters.TurnRequestedAdapter;
import com.example.dispo_deportessr.R;
import com.example.dispo_deportessr.UserAdminActivity;
import com.example.dispo_deportessr.UserMainActivity;
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

public class TurnRequestedActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private ListView listViewTurnsRequested;
    private List<Court> turns;
    private SharedPreferences tokenAndRole,placeAndSport;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressTurnRequested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_requested);

        listViewTurnsRequested = findViewById(R.id.listViewTurnsRequested);
        progressTurnRequested = findViewById(R.id.progressTurnRequested);
        progressTurnRequested.setVisibility(View.VISIBLE);

        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        final String token = tokenAndRole.getString("token","null");
        placeAndSport = getSharedPreferences("placeAndSport", Context.MODE_PRIVATE);
        final int id_place = placeAndSport.getInt("id_place",0);

            //LLAMADA A LA API - TRAE TURNOS PEDIDOS DEL CHAÑARAL
            Call<List<Court>> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .getAllTurnsBusy(token,id_place,1);

            call.enqueue(new Callback<List<Court>>() {
                @Override
                public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                    turns = (List<Court>) response.body();
                    listViewTurnsRequested.setAdapter(new TurnRequestedAdapter(getApplicationContext(), turns));
                    progressTurnRequested.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<List<Court>> call, Throwable t) {
                    Toasty.warning(TurnRequestedActivity.this,"Error al comunicarse con el servidor",Toast.LENGTH_LONG,true).show();
                }
            });

            //FUNCIONES DE CADA ITEM
            listViewTurnsRequested.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {


                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(TurnRequestedActivity.this);
                    builder.setTitle("Desea cancelar el turno?");

                    builder.setNeutralButton("Sancionar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Court turndata = turns.get(position);

                            //CANCELA EL TURNO
                            Call<LoginResponse> callToSanction = RetrofitClient
                                    .getInstance()
                                    .getApi()
                                    .userSanctioned(token,turndata.getName(),turndata.getId_users(),turndata.getId_place());

                            callToSanction.enqueue(new Callback<LoginResponse>() {
                                @Override
                                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                    LoginResponse message = response.body();
                                    Toasty.success(TurnRequestedActivity.this,message.getMessage(),Toast.LENGTH_SHORT,true).show();
                                }

                                @Override
                                public void onFailure(Call<LoginResponse> call, Throwable t) {
                                    Toast.makeText(TurnRequestedActivity.this,"error al sancionar", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    });

                    builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {

                            Court courtdata = turns.get(position);
                            int id = courtdata.getId();

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
                                    Toasty.success(TurnRequestedActivity.this,"Turno Cancelado!",Toasty.LENGTH_SHORT,true).show();
                                    Log.e("Search","Hay usuarios: "+message.getMessage());
                                }

                                @Override
                                public void onFailure(Call<LoginResponse> call, Throwable t) {
                                    Toast.makeText(TurnRequestedActivity.this, "Error en la llamada", Toast.LENGTH_SHORT).show();
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

            //REFRESH TO LAYOUT
            swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
            int myColor = Color.parseColor("#30E214");
            swipeRefreshLayout.setProgressBackgroundColorSchemeColor(myColor); //color de fondo
            swipeRefreshLayout.setColorSchemeColors(Color.BLUE); //color de la flecha
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    //LLAMADA A LA API - TRAE TODOS LOS TURNOS PEDIDOS DEL CHAÑARAL
                    Call<List<Court>> call = RetrofitClient
                            .getInstance()
                            .getApi()
                            .getAllTurnsBusy(token,id_place,1);

                    call.enqueue(new Callback<List<Court>>() {
                        @Override
                        public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                            turns = (List<Court>) response.body();
                            listViewTurnsRequested.setAdapter(new TurnRequestedAdapter(getApplicationContext(),turns));
                        }

                        @Override
                        public void onFailure(Call<List<Court>> call, Throwable t) {
                            Toast.makeText(TurnRequestedActivity.this,"error", Toast.LENGTH_LONG).show();
                        }
                    });
                    swipeRefreshLayout.setRefreshing(false);
                }
            });


        bottomNavigation = findViewById(R.id.bottomNavigation);
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
}