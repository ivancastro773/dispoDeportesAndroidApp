package com.example.dispo_deportessr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.CodeMsjResponse;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReserveByAdminActivity extends AppCompatActivity {

    private EditText editTextName;
    private Toolbar mTopToolbar;
    private SharedPreferences tokenAndRole;
    private int id_place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_by_admin);

        editTextName = findViewById(R.id.editTextName);
        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        id_place = getIntent().getIntExtra("id_place",0);
    }

    public void reserveTurn(View view) {
        String name = editTextName.getText().toString().trim();

        Intent receiveIntent = getIntent();
        int id = receiveIntent.getIntExtra("id",0);
        String date = receiveIntent.getStringExtra("date");
        int id_users = 6; //NO IMPORTA EL ID USERS PORQUE EL USUARIO NO TIENE CUENTA
        String busy = "Ocupado";
        //TRAIGO EL TOKEN
        String token = Util.getUserTokenPrefs(tokenAndRole);

            //RESERVA DEL TURNO
            Call<CodeMsjResponse> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .reserveRegister(token,id_users,name,busy,date,id_place,id,0);

            call.enqueue(new Callback<CodeMsjResponse>() {
                @Override
                public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                    CodeMsjResponse codeMsjResponse = response.body();

                    if (codeMsjResponse.getCode() == 1){
                        Toasty.success(ReserveByAdminActivity.this,codeMsjResponse.getMessage(),Toast.LENGTH_SHORT,true).show();
                        Intent intent = new Intent(ReserveByAdminActivity.this, UserMainActivity.class);
                        startActivity(intent);
                    }else{
                        Toasty.warning(ReserveByAdminActivity.this,codeMsjResponse.getMessage(),Toast.LENGTH_SHORT,true).show();
                    }
                }

                @Override
                public void onFailure(Call<CodeMsjResponse> call, Throwable t) {
                    String message_fail = getString(R.string.fail_connection_api);
                    Toasty.warning(ReserveByAdminActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
                }
            });
    }
}