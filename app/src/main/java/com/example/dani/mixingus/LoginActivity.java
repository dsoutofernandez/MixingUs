package com.example.dani.mixingus;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText correoe;
    EditText contraseña;
    boolean logeado = false;
    Button botonReg;
    Button botonLog;
    Location location;
    Connection con;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        contraseña = (EditText) findViewById(R.id.CajaContraIni);
        correoe = (EditText) findViewById(R.id.CajaCorreoIni);
        mLoginFormView = findViewById(R.id.botonLogin);
        mProgressView = findViewById(R.id.login_progress);
        botonLog = (Button) findViewById(R.id.botonLogin);
        botonLog.setOnClickListener(this);
        botonReg = (Button) findViewById(R.id.botonRegistroLogin);
        botonReg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.botonRegistroLogin:
                Intent intentRegistro = new Intent(LoginActivity.this, Registro.class);
                LoginActivity.this.startActivity(intentRegistro);
                //Toast.makeText(getApplicationContext(),"¡Boton REGISTRO!", Toast.LENGTH_LONG).show();
                break;
            case R.id.botonLogin:
                // Intent intentRegistro = new Intent(LoginActivity.this, Registro.class);
                //LoginActivity.this.startActivity(intentRegistro);
                login(correoe.getText().toString(), contraseña.getText().toString());
                //Toast.makeText(getApplicationContext(),"¡Boton LOGIN", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }


    private void login(String usuario, String contra){
        setUpMap();
        new ConexionDB().execute(null, null, null);
    }

    //**********************************************************************************************************************************************************************************

    public class ConexionDB extends AsyncTask<String,Void,ResultSet> {

        @Override
        protected ResultSet doInBackground(String... strings) {

            Statement estado;

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                System.out.println("Registro exitoso");
            } catch (Exception e) {
                Log.d("milog", "Error de CLASE");
                System.out.println(e.toString());
            }
            try {
                con = DriverManager.getConnection("jdbc:mysql://db4free.net:3306/mixingususers?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=utf-8", "adminmixingus", "135790");
                System.out.println("CONECTANDOOOOOOOOO");
                estado = con.createStatement();
                ResultSet result = estado.executeQuery("select * from users");
                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                Log.d("milog", "Error de sql "+e.getErrorCode()+"\n"
                        +e.getSQLState()
                        +"\n"+
                        e.getMessage()
                        +"\n"+
                        e.getLocalizedMessage()
                        +"\n"+
                        e.getCause());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResultSet result) {
            try {
                if (result != null){
                    while (result.next()) {
                        System.out.println("Leyendo datos base");
                        if(isEmailValid(correoe.getText().toString())){
                            if (result.getString("correo").equals(correoe.getText().toString()) && result.getString("contra").equals(contraseña.getText().toString())) {
                                System.out.println("Sesión iniciada");
                                logeado = true;
                                Toast.makeText(getApplicationContext(), "Bienvenido/a de nuevo " + result.getString("nombre"), Toast.LENGTH_LONG).show();
                                MainActivity.correo =result.getString("correo");
                                Toast.makeText(getApplicationContext(),MainActivity.correo, Toast.LENGTH_LONG).show();
                                MainActivity.nombre = result.getString("nombre");
                                MainActivity.logeado=true;
                                finish();
                            }
                        }
                    }
                    if(logeado==false){
                        Toast.makeText(getApplicationContext(),"Inicio de sesión incorrecto", Toast.LENGTH_SHORT).show();
                        correoe.setText("");
                        contraseña.setText("");
                    }
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"No hemos podido encontrarte en nuestra base de datos",Toast.LENGTH_LONG);
                    toast.show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //***********************************************************************************************************************************************************************************

    private void setUpMap() {
        Usuarios[] users;
        //mapa.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").snippet("Snippet"));
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);

        LocationListener locListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.i("location", String.valueOf(location.getLongitude()));
                Log.i("location", String.valueOf(location.getLatitude()));
            }

            public void onProviderDisabled(String provider) {
                Log.i("info", "Provider OFF");
            }

            public void onProviderEnabled(String provider) {
                Log.i("info", "Provider ON");
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i("LocAndroid", "Provider Status: " + status);
                Log.i("info", "Provider Status: " + status);
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 500, locListener);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(),"Debe activar la locacalización y dar permiso a la aplicación", Toast.LENGTH_LONG).show();
            location =null;
        }else{
            location = locationManager.getLastKnownLocation(provider);
        }
        if (location != null) {
            LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
        }

    }

    //***********************************************************************************************************************************************************************************

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}