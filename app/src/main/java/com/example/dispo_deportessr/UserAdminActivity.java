package com.example.dispo_deportessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispo_deportessr.Admins.EditInfoPlaceActivity;
import com.example.dispo_deportessr.Admins.TurnsBusyActivity;
import com.example.dispo_deportessr.Admins.UserSantionedActivity;
import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.CodeMsjResponse;
import com.example.dispo_deportessr.Models.Place;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdminActivity extends AppCompatActivity {

    private TextView profileName, profileEmail;
    private BottomNavigationView bottomNavigation;
    private MaterialButton finishSession;
    private SharedPreferences tokenAndRole, dataUser, switchHolidays;
    private SharedPreferences.Editor tokenAndRoleEditor, dataUserEditor, editorHolidays;
    private SwitchCompat switchHoly;
    private MaterialButton buttonChangePassword;
    private Button buttonEditInfoPlace;
    private Button buttonViewInfoPlace;
    private FloatingActionButton fabEdit, fabInfo, fabAddCourt;
    public static int id_place = 0;
    public static String namePlace = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        //LLAMO A PREFERENCES PARA ACCEDER AL TOKEN
        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        tokenAndRoleEditor = tokenAndRole.edit();
        final String token = Util.getUserTokenPrefs(tokenAndRole);
        String role = Util.getUserRolePrefs(tokenAndRole);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileMail);
        finishSession = findViewById(R.id.finishSession);
        switchHoly = findViewById(R.id.switchHoly);
        fabEdit = findViewById(R.id.fabEdit);
        fabInfo = findViewById(R.id.fabInfo);
        fabAddCourt = findViewById(R.id.fabAddCourt);
        buttonChangePassword = findViewById(R.id.changePasswordAdmin);

        //CREO SHARED PREFERENCES PARA GUARDAR LOS DATOS DE PERFIL DEL USUARIO
        dataUser = getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        dataUserEditor = dataUser.edit();

        profileName.setText(Util.getUserNamePrefs(dataUser));
        profileEmail.setText(Util.getUserEmailPrefs(dataUser));
        int id_users = Util.getUserIdPrefs(dataUser);

        Call<Place> call = RetrofitClient
                .getInstance()
                .getApi()
                .getPlaceByUser(token, id_users);

        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                Place place = response.body();
                id_place = place.getId();
                namePlace = place.getName();
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(UserAdminActivity.this, message_fail, Toast.LENGTH_SHORT, true).show();
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://dispo-deportes-sr.herokuapp.com/auth/form"));
                startActivity(intent);
            }
        });

        switchHolidays = getSharedPreferences("switchHoly", Context.MODE_PRIVATE);
        editorHolidays = switchHolidays.edit();
        switchHoly.setChecked(switchHolidays.getBoolean("value", false));

        switchHoly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchHoly.isChecked()) {
                    editorHolidays.putBoolean("value", true);
                    editorHolidays.commit();
                    switchHoly.setChecked(true);
                    modeHolidaysOn(token);
                } else {
                    editorHolidays.putBoolean("value", false);
                    editorHolidays.commit();
                    switchHoly.setChecked(false);
                    modeHolidaysOff(token);
                }
            }
        });

        fabInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserAdminActivity.this, InfoPlaceActivity.class);
                intent.putExtra("num_id_place", id_place);
                startActivity(intent);
            }
        });

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserAdminActivity.this, EditInfoPlaceActivity.class);
                intent.putExtra("num_id_place", id_place);
                startActivity(intent);
            }
        });

        fabAddCourt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserAdminActivity.this, SelectCantCourtsActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.admin);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.turns:
                        startActivity(new Intent(getApplicationContext(), UserMainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.turnsRequested:
                        startActivity(new Intent(getApplicationContext(), TurnsBusyActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.userSantioned:
                        startActivity(new Intent(getApplicationContext(), UserSantionedActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.admin:
                        return true;
                }
                return false;
            }
        });
    }

    private void modeHolidaysOff(String token) {
        Call<CodeMsjResponse> callFreeHoly = RetrofitClient
                .getInstance()
                .getApi()
                .freeHolidays(token, 0, id_place);

        callFreeHoly.enqueue(new Callback<CodeMsjResponse>() {
            @Override
            public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                CodeMsjResponse message = response.body();

                Toasty.success(UserAdminActivity.this, message.getMessage(), Toast.LENGTH_SHORT, true).show();
            }

            @Override
            public void onFailure(Call<CodeMsjResponse> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(UserAdminActivity.this, message_fail, Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void modeHolidaysOn(String token) {
        Call<CodeMsjResponse> callModeHoly = RetrofitClient
                .getInstance()
                .getApi()
                .modeHolidays(token, 1, id_place);

        callModeHoly.enqueue(new Callback<CodeMsjResponse>() {
            @Override
            public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                CodeMsjResponse message = response.body();
                Toasty.success(UserAdminActivity.this, message.getMessage(), Toast.LENGTH_SHORT, true).show();
            }

            @Override
            public void onFailure(Call<CodeMsjResponse> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(UserAdminActivity.this, message_fail, Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    public void finishSession(View view) {
        tokenAndRoleEditor.clear();
        tokenAndRoleEditor.commit();
        dataUserEditor.clear();
        dataUserEditor.commit();
        Intent intent = new Intent(UserAdminActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}