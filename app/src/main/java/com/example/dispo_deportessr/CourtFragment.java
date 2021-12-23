package com.example.dispo_deportessr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dispo_deportessr.Adapters.CourtListAdapter;
import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.CodeMsjResponse;
import com.example.dispo_deportessr.Models.Court;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CourtFragment extends ListFragment {

    private int id_place,courtNumber,id_users;
    private List<Court> courts;
    private SharedPreferences preferencesTokenIdPhone;
    private SwipeRefreshLayout swipeRefreshLayout;
    //BUSCADOR POR FECHA
    private SharedPreferences searchForDate;
    private SharedPreferences userData;
    private SharedPreferences tokenAndRole,placeAndSport;
    private List<Court> statusCourt;
    private CourtListAdapter adapter;
    private String token,tokenIdPhone,role;
    private Context context;
    private ProgressBar progressBar;

    public CourtFragment(int id_place, int courtNumber) {
        this.id_place = id_place;
        this.courtNumber = courtNumber;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tokenAndRole = getActivity().getSharedPreferences("tokenAndRole",Context.MODE_PRIVATE);
        token = Util.getUserTokenPrefs(tokenAndRole);
        role = Util.getUserRolePrefs(tokenAndRole);

        preferencesTokenIdPhone = getActivity().getSharedPreferences("firebaseToken",Context.MODE_PRIVATE);
        tokenIdPhone = Util.getUserTokenIdPhonePrefs(preferencesTokenIdPhone);

        placeAndSport = getActivity().getSharedPreferences("placeAndSport",Context.MODE_PRIVATE);
        int id_sports = Util.getIdSportsPrefs(placeAndSport);
        userData = getActivity().getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        id_users = Util.getUserIdPrefs(userData);

        //VERIFICA SI EL USUARIO ESTANDAR ESTA SANCIONADO
        verifUserSanction();
        //BUSCA SI LA CANCHA ESTA BLOQUEADA
        searchBlockedCourt(id_sports);

        //BUSCA LAS CANCHAS POR FECHA- USUARIO BUSCA POR FECHA
        searchForDate = getActivity().getSharedPreferences("searchForDate", Context.MODE_PRIVATE);

       int fordate = Util.getForDatePrefs(searchForDate);
       String dateString = Util.getDatePrefs(searchForDate);

        if (fordate == 2){
            Date date = parseDate(dateString);
            java.sql.Date dateSend = convertJavaDateToSqlDate(date);
            showTurnsSearched(dateSend);
        }else{
            showTurns();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = container.getContext();
        View view = inflater.inflate(R.layout.fragment_uno, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshChaña1);
        progressBar = view.findViewById(R.id.progressBarFragment);
        progressBar.setVisibility(View.VISIBLE);
        //REFRESH TO LAYOUT
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList(token);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }


    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, final long id) {

        final Court court = (Court) l.getItemAtPosition(position);
        final String name = Util.getUserNamePrefs(userData);
        final int id_court = court.getId();

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle("Desea reservar el turno?");

        if (role.equals("admin")){

        }else {
            builder.setNeutralButton("Lista de espera", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //PARSEO until.Date (fecha y hora) a sql.Date(fecha) -> porque sino no puedo guardarlo en la BD
                    java.sql.Date date2 = new java.sql.Date(court.getDate().getTime());
                    registerInWaitingList(id_court,court,date2,name);
                }
            });
        }

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String status = "Ocupado";

                if (role.equals("admin")){
                    String free = "Disponible";
                    if (free.equals(court.getStatus())){
                        Intent intent = new Intent(getActivity(), ReserveByAdminActivity.class);
                        intent.putExtra("id",id_court);
                        String dateTurn = Util.getDateToString(court.getDate());
                        intent.putExtra("date",dateTurn);
                        intent.putExtra("id_place",id_place);
                        startActivity(intent);
                    }else{
                        Toasty.error(getActivity(),"Lo sentimos, se encuentra ocupado",Toast.LENGTH_SHORT,true).show();
                    }

                }else {
                    //Parseo la fecha
                    String date = Util.getDateToString(court.getDate());
                    //RESERVA DEL TURNO
                    registerTurn(name,status,date,id_court);
                }
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

    private void registerTurn(String name, String status,String date, int id_court ) {
        Call<CodeMsjResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .reserveRegister(token,id_users,name,status,date,id_place,id_court,0);

        call.enqueue(new Callback<CodeMsjResponse>() {
            @Override
            public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                CodeMsjResponse codeMsjResponse = response.body();

                if (codeMsjResponse.getCode() == 1){
                    Toasty.success(getActivity(),codeMsjResponse.getMessage(),Toast.LENGTH_SHORT,true).show();
                    refreshList(token);
                }else{
                    Toasty.error(getActivity(),codeMsjResponse.getMessage(),Toast.LENGTH_SHORT,true).show();
                }
            }

            @Override
            public void onFailure(Call<CodeMsjResponse> call, Throwable t) {

            }
        });
    }

    private void refreshList(String token) {
        Call<List<Court>> callRefresh = RetrofitClient
                .getInstance()
                .getApi()
                .getTurnsForCourtNumber(token,id_place,courtNumber);

        callRefresh.enqueue(new Callback<List<Court>>() {
            @Override
            public void onResponse(Call<List<Court>> callRefresh, Response<List<Court>> response) {
                courts = (List<Court>) response.body();
                adapter = new CourtListAdapter(context,courts);
                setListAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Court>> callRefresh, Throwable t) {
                Toast.makeText(getActivity(),"error", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void showTurns(){
        Call<List<Court>> call = RetrofitClient
                .getInstance()
                .getApi()
                .getTurnsForCourtNumber(token,id_place,courtNumber);

        call.enqueue(new Callback<List<Court>>() {
            @Override
            public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                courts = (List<Court>) response.body();
                adapter = new CourtListAdapter(context,courts);
                setListAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Court>> call, Throwable t) {
                Toast.makeText(getActivity(),"error", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void searchBlockedCourt(int id_sports) {
        //LLAMADA VERIFICAR SI LA CANCHA ESTA BLOQUEADA
        Call<List<Court>> callStatusCourt = RetrofitClient
                .getInstance()
                .getApi()
                .getStatusCourt(token,id_sports,id_place);

        callStatusCourt.enqueue(new Callback<List<Court>>() {
            @Override
            public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                statusCourt = (List<Court>) response.body();

                for (int i = 0; i < statusCourt.size(); i++){

                    if(statusCourt.get(i).getCourtNumber() == courtNumber){

                        // Crear fragmento de tu clase
                        Fragment fragment = new CourtBlockedFragment();
                        // Obtener el administrador de fragmentos a través de la actividad
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        // Definir una transacción
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        // Remplazar el contenido principal por el fragmento
                        fragmentTransaction.replace(R.id.frameLayout, fragment);
                        fragmentTransaction.addToBackStack(null);
                        // Cambiar
                        fragmentTransaction.commit();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Court>> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(getActivity(),message_fail ,Toast.LENGTH_SHORT,true).show();
            }
        });
    }
    private void verifUserSanction() {
        Call<CodeMsjResponse> callVerifSanction = RetrofitClient
                .getInstance()
                .getApi()
                .getVeriftySanction(token,id_place,id_users);

        callVerifSanction.enqueue(new Callback<CodeMsjResponse>() {
            @Override
            public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                CodeMsjResponse message = response.body();
                if (message.getCode() == 1){
                    Intent intent = new Intent(getActivity(), SanctionActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<CodeMsjResponse> call, Throwable t) {
                String message_fail = getString(R.string.fail_connection_api);
                Toasty.warning(getActivity(),message_fail ,Toast.LENGTH_SHORT,true).show();
            }
        });
    }
    private void showTurnsSearched(Date datesend){
        Call<List<Court>> call = RetrofitClient
                .getInstance()
                .getApi()
                .getTurnsForDate(token,id_place,courtNumber,datesend);

        call.enqueue(new Callback<List<Court>>() {
            @Override
            public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                courts = (List<Court>) response.body();
                CourtListAdapter adapter = new CourtListAdapter(context,courts);
                setListAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Court>> call, Throwable t) {
                Toast.makeText(getActivity(),"error", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void registerInWaitingList(int id_court,Court court,Date date2,String name){
        //SE REGISTRA EN LA LISTA DE ESPERA

        Call<CodeMsjResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .reserveTurnWaitList(token,id_court,id_users,court.getId_place(),date2,court.getCourtNumber(),court.getEntryTime(),court.getDepartureTime(),name,tokenIdPhone);

        call.enqueue(new Callback<CodeMsjResponse>() {
            @Override
            public void onResponse(Call<CodeMsjResponse> call, Response<CodeMsjResponse> response) {
                CodeMsjResponse codeMsjResponse = response.body();
                if (codeMsjResponse.getCode() == 1){
                    Toasty.success(getActivity(),codeMsjResponse.getMessage(),Toast.LENGTH_SHORT,true).show();
                }else{
                    Toasty.warning(getActivity(),codeMsjResponse.getMessage(),Toast.LENGTH_LONG,true).show();
                }
            }

            @Override
            public void onFailure(Call<CodeMsjResponse> call, Throwable t) {
                Toasty.warning(getActivity(),"Error al comunicarse con el servidor",Toast.LENGTH_LONG,true).show();
            }
        });
    }
    public java.sql.Date convertJavaDateToSqlDate(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }
    public static java.util.Date parseDate(String dateselected) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = null;
        try {
            date = format.parse(dateselected);
        }
        catch (ParseException ex)
        {
            System.out.println(ex);
        }
        return date;
    }
}

