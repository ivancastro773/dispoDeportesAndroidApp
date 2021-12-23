package com.example.dispo_deportessr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dispo_deportessr.Adapters.TurnRequestedAdapter;
import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.Court;
import com.example.dispo_deportessr.Models.LoginResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TurnsBusyFragment extends ListFragment {

    private int id_place,courtNumber,id_users;
    private List<Court> courts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences userData;
    private SharedPreferences tokenAndRole,placeAndSport;
    private TurnRequestedAdapter adapter;
    private String token;
    private ProgressBar progressBar;
    private Context context;

    public TurnsBusyFragment(int id_place, int courtNumber) {
        this.id_place = id_place;
        this.courtNumber = courtNumber;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tokenAndRole = getActivity().getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        token = Util.getUserTokenPrefs(tokenAndRole);
        placeAndSport = getActivity().getSharedPreferences("placeAndSport",Context.MODE_PRIVATE);
        int id_sports = Util.getIdSportsPrefs(placeAndSport);

        //LLAMADA A LA API PARA TRAER LOS TURNOS OCUPADOS
        getBusyTurns();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_turns_busy, container, false);
        context = container.getContext();
        progressBar = view.findViewById(R.id.progressBarBusy);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshChaña1);
        progressBar.setVisibility(View.VISIBLE);
        //REFRESH TO LAYOUT
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               refreshList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }



    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        Court court = (Court) l.getItemAtPosition(position);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle("¿Desea cancelar el turno?");
        builder.setNeutralButton("Sancionar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Call<LoginResponse> callToSanction = RetrofitClient
                        .getInstance()
                        .getApi()
                        .userSanctioned(token,court.getName(),court.getId_users(),court.getId_place());

                callToSanction.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        LoginResponse message = response.body();
                        Toasty.success(getActivity(),message.getMessage(),Toast.LENGTH_SHORT,true).show();
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(getActivity(),"error al sancionar", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                int id = court.getId();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String date = sdf.format(court.getDate());
                cancelAndSearch(court,id,date);
            }

        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void cancelAndSearch(Court court,int id, String date) {
        //BUSCA SI HAY ALGUIEN EN LA LISTA Y ENVIA LA NOTIFICACION EN CASO DE QUE SI
        Call<LoginResponse> call2 = RetrofitClient
                .getInstance()
                .getApi()
                .searchwaitlist(court.getId_place(),date,court.getEntryTime(),court.getDepartureTime(),
                        court.getCourtNumber(),id);

        call2.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse message = response.body();
                Toasty.success(getActivity(),"Turno Cancelado!",Toasty.LENGTH_SHORT,true).show();
                Log.e("Search","Hay usuarios: "+message.getMessage());
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error en la llamada", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void refreshList() {
        Call<List<Court>> callGetTurnBusy = RetrofitClient
                .getInstance()
                .getApi()
                .getAllTurnsBusy(token,id_place,courtNumber);

        callGetTurnBusy.enqueue(new Callback<List<Court>>() {
            @Override
            public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                courts = (List<Court>) response.body();
                adapter = new TurnRequestedAdapter(getActivity(),courts);
                setListAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Court>> call, Throwable t) {
                Toast.makeText(getActivity(),"error", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void getBusyTurns() {
        Call<List<Court>> callGetTurnBusy = RetrofitClient
                .getInstance()
                .getApi()
                .getAllTurnsBusy(token,id_place,courtNumber);

        callGetTurnBusy.enqueue(new Callback<List<Court>>() {
            @Override
            public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                courts = (List<Court>) response.body();
                adapter = new TurnRequestedAdapter(context,courts);
                setListAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Court>> call, Throwable t) {
                Toast.makeText(getActivity(),"error", Toast.LENGTH_LONG).show();
            }
        });
    }
}