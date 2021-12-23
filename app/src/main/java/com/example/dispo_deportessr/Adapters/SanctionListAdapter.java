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
import com.example.dispo_deportessr.Models.SanctionList;

import java.text.SimpleDateFormat;
import java.util.List;

public class SanctionListAdapter extends ArrayAdapter<SanctionList> {

    private Context context;
    private List<SanctionList> sanctions;

    public SanctionListAdapter (Context context, List<SanctionList> sanctions){
        super(context, R.layout.row_sanction, sanctions);
        this.context = context;
        this.sanctions = sanctions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.row_sanction, parent, false);

        SanctionList sanctionUser = sanctions.get(position);
        //Parseo la fecha
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
        String dateSaction = formater.format(sanctionUser.getSanctionDay());
        String dateFree = formater.format(sanctionUser.getFreeDay());
        TextView textName = convertView.findViewById(R.id.nameSanction);
        String textNameAdapter = context.getString(R.string.name_adapter);
        textName.setText( textNameAdapter + sanctionUser.getName());

        TextView textEmail = convertView.findViewById(R.id.emailSanction);
        String textEmailAdapter = context.getString(R.string.email_adapter);
        textEmail.setText( textEmailAdapter + sanctionUser.getEmail());

        TextView textViewStatus = convertView.findViewById(R.id.timeToFree);
        textViewStatus.setText("Sancionado: "+dateSaction+"\n Libre: "+dateFree);

        return convertView;
    }
}
