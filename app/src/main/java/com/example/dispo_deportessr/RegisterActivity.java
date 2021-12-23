package com.example.dispo_deportessr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dispo_deportessr.Models.LoginResponse;
import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.CodeMsjResponse;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextName, editTextEmail, editTextPassword1, editTextPassword2;
    //Preferences para id de dispositivo
    private SharedPreferences tokenIdPhone;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        if (width <= 480 && height <=800){
            setContentView(R.layout.activity_register_small);
        }if (width >= 720 && height >= 1280){
            setContentView(R.layout.activity_register);
        }
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword1 = findViewById(R.id.editTextPassword1);
        editTextPassword2 = findViewById(R.id.editTextPassword2);
        progressBar = findViewById(R.id.progressBarRegister);
        progressBar.setVisibility(View.GONE);
        findViewById(R.id.buttonRegister).setOnClickListener(this);

        //LLAMO A PREFERENCES PARA ACCEDER AL TOKEN DE DISPOSITIVO
        tokenIdPhone = getSharedPreferences("firebaseToken", Context.MODE_PRIVATE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonRegister:
                userRegister();
                break;

        }
    }

    //FUNCION PARA REGISTRAR UN USUARIO NUEVO
    private void userRegister() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword1.getText().toString().trim();
        String passwordConfirm = editTextPassword2.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        //TRAIGO EL TOKEN DE DISPOSITIVO
        String tokenIdPhone1 = Util.getUserTokenIdPhonePrefs(tokenIdPhone);

        Call<LoginResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .register(name,email,password,passwordConfirm,tokenIdPhone1);

       call.enqueue(new Callback<LoginResponse>() {
           @Override
           public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
               LoginResponse codeMsjResponse = response.body();

               if(response.isSuccessful()){
                   Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                   Toasty.success(RegisterActivity.this, codeMsjResponse.getMessage(), Toast.LENGTH_LONG).show();
                   startActivity(intent);
                   progressBar.setVisibility(View.GONE);
               }else {
                   Toasty.warning(RegisterActivity.this, codeMsjResponse.getMessage(), Toast.LENGTH_LONG).show();
               }
           }

           @Override
           public void onFailure(Call<LoginResponse> call, Throwable t) {
               String message_fail = getString(R.string.fail_connection_api);
               Toasty.warning(RegisterActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
           }
       });

       //TOKEN DEL DISPOSITIVO
        Log.e("TokenIdPhone","MI TOKENIDPHONE DE APP ES: "+tokenIdPhone1);
    }
}