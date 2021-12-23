package com.example.dispo_deportessr.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.dispo_deportessr.Adapters.SportsListAdapter;
import com.example.dispo_deportessr.Admins.TurnsBusyActivity;
import com.example.dispo_deportessr.Admins.UserSantionedActivity;
import com.example.dispo_deportessr.MainActivity;
import com.example.dispo_deportessr.Models.Sports;
import com.example.dispo_deportessr.R;
import com.example.dispo_deportessr.TokenInvalidActivity;
import com.example.dispo_deportessr.UserMainActivity;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Util {

    public static String getUserTokenPrefs(SharedPreferences preferences){
        return preferences.getString("token","error");
    }
    public static String getUserRolePrefs(SharedPreferences preferences){
        return preferences.getString("role","error");
    }
    public static String getDatePrefs(SharedPreferences preferences){
        return preferences.getString("date","error");
    }
    public static int getForDatePrefs(SharedPreferences preferences){
        return preferences.getInt("forDate",0);
    }
    public static String getUserTokenIdPhonePrefs(SharedPreferences preferences){
        return preferences.getString("tokenIdPhone","error");
    }
    public static int getIdSportsPrefs(SharedPreferences preferences){
        return preferences.getInt("id_sports",0);
    }
    public static int getIdPlacePrefs(SharedPreferences preferences){
        return preferences.getInt("id_place",0);
    }
    public static int getUserIdPrefs(SharedPreferences preferences){
        return preferences.getInt("id",0);
    }
    public static String getUserEmailPrefs(SharedPreferences preferences){
        return preferences.getString("email","error");
    }
    public static String getUserNamePrefs(SharedPreferences preferences){
        return preferences.getString("name","empty");
    }
    public static String getDateToString(Date date){
        //Parseo la fecha
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formater.format(date);
        return dateString;
    }

}
