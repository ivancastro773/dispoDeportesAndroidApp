package com.example.dispo_deportessr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import es.dmoral.toasty.Toasty;

public class InProcessActivity extends AppCompatActivity {

    private TextView textMessage;
    private Toolbar mTopToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_process);
        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textMessage = findViewById(R.id.textView3);
    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(InProcessActivity.this, MainActivity.class);
        startActivity(intent);
        return false;
    }
}