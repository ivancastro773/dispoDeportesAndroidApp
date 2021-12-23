package com.example.dispo_deportessr;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class FirebaseService extends  FirebaseMessagingService{
    private SharedPreferences tokenIdPhone;
    private SharedPreferences.Editor editortokenIdPhone;
    private SharedPreferences DataTurn;
    private SharedPreferences.Editor DataTurnEditor;
    Random random = new Random();
    private int idNotify = random.nextInt(8000);
    private boolean hasExecuted = false;

    //Nos da el token del dispositivo
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("token","MI TOKEN ES: "+s);

        //CREO EL METODO PREFERENCES PARA GUARDAR EL TOKEN DE DISPOSITIVO
        tokenIdPhone = getSharedPreferences("firebaseToken", Context.MODE_PRIVATE);
        editortokenIdPhone = tokenIdPhone.edit();

        editortokenIdPhone.putString("tokenIdPhone",s);
        editortokenIdPhone.commit();
    }

    //todas las notificaciones que recibamos van a llegar aqui
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();
        Log.e("TAG","Mensaje recibido de: "+from);

        if(remoteMessage.getNotification() != null){
            Log.e("TAG", "el titulo es: "+remoteMessage.getNotification().getTitle());
            Log.e("TAG", "el cuerpo es: "+remoteMessage.getNotification().getBody());
            //TRAIGO LOS DATOS DE LA NOTIFICACION
            Log.e("TAG", "el bundle es: "+remoteMessage.getData());

            //traigo el titulo y el cuerpo de la notificacion
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            DataTurn = getSharedPreferences("DataTurn", Context.MODE_PRIVATE);
            DataTurnEditor = DataTurn.edit();

            //DATOS DEL TURNO
            DataTurnEditor.putString("id_place",remoteMessage.getData().get("id_place"));
            DataTurnEditor.putString("date",remoteMessage.getData().get("date"));
            DataTurnEditor.putString("entryTime",remoteMessage.getData().get("entryTime"));
            DataTurnEditor.putString("departureTime",remoteMessage.getData().get("departureTime"));
            DataTurnEditor.putString("courtNumber",remoteMessage.getData().get("courtNumber"));
            DataTurnEditor.putString("id_court",remoteMessage.getData().get("id_court"));
            DataTurnEditor.putString("id_waitlist",remoteMessage.getData().get("id_waitlist"));
            DataTurnEditor.commit();

            //verifica si la version de android es mayor a oreo o no
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                higherOreo(title, body);
            }
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                lessOreo(title,body);
            }
        }
    }

    private void lessOreo(String title, String body) {
        String id = "mensaje";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), id);
        builder.setSmallIcon(R.drawable.ic_logo_notif)
                .setContentTitle(title)
                .setColor(ContextCompat.getColor(this, R.color.primaryColor))
                .setContentText(body)
                .setTimeoutAfter(300000)
                .setDefaults(Notification.DEFAULT_SOUND);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(idNotify, builder.build());
    }

    private void higherOreo(String title, String body){
        String id = "mensaje";
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(id, "nuevo", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(true);
            assert nm != null;
            nm.createNotificationChannel(notificationChannel);
        }
        builder.setAutoCancel(true)
                .setTimeoutAfter(300000)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_logo_notif)
                .setColor(ContextCompat.getColor(this, R.color.primaryColor))
                .setContentIntent(clickNoti());
        assert nm != null;
        nm.notify(idNotify,builder.build());
    }


    public PendingIntent clickNoti(){
        Intent nf = new Intent(getApplicationContext(),TurnWaitListActivity.class);
        nf.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this,0,nf,0);
    }
}
