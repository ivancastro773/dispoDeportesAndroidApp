package com.example.dispo_deportessr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.CodeMsjResponse;
import com.example.dispo_deportessr.Models.LoginResponse;
import com.example.dispo_deportessr.Models.Place;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TurnWaitListActivity extends AppCompatActivity {
    private TextView placeTurnWaitList;
    private TextView dateTurnWaitList;
    private TextView hourTurnWaitList;
    private TextView courtNumberTurnWaitList;
    private MaterialButton yesButton, noButton;
    private SharedPreferences DataTurn;
    private SharedPreferences.Editor DataTurnEditor;
    private SharedPreferences userData;
    private SharedPreferences tokenAndRole;
    private int id_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_wait_list);
        //DATOS DEL TURNO QUE SE CANCELO
        String id_place = "";
        String datedata = "";
        String entryTime = "";
        String departureTime = "";
        String courtNumberdata = "";
        String id_court = "";
        String id_waitlist = "";

        //DATOS DEL TURNO QUE SE CANCELO
        DataTurn = getSharedPreferences("DataTurn", Context.MODE_PRIVATE);
        DataTurnEditor = DataTurn.edit();
        //get notification data info
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id_place = bundle.getString("id_place");
            datedata = bundle.getString("date");
            entryTime = bundle.getString("entryTime");
            departureTime = bundle.getString("departureTime");
            courtNumberdata = bundle.getString("courtNumber");
            id_court = bundle.getString("id_court");
            id_waitlist = bundle.getString("id_waitlist");
        } else {
            id_place = DataTurn.getString("id_place", "error");
            datedata = DataTurn.getString("date", "error");
            entryTime = DataTurn.getString("entryTime", "error");
            departureTime = DataTurn.getString("departureTime", "error");
            courtNumberdata = DataTurn.getString("courtNumber", "error");
            id_court = DataTurn.getString("id_court", "error");
            id_waitlist = DataTurn.getString("id_waitlist", "error");
        }

        //Borro la notificacion cuando presionan en la notificacion
        int idnoti = getIntent().getIntExtra("idnotification", 0);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.cancel(idnoti);

        placeTurnWaitList = findViewById(R.id.placeTurnWaitList);
        dateTurnWaitList = findViewById(R.id.dateTurnWaitList);
        hourTurnWaitList = findViewById(R.id.hourTurnWaitList);
        courtNumberTurnWaitList = findViewById(R.id.courtNumberTurnWaitList);
        yesButton = findViewById(R.id.yesTurnWaitList);
        noButton = findViewById(R.id.noTurnWaitList);

        //TRAIGO EL TOKEN
        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        final String token = Util.getUserTokenPrefs(tokenAndRole);

        //PARSEO LOS STRING A INT
        final int place = Integer.parseInt(id_place);
        final int courtNumber = Integer.parseInt(courtNumberdata);
        final int idCourt = Integer.parseInt(id_court);
        final int idWaitList = Integer.parseInt(id_waitlist);

        final String date = datedata;
        final Date datesend = parseDate(date);
        //COMIENZA EL CRONOMETRO (5 MIN)
        String textPlaceAdapter = getApplicationContext().getString(R.string.place_adapter);
        Call<Place> callGetName = RetrofitClient
                .getInstance()
                .getApi()
                .getNamePlaceId(token, place);
        callGetName.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                Place place = response.body();
                placeTurnWaitList.setText(textPlaceAdapter + place.getName());
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {

            }
        });

        dateTurnWaitList.setText("Fecha: " + date);
        String text_hour = getApplicationContext().getString(R.string.text_hour);
        String texths = getApplicationContext().getString(R.string.hs);
        String a = getApplicationContext().getString(R.string.a);
        hourTurnWaitList.setText(text_hour + entryTime + texths + a +departureTime + texths);
        String textNumberCourt = getApplicationContext().getString(R.string.text_number_court);
        courtNumberTurnWaitList.setText(textNumberCourt + courtNumber);

        //FUNCIONES PARA LOS DOS BOTONES
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userData = getSharedPreferences("dataUser", Context.MODE_PRIVATE);
                id_users = Util.getUserIdPrefs(userData);
                String name = Util.getUserNamePrefs(userData);
                String status = "Ocupado";

                //RESERVA DEL TURNO
                Call<CodeMsjResponse> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .reserveRegister(token, id_users, name, status,date, place, idCourt, 1);

                call.enqueue(new Callback<CodeMsjResponse>() {
                    @Override
                    public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                        CodeMsjResponse codeMsjResponse = response.body();

                        if (codeMsjResponse.getCode() == 1) {
                            Toasty.success(TurnWaitListActivity.this, codeMsjResponse.getMessage(), Toast.LENGTH_SHORT, true).show();
                            stopTime();
                        } else {
                            Toasty.warning(TurnWaitListActivity.this, codeMsjResponse.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CodeMsjResponse> call, Throwable t) {
                        String message_fail = getString(R.string.fail_connection_api);
                        Toasty.warning(TurnWaitListActivity.this, message_fail, Toast.LENGTH_SHORT, true).show();
                    }
                });

                //LLAMAR A LA API PARA BORRAR AL USUARIO DE LA LISTA DE ESPERA YA QUE RESERVO.
                Call<CodeMsjResponse> call2 = RetrofitClient
                        .getInstance()
                        .getApi()
                        .cancelTurnwaitlist(token, place, idWaitList);
                call2.enqueue(new Callback<CodeMsjResponse>() {
                    @Override
                    public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                        CodeMsjResponse message = response.body();
                        Intent intent = new Intent(TurnWaitListActivity.this, TurnActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<CodeMsjResponse> call, Throwable t) {
                        String message_fail = getString(R.string.fail_connection_api);
                        Toasty.warning(TurnWaitListActivity.this, message_fail, Toast.LENGTH_SHORT, true).show();
                    }
                });

                DataTurnEditor.clear();
                DataTurnEditor.commit();
                finish();
            }
        });

        final String finalEntryTime = entryTime;
        final String finalDepartureTime = departureTime;
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //LLAMAR A LA API PARA BORRAR AL USUARIO DE LA LISTA
                Call<CodeMsjResponse> callCancel = RetrofitClient
                        .getInstance()
                        .getApi()
                        .cancelTurnwaitlist(token, place, idWaitList);

                callCancel.enqueue(new Callback<CodeMsjResponse>() {
                    @Override
                    public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                        CodeMsjResponse codeMsjResponse = response.body();
                        if (codeMsjResponse.getCode() == 1) {
                            Toast.makeText(TurnWaitListActivity.this, codeMsjResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            stopTime();

                            //BUSCAR AL SIGUIENTE DE LA LISTA SI ES QUE LO HAY
                            Call<LoginResponse> callSearchNext = RetrofitClient
                                    .getInstance()
                                    .getApi()
                                    .searchwaitlist(place, date, finalEntryTime, finalDepartureTime, courtNumber, idCourt);

                            callSearchNext.enqueue(new Callback<LoginResponse>() {
                                @Override
                                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                    LoginResponse message = response.body();
                                    Log.e("Search", "Hay usuarios: " + message.getMessage());
                                }

                                @Override
                                public void onFailure(Call<LoginResponse> call, Throwable t) {
                                    String message_fail = getString(R.string.fail_connection_api);
                                    Toasty.warning(TurnWaitListActivity.this, message_fail, Toast.LENGTH_SHORT, true).show();
                                }
                            });
                            Intent intent = new Intent(TurnWaitListActivity.this, WaitListActivity.class);
                            startActivity(intent);
                        }else{
                            Toasty.warning(TurnWaitListActivity.this, codeMsjResponse.getMessage(), Toast.LENGTH_LONG, true).show();
                            Intent intent = new Intent(TurnWaitListActivity.this, WaitListActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<CodeMsjResponse> call, Throwable t) {
                        String message_fail = getString(R.string.fail_connection_api);
                        Toasty.warning(TurnWaitListActivity.this, message_fail, Toast.LENGTH_SHORT, true).show();
                    }
                });

                DataTurnEditor.clear();
                DataTurnEditor.commit();
                finish();
            }
        });
    }

    private void stopTime() {
        Call<LoginResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .expiredTimeStop();

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse message = response.body();
                Log.e("Chonometer", "Cronometro: " + message.getMessage());
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(TurnWaitListActivity.this, "error 1", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public java.sql.Date convertJavaDateToSqlDate(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static Date parseDate(String dateselected) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(dateselected);
        } catch (ParseException ex) {
            System.out.println(ex);
        }
        return date;
    }


}