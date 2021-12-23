package com.example.dispo_deportessr;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SelectNumberCourtActivity extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextView;
    private Button button;
    private BottomNavigationView bottomNavigation;
    private Toolbar mTopToolbar;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_court_number);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        autoCompleteTextView = findViewById(R.id.CourtNumberSelected);

        //Numeros de canchas
        String []option = {"1","2","3"};

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.item_court_number,option);
        autoCompleteTextView.setText(arrayAdapter.getItem(0).toString(),false);
        autoCompleteTextView.setAdapter(arrayAdapter);

        button = findViewById(R.id.buttonSendSelected);
        //BOTONES DE NAVEGACION
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.home);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        return true;

                    case R.id.list:
                        startActivity(new Intent(getApplicationContext(),WaitListActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.user:
                        startActivity(new Intent(getApplicationContext(), UserMainActivity.class));
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

    public void searchForNumberCourt(View view) {
        final String courtNumber = autoCompleteTextView.getText().toString().trim();
        int number = Integer.parseInt(courtNumber);

        Intent intent = new Intent(SelectNumberCourtActivity.this, SearchActivity.class);
        intent.putExtra("number",number);
        intent.putExtra("forNumber","1");
        startActivity(intent);
    }
}