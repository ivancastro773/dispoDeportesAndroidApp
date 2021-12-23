package com.example.dispo_deportessr.Admins;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.dispo_deportessr.UserAdminActivity;
import com.example.dispo_deportessr.UserMainActivity;
import com.example.dispo_deportessr.R;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.Place;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditInfoPlaceActivity extends AppCompatActivity implements View.OnClickListener{

    private TextInputEditText description, address,phone;
    private BottomNavigationView bottomNavigation;
    private Button buttonSaveData;
    private Toolbar mTopToolbar;
    private SharedPreferences tokenAndRole,placeAndSport;
    private SharedPreferences.Editor adminDataEditor;
    private ImageView imageProfile;
    private ProgressBar progressBarImage;

    //image
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1000;
    private Uri fileUri;
    private int id_place;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        if (width >= 480 && height <=800){
            setContentView(R.layout.activity_edit_info_small);
        }if (width >= 1080 && height >= 1920){
            setContentView(R.layout.activity_edit_info_place);
        }if (width >= 720 && height <= 1280){
            setContentView(R.layout.activity_edit_info_place_med);
        }

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        description = findViewById(R.id.editTextDescription);
        address = findViewById(R.id.editTextAddress);
        phone = findViewById(R.id.editTextPhone);
        buttonSaveData = findViewById(R.id.buttonSave);
        imageProfile = findViewById(R.id.imageProfilePlace);
        progressBarImage = findViewById(R.id.progressBarImage);
        progressBarImage.setVisibility(View.GONE);

        Intent receive = getIntent();
        int num_id_place = receive.getIntExtra("num_id_place",0);

        tokenAndRole = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        token = tokenAndRole.getString("token","null");
        placeAndSport = getSharedPreferences("placeAndSport", Context.MODE_PRIVATE);
        //final int id_place = placeAndSport.getInt("id_place",0);

            Call<Place> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .getInfoPlace(token,num_id_place);

            call.enqueue(new Callback<Place>() {
                @Override
                public void onResponse(Call<Place> call, Response<Place> response) {
                    Place place = response.body();

                    description.setText(place.getDescription());
                    address.setText(place.getAddress());
                    phone.setText(place.getPhone());
                    id_place = place.getId();

                    if (place.getImage_url() == null){
                        imageProfile.setImageResource(R.drawable.ic_logo_image);
                    }else {
                        Glide.with(EditInfoPlaceActivity.this)
                                .load(place.getImage_url())
                                .transition(DrawableTransitionOptions.withCrossFade(2000))
                                .into(imageProfile);
                    }
                }

                @Override
                public void onFailure(Call<Place> call, Throwable t) {
                    Toast.makeText(EditInfoPlaceActivity.this, "error al traer datos de complejos", Toast.LENGTH_SHORT).show();
                }
            });


        buttonSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        //permission not granted,request it
                        String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permission,PERMISSION_CODE);
                    }else {
                        //permission already granted
                        pickImageFromGallery();
                    }
                }else{
                    //system os is less then marshmallow
                    pickImageFromGallery();
                }
            }
        });

        buttonSaveData.setOnClickListener(this);
        
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.admin);


        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.turns:
                        startActivity(new Intent(getApplicationContext(), UserMainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.turnsRequested:
                        startActivity(new Intent(getApplicationContext(), TurnRequestedActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.userSantioned:
                        startActivity(new Intent(getApplicationContext(), UserSantionedActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.admin:
                        return true;
                }
                return false;
            }
        });

    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_PICK_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set image to image view
           // String path = data.getData().getPath();
            fileUri = data.getData();

            if (fileUri != null){
                imageProfile.setImageURI(data.getData());
                try {
                    InputStream is = getContentResolver().openInputStream(fileUri);
                    saveImageData(getBytes(is));
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    public void saveImageData(byte[] imageBytes) {
        progressBarImage.setVisibility(View.VISIBLE);
        String mimeType = getContentResolver().getType(fileUri);
        //controlo que la imagen solo sea JPG JPEG PNG
        if (mimeType.equals("image/jpeg") || mimeType.equals("image/jpg") || mimeType.equals("image/png")){
            RequestBody idpart = RequestBody.create(MultipartBody.FORM,String.valueOf(id_place));
            RequestBody requestBody = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)),imageBytes);
            MultipartBody.Part parts =  MultipartBody.Part.createFormData("image","image",requestBody);

            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .saveDataImage(token,idpart,parts);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        progressBarImage.setVisibility(View.GONE);
                        Toasty.success(EditInfoPlaceActivity.this,"Foto guardada!",Toast.LENGTH_SHORT,true).show();
                    }else {
                        Toast.makeText(EditInfoPlaceActivity.this, "NO GUARDO LOS DATOS", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(EditInfoPlaceActivity.this, "NO SE HIZO LA LLAMADA DE GUARDAR IMAGEN", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toasty.warning(this, "Debe ser una imagen .jpeg-jpg-png", Toast.LENGTH_LONG,true).show();
        }
    }

    private void saveData() {
        final String description1 = description.getText().toString().trim();
        final String address1 = address.getText().toString().trim();
        final String phone1 = phone.getText().toString().trim();

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .saveDataPlace(token,description1,address1,phone1,id_place);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Toast.makeText(EditInfoPlaceActivity.this, "Datos Guardados", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditInfoPlaceActivity.this, UserAdminActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EditInfoPlaceActivity.this, "Error al editar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        int buffSize = 1024;
        byte[] buff = new byte[buffSize];

        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }

        return byteBuff.toByteArray();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission was granted
                    pickImageFromGallery();
                }else {
                    Toast.makeText(EditInfoPlaceActivity.this, "Permiso denegado!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSave:
                saveData();
                break;
        }
    }
}