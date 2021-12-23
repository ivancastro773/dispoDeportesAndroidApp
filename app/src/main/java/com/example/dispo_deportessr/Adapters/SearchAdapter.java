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
import com.example.dispo_deportessr.Models.Search;

import java.util.List;

public class SearchAdapter extends ArrayAdapter<Search> {

    private Context context;
    private List<Search> options;

    public SearchAdapter(Context context, List<Search> options){
        super(context, R.layout.row_place, options);
        this.context = context;
        this.options = options;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.row_place, parent, false);

        Search option = options.get(position);
        TextView textView = convertView.findViewById(R.id.namePlace);
        textView.setText(option.getName());

        return convertView;
    }
}
