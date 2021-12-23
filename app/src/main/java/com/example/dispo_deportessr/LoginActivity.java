package com.example.dispo_deportessr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dispo_deportessr.Utils.Util;
import com.example.dispo_deportessr.api.RetrofitClient;
import com.example.dispo_deportessr.Models.LoginResponse;
import com.example.dispo_deportessr.Models.Place;
import com.example.dispo_deportessr.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.PrivateKey;
import java.util.List;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private SharedPreferences preferences,placeAndSport,tokenIdPhone;
    private SharedPreferences.Editor editor,placeAndSportEditor;
    private ImageView imageApp;
    private ProgressBar progressBarLogin;
    private String role,token;
    private int id_place,id_sports;
    private Button btnLogin;
    private TextInputLayout textInputLayoutEmail;
    private String rolAdmin = "admin";
    private String rolEstandar = "estandar";
    //LOGIN WITH GOOGLE
    private ActivityLoginBinding binding;
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private static final String TAG ="GOOGLE_SING_IN_TAG";


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Log.d(TAG,"onActivityResult: Google SignIn intent Result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign in success, now auth with firebase
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }
            catch (Exception e){
                Log.d(TAG,"onActivityResult: // "+e.getMessage());
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG,"FirebaseAuthWithGoogle: begin firebase auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG,"Succes: Logged in");

                        //get logged in user
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        //get user info
                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();
                        String name = firebaseUser.getDisplayName();
                        Log.d(TAG,"onSuccess: uid: "+uid);
                        Log.d(TAG,"onSuccess: Email: "+email);
                        Log.d(TAG,"onSuccess: Name: "+name);
                        //TRAIGO EL TOKEN DE DISPOSITIVO
                        String tokenIdPhone1 = Util.getUserTokenIdPhonePrefs(tokenIdPhone);
                        //check if user is new or existing
                        if (authResult.getAdditionalUserInfo().isNewUser()){
                            Log.d(TAG,"onSuccess: Account created...\n"+email);
                            //GUARDO AL USUARIO EN LA BD
                            registerWithGoogle(name,email,tokenIdPhone1);
                        }
                        else {
                            Log.d(TAG,"onSuccess: Existing user...\n"+email);
                            registerWithGoogle(name,email,tokenIdPhone1);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure: Lggin failed "+e.getMessage());
                    }
                });
    }

    private void registerWithGoogle(String name, String email, String tokenIdPhone) {
        String password = "";
        Call<LoginResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .registerWithGoogle(name, email,password,tokenIdPhone);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse message = response.body();
                token = message.getMessage();
                editor.putString("token", token);
                editor.putString("role", role);
                editor.commit();
                Log.d(TAG,"token: "+token);
                if (rolEstandar.equals(message.getRol())){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{

                    Call<Place> call2 = RetrofitClient
                            .getInstance()
                            .getApi()
                            .getUserPlace(token,message.getIdAdmin());

                    call2.enqueue(new Callback<Place>() {
                        @Override
                        public void onResponse(Call<Place> call, Response<Place> response) {
                            Place data = response.body();

                            placeAndSportEditor.putInt("id_sports", data.getId_sports());
                            placeAndSportEditor.putInt("id_place", data.getId());
                            placeAndSportEditor.putString("name_place", data.getName());
                            placeAndSportEditor.commit();

                            Intent intent = new Intent(LoginActivity.this, UserMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("id_place",data.getId());
                            progressBarLogin.setVisibility(View.GONE);
                            startActivity(intent);
                            finish();
                        }
                        @Override
                        public void onFailure(Call<Place> call, Throwable t) {
                            String message_fail = getString(R.string.fail_connection_api);
                            Toasty.warning(LoginActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(TAG,"Error al registrar usuario en la bd");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /*if (width <= 480 && height <=800){
            setContentView(R.layout.activity_login_small);
        }*/
        /*if (width >= 720 && height <= 1280){
            setContentView(R.layout.activity_login_medium);
        }*/
        textInputLayoutEmail = findViewById(R.id.imputLayoutEmail);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        progressBarLogin = findViewById(R.id.progressBarLogin);
        btnLogin = findViewById(R.id.buttonLogin);
        progressBarLogin.setVisibility(View.GONE);
        imageApp = findViewById(R.id.imageApp);
        tokenIdPhone = getSharedPreferences("firebaseToken", Context.MODE_PRIVATE);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        //init Firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        //Google SignInButton
        binding.googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: Begin Google SignIn");
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });

        //CREO EL METODO SHARED PREFERENCES PARA GUARDAR DATOS DEL LOGIN
        preferences = getSharedPreferences("tokenAndRole", Context.MODE_PRIVATE);
        editor = preferences.edit();

        placeAndSport = getSharedPreferences("placeAndSport", Context.MODE_PRIVATE);
        placeAndSportEditor = placeAndSport.edit();

        //VERIFICAMOS SI YA SE LOGEO
        if (preferences.contains("token")){
            token = preferences.getString("token","error");
            role = preferences.getString("role","error");
            id_place = placeAndSport.getInt("id_place",0);
            id_sports = placeAndSport.getInt("id_sports",0);

            Intent intent;
            if(role.equals("admin")){
                intent = new Intent(LoginActivity.this, UserMainActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("id_sports", id_sports);
                intent.putExtra("id_place", id_place);
                intent.putExtra("role", role);
                progressBarLogin.setVisibility(View.GONE);
                startActivity(intent);
            }else {
                intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
                finish();
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                userLogin(email,password);
            }
        });
        findViewById(R.id.textViewRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            Log.d(TAG,"checkUser: Already logged in");
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }

    //VALIDAMOS EMAIL
    private boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //FUNCION DEL LOGIN
    private void userLogin(String email,String password) {
        if(!isValidEmail(email)){
            Toasty.warning(LoginActivity.this,"El email es incorrecto",Toast.LENGTH_SHORT,true).show();
        }else{
            progressBarLogin.setVisibility(View.VISIBLE);
            Call<LoginResponse> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .userlogin(email,password);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    LoginResponse message = response.body();
                    token = message.getMessage();
                    if (rolEstandar.equals(message.getRol())){
                        editor.putString("token", token);
                        editor.commit();
                        Log.e("TokenApp","MI TOKEN DE APP ES: "+message.getMessage());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        progressBarLogin.setVisibility(View.GONE);
                        startActivity(intent);
                        finish();

                    }if (rolAdmin.equals(message.getRol())){
                        role = message.getRol();
                        editor.putString("token", token);
                        editor.putString("role", role);
                        editor.commit();

                        Call<Place> call2 = RetrofitClient
                                .getInstance()
                                .getApi()
                                .getUserPlace(message.getMessage(),message.getIdAdmin());

                        call2.enqueue(new Callback<Place>() {
                            @Override
                            public void onResponse(Call<Place> call, Response<Place> response) {
                                Place data = response.body();

                                placeAndSportEditor.putInt("id_sports", data.getId_sports());
                                placeAndSportEditor.putInt("id_place", data.getId());
                                placeAndSportEditor.putString("name_place", data.getName());
                                placeAndSportEditor.commit();

                                Intent intent = new Intent(LoginActivity.this, UserMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("id_place",data.getId());
                                progressBarLogin.setVisibility(View.GONE);
                                startActivity(intent);
                                finish();
                            }
                            @Override
                            public void onFailure(Call<Place> call, Throwable t) {
                                String message_fail = getString(R.string.fail_connection_api);
                                Toasty.warning(LoginActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
                            }
                        });
                    }if (message.getMessage().equals("El email o contraseña son incorrectos")){
                        Toasty.warning(LoginActivity.this,message.getMessage(),Toast.LENGTH_SHORT,true).show();
                        progressBarLogin.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    String message_fail = getString(R.string.fail_connection_api);
                    Toasty.warning(LoginActivity.this,message_fail ,Toast.LENGTH_SHORT,true).show();
                }
            });
        }
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://dispo-deportes-sr.herokuapp.com/auth/form"));
        startActivity(intent);
    }
}