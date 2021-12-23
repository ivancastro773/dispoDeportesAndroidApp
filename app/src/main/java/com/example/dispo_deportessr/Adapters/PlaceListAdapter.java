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

import com.bumptech.glide.Glide;
import com.example.dispo_deportessr.R;
import com.example.dispo_deportessr.Models.Place;

import java.util.List;

public class PlaceListAdapter extends ArrayAdapter<Place> {

    private Context context;
    private List<Place> places;

    public PlaceListAdapter(Context context, List<Place> places){
        super(context, R.layout.row_place, places);
        this.context = context;
        this.places = places;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.row_place, parent, false);

        Place place = places.get(position);
        TextView textView = convertView.findViewById(R.id.namePlace);
        textView.setText(place.getName());
        ImageView imagePlace = convertView.findViewById(R.id.image_place);

        Glide.with(convertView).
                load(place.getImage_url())
                .placeholder(R.drawable.ic_logo_image)
                .into(imagePlace);

        return convertView;
    }
}
