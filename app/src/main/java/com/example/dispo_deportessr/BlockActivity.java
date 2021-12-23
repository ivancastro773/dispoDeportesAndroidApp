package com.example.dispo_deportessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dispo_deportessr.Adapters.BlockCourtAdapter;
import com.example.dispo_deportessr.Admins.TurnRequestedActivity;
import com.example.dispo_deportessr.Admins.UserSantionedActivity;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.Court;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private Toolbar mTopToolbar;
    private ListView listViewBlockCourt;
    private List<Court> blockCourts;
    private int id_sports,id_place;
    private ProgressBar progressBlock;
    private SharedPreferences sharedSwith1,sharedSwith2,sharedSwith3, tokenAndRole;
    private SharedPreferences.Editor editorSwitch1,editorSwitch2,editorSwitch3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewBlockCourt = (ListView) findViewById(R.id.listViewBlockCourt);
        progressBlock = findViewById(R.id.progressBlock);
        progressBlock.setVisibility(View.VISIBLE);
        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        final String token = tokenAndRole.getString("token","null");
        id_place = getIntent().getIntExtra("num_id_place",0);
        id_sports = getIntent().getIntExtra("num_id_sports",0);

        getListBlockCourt(token);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.turns);

        //BOTONES DE NAVEGACION
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

    }

    private void getListBlockCourt(String token) {
        Call<List<Court>> call = RetrofitClient
                .getInstance()
                .getApi()
                .getListBlockCourt(token,id_place);

        call.enqueue(new Callback<List<Court>>() {
            @Override
            public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                blockCourts = (List<Court>) response.body();
                listViewBlockCourt.setAdapter(new BlockCourtAdapter(getApplicationContext(),blockCourts,token,id_sports,id_place));
                progressBlock.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Court>> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(BlockActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
            }
        });
    }
}