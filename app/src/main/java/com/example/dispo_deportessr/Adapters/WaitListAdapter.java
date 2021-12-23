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
import com.example.dispo_deportessr.Models.Place;
import com.example.dispo_deportessr.Models.WaitList;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaitListAdapter extends ArrayAdapter<WaitList> {

    private Context context;
    private List<WaitList> waitLists;
    private SharedPreferences tokenAndRole;
    private String token;

    public WaitListAdapter(Context context, List<WaitList> waitLists){
        super(context,R.layout.row_waitlist,waitLists);
        this.context = context;
        this.waitLists = waitLists;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.row_waitlist,parent,false);
        tokenAndRole = context.getSharedPreferences("tokenAndRole",Context.MODE_PRIVATE);
        token = Util.getUserTokenPrefs(tokenAndRole);
        WaitList waitList = waitLists.get(position);

        TextView textViewName = convertView.findViewById(R.id.nameWaitList);
        String textNameAdapter = context.getString(R.string.name_adapter);
        textViewName.setText(textNameAdapter + waitList.getName());

        TextView textViewPlace = convertView.findViewById(R.id.placeWaitList);
        String textPlaceAdapter = context.getString(R.string.place_adapter);
        Call<Place> callGetName = RetrofitClient
                .getInstance()
                .getApi()
                .getNamePlaceId(token,waitList.getId_place());
        callGetName.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                Place place = response.body();
                textViewPlace.setText(textPlaceAdapter + place.getName());
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {

            }
        });

        //Parseo la fecha
        SimpleDateFormat formater = new SimpleDateFormat("EEEE dd MMMM yyyy");
        String date = formater.format(waitList.getDate());


        TextView textViewDate = convertView.findViewById(R.id.dateWaitList);
        textViewDate.setText(date);

        TextView textViewHour = convertView.findViewById(R.id.hourWaitList);
        String text_hour = context.getString(R.string.text_hour);
        String texths = context.getString(R.string.hs);
        String a = context.getString(R.string.a);
        textViewHour.setText(text_hour + waitList.getEntryTime() + texths + a + waitList.getDepartureTime() + texths);

        TextView textViewNumberCourt = convertView.findViewById(R.id.numberCourtWaitList);
        String textNumberCourt = context.getString(R.string.text_number_court);
        textViewNumberCourt.setText(textNumberCourt + waitList.getCourtNumber());

        return convertView;
    }
}
