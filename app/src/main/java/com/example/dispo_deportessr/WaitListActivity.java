package com.example.dispo_deportessr;

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

import com.example.dispo_deportessr.Adapters.WaitListAdapter;
import com.example.dispo_deportessr.Models.CodeMsjResponse;
import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.LoginResponse;
import com.example.dispo_deportessr.Models.WaitList;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaitListActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private SharedPreferences userData, tokenAndRole;
    private ListView waitListView;
    private List<WaitList> waitLists;
    private int id_place;
    private ProgressBar progressTurnWaitList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_list);
        waitListView = findViewById(R.id.waitListView);
        progressTurnWaitList = findViewById(R.id.progressTurnWaitList);
        progressTurnWaitList.setVisibility(View.VISIBLE);
        userData = getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        //TRAIGO EL TOKEN
        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        final String token = Util.getUserTokenPrefs(tokenAndRole);
        final int id_users = userData.getInt("id",0);

        //LLAMADA A LA API - TRAE LISTA DE TURNOS DEL USUARIO EN LA LISTA
        getTurnsUserwl(token,id_users);

        //FUNCIONES DE CADA ITEM
        waitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(WaitListActivity.this);
                builder.setTitle("Desea cancelar el turno?");

                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WaitList waitListData = waitLists.get(position);
                        int id = waitListData.getId();
                        id_place = waitListData.getId_place();
                        cancelTurnWl(token,id_users,id);
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
        bottomNavigation.setSelectedItemId(R.id.list);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.list:
                        return true;

                    case R.id.user:
                        startActivity(new Intent(getApplicationContext(), UserActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.turnsUser:
                        startActivity(new Intent(getApplicationContext(),TurnActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    private void getTurnsUserwl(String token, int id_users) {
        Call<List<WaitList>> call = RetrofitClient
                .getInstance()
                .getApi()
                .turnwaitlist(token,id_users);

        call.enqueue(new Callback<List<WaitList>>() {
            @Override
            public void onResponse(Call<List<WaitList>> call, Response<List<WaitList>> response) {
                waitLists = (List<WaitList>) response.body();
                waitListView.setAdapter(new WaitListAdapter(getApplicationContext(),waitLists));
                progressTurnWaitList.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<WaitList>> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(WaitListActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();            }
        });
    }

    private void cancelTurnWl(String token,int id_users,int id) {
        Call<CodeMsjResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .cancelTurnwaitlist(token,id_place,id);

        call.enqueue(new Callback<CodeMsjResponse>() {
            @Override
            public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                CodeMsjResponse message = response.body();
                Toasty.success(WaitListActivity.this,message.getMessage(),Toast.LENGTH_SHORT,true).show();
                refreshList(token,id_users);
            }

            @Override
            public void onFailure(Call<CodeMsjResponse> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(WaitListActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
            }
        });
    }

    private void refreshList(String token,int id_users){
        //LLAMADA A LA API - TRAE LISTA DE TURNOS DEL USUARIO EN LA LISTA
        Call<List<WaitList>> call = RetrofitClient
                .getInstance()
                .getApi()
                .turnwaitlist(token,id_users);

        call.enqueue(new Callback<List<WaitList>>() {
            @Override
            public void onResponse(Call<List<WaitList>> call, Response<List<WaitList>> response) {
                waitLists = (List<WaitList>) response.body();
                waitListView.setAdapter(new WaitListAdapter(getApplicationContext(),waitLists));
                progressTurnWaitList.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<WaitList>> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(WaitListActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
            }
        });
    }
}