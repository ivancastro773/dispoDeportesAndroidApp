package com.example.dispo_deportessr;

import com.example.dispo_deportessr.Models.CodeMsjResponse;
import com.example.dispo_deportessr.Models.Court;
import com.example.dispo_deportessr.Models.LoginResponse;
import com.example.dispo_deportessr.Models.Place;
import com.example.dispo_deportessr.Models.SanctionList;
import com.example.dispo_deportessr.Models.Sports;
import com.example.dispo_deportessr.Models.User;
import com.example.dispo_deportessr.Models.WaitList;

import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Api {

    // REGISTRO DE USUARIO
    @FormUrlEncoded
    @POST("auth/register")
    Call<LoginResponse> register (
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("passwordConfirm") String passwordConfirm,
            @Field("tokenIdPhone") String tokenIdPhone
    );
    @FormUrlEncoded
    @POST("auth/register-google")
    Call<LoginResponse> registerWithGoogle (
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("tokenIdPhone") String tokenIdPhone
    );
    //LOGIN
    @FormUrlEncoded
    @POST("auth/login")
    Call<LoginResponse> userlogin (
            @Field("email") String email,
            @Field("password") String password
    );

    //PERFIL DEL USUARIO
    @GET("profile")
    Call<User> getProfile(@Header("authorization") String authorization);

    //EMAIL DE LOS ADMINS
    @GET("/users/admins")
    Call<List<User>> getAllAdmin();

    //LISTA DE DEPORTES
    @GET("sports")
    Call<List<Sports>> getSports(@Header("authorization") String authorization);

    //LISTA DE COMPLEJOS SEGUN EL DEPORTE
    @GET("sports/{id_sports}")
    Call<List<Place>> getPlace(
            @Header("authorization") String authorization,
            @Path("id_sports") int id_sports
    );
    
    //LISTA DE TODOS LOS COMPLEJOS
    @GET("sports/generalfc/places/{id_users}")
    Call<Place> getPlaceByUser(
            @Header("authorization") String authorization,
            @Path("id_users") int id_users
    );

    //INFORMACION DE COMPLEJOS

    //SELECCIONO EL COMPLEJO SEGUN EL USUARIO
    @GET("/information/{id_user}")
    Call<Place> getUserPlace(
            @Header("authorization") String authorization,
            @Path("id_user") int id_user
    );

    @GET("/information/name/{id}")
    Call<Place> getNamePlaceId(
            @Header("authorization") String authorization,
            @Path("id") int id
    );

    //INFORMACION DEL COMPLEJO
    @GET("/information/data/{id}")
    Call<Place> getInfoPlace(
            @Header("authorization") String authorization,
            @Path("id") int id
    );

    //CANTIDAD DE CANCHAS DEL COMPLEJO
    @GET("/information/{id}/totalcourts")
    Call<Place> getCantCourts(
            @Header("authorization") String authorization,
            @Path("id") int id
    );

    //EDITA INFORMACION DEL COMPLEJO
    @FormUrlEncoded
    @PUT("/information/{id}")
    Call<ResponseBody> saveDataPlace (
            @Header("authorization") String authorization,
            @Field("description") String description,
            @Field("address") String address,
            @Field("phone") String phone,
            @Path("id") int id
            //@Field("tokenIdPhone") String tokenIdPhone
    );

    @FormUrlEncoded
    @POST("/sports/place/{id_place}/addcourts")
    Call<LoginResponse> addNewCourt (
            @Header("authorization") String authorization,
            @Path("id_place") int id_place,
            @Field("courtNumber") int courtNumber
    );


    //SUBIDA DE DATOS E IMAGEN
    @Multipart
    @POST("sports/place/photo/add")
    Call<ResponseBody> saveDataImage (
            @Header("authorization") String authorization,
            @Part("id") RequestBody id,
            @Part MultipartBody.Part image
            //@Field("tokenIdPhone") String tokenIdPhone
    );


    //    ------      TURNOS    --------

    //RESERVA DEL TURNO
    @FormUrlEncoded
    @PUT("sports/place/turn/{id_place}/{id}/{nlist}")
    Call<CodeMsjResponse> reserveRegister (
            @Header("authorization") String authorization,
            @Field("id_users") int id_users,
            @Field("name") String name,
            @Field("status") String status,
            @Field("date") String date,
            @Path("id_place") int id_place,
            @Path("id") int id,
            @Path("nlist") int nlist
            //@Field("tokenIdPhone") String tokenIdPhone
    );

    //TURNOS PEDIDOS DEL USUARIO
    @GET("sports/place/turn/{id_users}")
    Call<List<Court>> getTurnsUser(
            @Header("authorization") String authorization,
            @Path("id_users") int id_users
    );

    //CANCELACION DEL TURNO
    @FormUrlEncoded
    @PUT("sports/place/cancelturn/{id}")
    Call<CodeMsjResponse> cancelTurn(
            @Field("id_users") int id_users,
            @Field("name") String name,
            @Field("status") String status,
            @Path("id") int id
    );

    // -------- ADMINISTRADOR ---------

    //TURNOS SOLICITADOS
    @GET("sports/place/turnbusy/{id_place}/{courtNumber}")
    Call<List<Court>> getAllTurnsBusy(
            @Header("authorization") String authorization,
            @Path("id_place") int id_place,
            @Path("courtNumber") int courtNumber
    );

    //LISTA DE SANCIONADOS
    @GET("sports/generalfc/sanctionlist/{id_place}")
    Call<List<SanctionList>> getSanctionList(
            @Header("authorization") String authorization,
            @Path("id_place") int id_place
    );

    //VERIFICA SI EL USUARIO ESTA SANCIONADO
    @GET("sports/generalfc/verifysanction/{id_place}/{id_users}")
    Call<CodeMsjResponse> getVeriftySanction(
            @Header("authorization") String authorization,
            @Path("id_place") int id_place,
            @Path("id_users") int id_users
    );

    //DEVUELVE LOS DIAS (SANCIONADO Y LIBRE)
    @GET("sports/generalfc/verifysanction/sanctioned/{id_place}/{id_users}")
    Call<SanctionList> getDays(
            @Header("authorization") String authorization,
            @Path("id_place") int id_place,
            @Path("id_users") int id_users
    );

    //SANCIONAR USUARIO
    @FormUrlEncoded
    @POST("sports/generalfc/sanction")
    Call<LoginResponse> userSanctioned(
            @Header("authorization") String authorization,
            @Field("name") String name,
            @Field("id_users") int id_users,
            @Field("id_place") int id_place
    );

    //LIBERAR USUARIO DE LA SANCION
    @DELETE("sports/generalfc/userFreeSanction/{id_place}/{id_users}")
    Call<LoginResponse> userFreeSanction(
            @Header("authorization") String authorization,
            @Path("id_place") int id_place,
            @Path("id_users") int id_users
    );



    //VERIFICA SI LA CANCHA ESTA EN LA LISTA DE CANCHAS BLOQUEADAS
    @GET("sports/generalfc/statuscourt/{id_sport}/{id_place}")
    Call<List<Court>> getStatusCourt(
            @Header("authorization") String authorization,
            @Path("id_sport") int id_sport,
            @Path("id_place") int id_place
    );

    //TRAE LA LISTA DE LAS CANCHAS PARA BLOQUEAR
    @GET("sports/generalfc/listbloquedcourt/{id_place}")
    Call<List<Court>> getListBlockCourt(
            @Header("authorization") String authorization,
            @Path("id_place") int id_place
    );


    //BLOQUEA LA CANCHA
    @FormUrlEncoded
    @POST("sports/generalfc/addbloquedcourt")
    Call<CodeMsjResponse> blockCourt (
            @Header("authorization") String authorization,
            @Field("id_sport") int id_sport,
            @Field("id_place") int id_place,
            @Field("courtNumber") int courtNumber
    );

    //DESBLOQUEA LA CANCHA
    @DELETE("sports/generalfc/freecourt/{id_sport}/{id_place}/{courtNumber}")
    Call<CodeMsjResponse> freeCourt(
            @Header("authorization") String authorization,
            @Path("id_sport") int id_sport,
            @Path("id_place") int id_place,
            @Path("courtNumber") int courtNumber
    );

    //PONER MODO VACACIONES
    @FormUrlEncoded
    @PUT("sports/generalfc/modeholidays/{id}")
    Call<CodeMsjResponse> modeHolidays(
            @Header("authorization") String authorization,
            @Field("holidays") int holidays,
            @Path("id") int id
    );

    //VERIFICA SI ESTA DE VACACIONES EL COMPLEJO
    @GET("sports/generalfc/searchholidays/{id}")
    Call<CodeMsjResponse> verifHolidays(
            @Header("authorization") String authorization,
            @Path("id") int id

    );

    //SACA MODO VACACIONES
    @FormUrlEncoded
    @PUT("sports/generalfc/freeholidays/{id}")
    Call<CodeMsjResponse> freeHolidays(
            @Header("authorization") String authorization,
            @Field("holidays") int holidays,
            @Path("id") int id
    );

    // ---------    BUSCADORES --------

    //BUSCAR POR NUMERO DE CANCHA
    @GET("sports/place/{id_place}/{courtNumber}")
    Call<List<Court>> getTurnsForCourtNumber(
            @Header("authorization") String authorization,
            @Path("id_place") int id_place,
            @Path("courtNumber") int courtNumber
    );

    //BUSCAR POR HORA
    @GET("sports/place/searchforhour/{id_place}/{entryTime}")
    Call<List<Court>> getTurnsForHour(
            @Header("authorization") String authorization,
            @Path("id_place") int id_place,
            @Path("entryTime") String entryTime
    );

    //BUSCAR POR FECHA
    @GET("sports/place/searchfordate/{id_place}/{courtNumber}/{date}")
    Call<List<Court>> getTurnsForDate(
            @Header("authorization") String authorization,
            @Path("id_place") int id_place,
            @Path("courtNumber") int courtNumber,
            @Path("date") Date date
    );

    // ---------    LISTA DE ESPERA --------

    //REGISTRARSE EN LA LISTA DE ESPERA
    @FormUrlEncoded
    @POST("sports/place/turnwaitlist")
    Call<CodeMsjResponse> reserveTurnWaitList(
            @Header("authorization") String authorization,
            @Field("id_court") int id_court,
            @Field("id_users") int id_users,
            @Field("id_place") int id_place,
            @Field("date") Date date,
            @Field("courtNumber") int courtNumber,
            @Field("entryTime") String entryTime,
            @Field("departureTime") String departureTime,
            @Field("name") String name,
            @Field("tokenIdPhone") String tokenIdPhone
    );

    //BUSCA SI HAY ALGUN USUARIO EN LA LISTA DE ESPERA
    @FormUrlEncoded
    @POST("sports/place/searchwaitlist/")
    Call<LoginResponse> searchwaitlist(
            @Field("id_place") int id_place,
            @Field("date") String date,
            @Field("entryTime") String entryTime,
            @Field("departureTime") String departureTime,
            @Field("courtNumber") int courtNumber,
            @Field("id_court") int id_court


    );

    //TURNOS DE LOS USUARIOS EN LA LISTA DE ESPERA
    @GET("sports/place/turnwaitlist/{id_users}")
    Call<List<WaitList>> turnwaitlist(
            @Header("authorization") String authorization,
            @Path("id_users") int id_users
    );

    //ELIMINA EL TURNO EN LA LISTA DE ESPERA
    @DELETE("sports/place/turnwaitlist/{id_place}/{id}")
    Call<CodeMsjResponse> cancelTurnwaitlist(
            @Header("authorization") String authorization,
            @Path("id_place") int id_place,
            @Path("id") int id
    );

    //Expira el tiempo
    @FormUrlEncoded
    @POST("sports/place/expiredtime")
    Call<LoginResponse> expiredTime(
            @Field("id_court") int id_court
    );

    //Para el tiempo
    @GET("sports/place/expiredtimestop")
    Call<LoginResponse> expiredTimeStop();

}
