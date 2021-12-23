package com.example.dispo_deportessr.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dispo_deportessr.R;
import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.Court;
import com.example.dispo_deportessr.Models.Place;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TurnsUserListAdapter extends ArrayAdapter<Court> {

    private Context context;
    private List<Court> turns;
    private SharedPreferences tokenAndRole;
    private String token;
    private String namePlace;


    public TurnsUserListAdapter(Context context, List<Court> turns){
        super(context, R.layout.row_turnuser, turns);
        this.context = context;
        this.turns = turns;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.row_turnuser, parent, false);
        tokenAndRole = context.getSharedPreferences("tokenAndRole",Context.MODE_PRIVATE);
        token = Util.getUserTokenPrefs(tokenAndRole);

        Court turn = turns.get(position);

        Date today = turn.getDate();
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd MMMM");//formating according to my need
        String date = formatter.format(today);

        TextView textViewDate = convertView.findViewById(R.id.date);
        textViewDate.setText(date);

        TextView textViewNamePlace = convertView.findViewById(R.id.namePlace);
        String textPlaceAdapter = context.getString(R.string.place_adapter);
        Call<Place> callGetName = RetrofitClient
                .getInstance()
                .getApi()
                .getNamePlaceId(token,turn.getId_place());
        callGetName.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                Place place = response.body();
                textViewNamePlace.setText(place.getName());
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {

            }
        });

        TextView textViewCourtNumber = convertView.findViewById(R.id.courtNumber);
        String textNumberCourt = context.getString(R.string.text_number_court);
        textViewCourtNumber.setText(textNumberCourt + String.valueOf(turn.getCourtNumber()));

        TextView textViewHour = convertView.findViewById(R.id.hour);
        String text_hour = context.getString(R.string.text_hour);
        String texths = context.getString(R.string.hs);
        String a = context.getString(R.string.a);
        textViewHour.setText(turn.getEntryTime() + " "+ a + " "+ turn.getDepartureTime() + texths);

        return convertView;
    }
}
