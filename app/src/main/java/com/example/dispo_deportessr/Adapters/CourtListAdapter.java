package com.example.dispo_deportessr.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.dispo_deportessr.R;
import com.example.dispo_deportessr.Models.Court;

import java.text.SimpleDateFormat;
import java.util.List;

public class CourtListAdapter extends ArrayAdapter<Court> {

    private Context context;
    private List<Court> courts;

    public CourtListAdapter(Context context, List<Court> courts){
        super(context, R.layout.row_court, courts);
        this.context = context;
        this.courts = courts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.row_court, parent, false);

        Court court = courts.get(position);

        //Parseo la fecha
        SimpleDateFormat formater = new SimpleDateFormat("EEEE dd MMMM");
        String date = formater.format(court.getDate());

        TextView textViewDate = convertView.findViewById(R.id.date);
        textViewDate.setText(date);

        TextView textViewCourtNumber = convertView.findViewById(R.id.courtNumber);
        String textNumberCourt = context.getString(R.string.text_number_court);
        textViewCourtNumber.setText( textNumberCourt + String.valueOf(court.getCourtNumber()));

       /* TextView textViewStatus = convertView.findViewById(R.id.status);
        String textStatus = context.getString(R.string.text_status);
        textViewStatus.setText( textStatus +" "+ court.getStatus());*/

        TextView textViewcourtHour = convertView.findViewById(R.id.courtHour);
        String text_hour = context.getString(R.string.text_hour);
        String texths = context.getString(R.string.hs);
        String a = context.getString(R.string.a);
        textViewcourtHour.setText( court.getEntryTime() + " " + a + " "+court.getDepartureTime() + texths);

        CardView cardView = convertView.findViewById(R.id.cardViewCourt);

        String busy = "Ocupado";
        if (busy.equals(court.getStatus())){
            cardView.setCardBackgroundColor(Color.rgb(243, 45, 42));
            textViewcourtHour.setTextColor(Color.WHITE);
            textViewCourtNumber.setTextColor(Color.WHITE);
            textViewDate.setTextColor(Color.WHITE);
//            textViewStatus.setTextColor(Color.WHITE);
        }
        return convertView;
    }

}
