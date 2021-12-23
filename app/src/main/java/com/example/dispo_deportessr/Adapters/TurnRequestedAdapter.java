package com.example.dispo_deportessr.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dispo_deportessr.R;
import com.example.dispo_deportessr.Models.Court;

import java.text.SimpleDateFormat;
import java.util.List;

public class TurnRequestedAdapter  extends ArrayAdapter<Court> {

    private Context context;
    private List<Court> courts;

    public TurnRequestedAdapter(Context context, List<Court> courts){
        super(context, R.layout.row_turn_requested, courts);
        this.context = context;
        this.courts = courts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.row_turn_requested, parent, false);

        Court court = courts.get(position);

        TextView textViewNameUser = convertView.findViewById(R.id.nameUser);
        String textNameAdapter = context.getString(R.string.name_adapter);
        textViewNameUser.setText(textNameAdapter + court.getName());

        //Parseo la fecha
        SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");
        String date = formater.format(court.getDate());

        TextView textViewDate = convertView.findViewById(R.id.date);
        String textDateAdapter = context.getString(R.string.date_adapter);
        textViewDate.setText(textDateAdapter + date);

        TextView textViewCourtNumber = convertView.findViewById(R.id.courtNumber);
        String textNumberCourt = context.getString(R.string.text_number_court);
        textViewCourtNumber.setText(textNumberCourt + String.valueOf(court.getCourtNumber()));

        TextView textViewStatus = convertView.findViewById(R.id.status);
        String textStatus = context.getString(R.string.text_status);
        textViewStatus.setText(textStatus + court.getStatus());

        TextView textViewcourtHour = convertView.findViewById(R.id.courtHour);
        String text_hour = context.getString(R.string.text_hour);
        String texths = context.getString(R.string.hs);
        String a = context.getString(R.string.a);
        textViewcourtHour.setText(text_hour + court.getEntryTime() + texths + a +court.getDepartureTime()+ texths);

        return convertView;
    }
}
