package com.example.dispo_deportessr.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dispo_deportessr.R;
import com.example.dispo_deportessr.Models.Sports;

import java.util.List;

public class SportsListAdapter extends ArrayAdapter<Sports> {


    private Context context;
    private List<Sports> sports;

    public SportsListAdapter(Context context, List<Sports> sports){
        super(context, R.layout.row_sports, sports);
        this.context = context;
        this.sports = sports;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.row_sports, parent, false);

        Sports sports1 = sports.get(position);

        ImageView imageView = convertView.findViewById(R.id.image_sport);
        if (sports1.getId() == 1){
            imageView.setImageResource(R.drawable.ic_soccer_ball);
        } if (sports1.getId() == 2){
            imageView.setImageResource(R.drawable.ic_tennis);
        }if (sports1.getId() == 3){
            imageView.setImageResource(R.drawable.ic_tennis);
        }
        TextView nameSport = (TextView) convertView.findViewById(R.id.nameSport);
        nameSport.setText(sports1.getName());


        return convertView;
    }
}
