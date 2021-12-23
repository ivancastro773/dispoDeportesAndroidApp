package com.example.dispo_deportessr.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dispo_deportessr.R;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.CodeMsjResponse;
import com.example.dispo_deportessr.Models.Court;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockCourtAdapter extends ArrayAdapter<Court> {

    private Context context;
    private List<Court> blockCourts;
    private String token;
    private List<Court> statusCourt;
    private Integer id_sports,id_place;

    public BlockCourtAdapter(Context context, List<Court> blockCourts, String token, Integer id_sports, Integer id_place ){
        super(context, R.layout.row_bloqued_court, blockCourts);
        this.context = context;
        this.blockCourts = blockCourts;
        this.token = token;
        this.id_sports = id_sports;
        this.id_place = id_place;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.row_bloqued_court, parent, false);

        Court blockCourt = blockCourts.get(position);

        Switch switchCourt = convertView.findViewById(R.id.switchCourt);
        String textCourtNumber = context.getString(R.string.court_number);
        switchCourt.setText(textCourtNumber+ blockCourt.getCourtNumber());

        Call<List<Court>> callStatusCourt = RetrofitClient
                .getInstance()
                .getApi()
                .getStatusCourt(token,id_sports,id_place);
        callStatusCourt.enqueue(new Callback<List<Court>>() {
            @Override
            public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                statusCourt = (List<Court>) response.body();

                for (int i = 0; i < statusCourt.size(); i++){

                    if(statusCourt.get(i).getCourtNumber() == blockCourt.getCourtNumber()){
                        switchCourt.setChecked(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Court>> call, Throwable t) {

            }
        });

        switchCourt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchCourt.isChecked()){

                    Call<CodeMsjResponse> callToBlock = RetrofitClient
                        .getInstance()
                        .getApi()
                        .blockCourt(token,id_sports,id_place,blockCourt.getCourtNumber());

                    callToBlock.enqueue(new Callback<CodeMsjResponse>() {
                        @Override
                        public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                            CodeMsjResponse message = response.body();
                            Toasty.success(context,"Cancha "+blockCourt.getCourtNumber()+ message.getMessage(),Toast.LENGTH_SHORT,true).show();
                        }

                        @Override
                        public void onFailure(Call<CodeMsjResponse> call, Throwable t) {
                            Toast.makeText(context, "Error al bloquear", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{

                    Call<CodeMsjResponse> callToFree = RetrofitClient
                            .getInstance()
                            .getApi()
                            .freeCourt(token,id_sports,id_place,blockCourt.getCourtNumber());

                    callToFree.enqueue(new Callback<CodeMsjResponse>() {
                        @Override
                        public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                            CodeMsjResponse message = response.body();
                            Toasty.success(context,"Cancha "+blockCourt.getCourtNumber()+ message.getMessage(),Toast.LENGTH_SHORT,true).show();
                        }

                        @Override
                        public void onFailure(Call<CodeMsjResponse> call, Throwable t) {
                            Toast.makeText(context, "Error al desbloquear", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        return convertView;
    }
}
