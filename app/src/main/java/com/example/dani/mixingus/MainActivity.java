package com.example.dani.mixingus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    GoogleMap mapa;
    public static boolean logeado;
    public static String nombre;
    public static String correo;
    TextView Nnombre;
    TextView Ncorreo;
    Location location;
    Usuarios[] users;
    StrictMode.ThreadPolicy policy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Nnombre = (TextView) findViewById(R.id.NombreView);
        Ncorreo = (TextView) findViewById(R.id.CorreoView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        new ConexionDB().execute(null, null, null);

        SupportMapFragment smap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapa = smap.getMap();


        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.setMyLocationEnabled(true);
        //setUpMap();


    }

    private void setUpMap() {
        Usuarios[] users;
        //mapa.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").snippet("Snippet"));
        mapa.setMyLocationEnabled(true);
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


        //USAR IF LOGEADO
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 500, locListener);



        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){


            Toast.makeText(getApplicationContext(),"Debe activar la locacalización y dar permiso a la aplicación", Toast.LENGTH_LONG).show();
            location =null;
        }else{

            location = locationManager.getLastKnownLocation(provider);
        }


        if (location != null) {

            LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition position = this.mapa.getCameraPosition();

            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(15);
            builder.target(target);

            this.mapa.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
            mapa.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Hola amable desconocido/a!!").snippet("Inicia sesión con nosotros para conocer gente GUAY!"));
        }





    }

    //**************************************************************************************************************************************************************


    public class ConexionDBMap extends AsyncTask<String,Void,ResultSet> {

        Connection conn;
        ResultSet result1;
        Statement estado1;
        Statement estado2;

        @Override
        protected ResultSet doInBackground(String... strings) {


            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                System.out.println("Registro exitoso");

            } catch (Exception e) {
                Log.d("milog", "Error de CLASE");
                System.out.println(e.toString());
                Toast.makeText(getApplicationContext(),"No ha sido posible CONECTARSE a la base de datos",Toast.LENGTH_LONG).show();

            }



            try {

                conn = DriverManager.getConnection("jdbc:mysql://db4free.net:3306/mixingususers?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=utf-8","adminmixingus","135790");

                estado1 = conn.createStatement();

                result1 = estado1.executeQuery("select * from users");

                return result1;
            } catch (SQLException e) {
                e.printStackTrace();
                Log.d("milog", "Error de sql"+e.getErrorCode()+"\n"
                        +e.getSQLState()
                        +"\n"+
                        e.getMessage()
                        +"\n"+
                        e.getLocalizedMessage()
                        +"\n"+
                        e.getCause());
                Toast.makeText(getApplicationContext(),"No ha sido posible acceder a los datos",Toast.LENGTH_LONG).show();
                return null;
            }


        }

        @Override
        protected void onPostExecute(ResultSet result2) {

            try {
                if (result2 != null){
                    mapa.clear();
                    while (result2.next()) {
                        mapa.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(result2.getString("latitud")),Double.parseDouble(result2.getString("longitud")))).title(result2.getString("nombre")).snippet(result2.getString("descripcion")));
                        System.out.println("Leyendo para longitud y latitud");
                        Toast.makeText(getApplicationContext(), "Correo base "+result2.getString("correo")+" Nombre almacenado: "+correo, Toast.LENGTH_SHORT).show();
                        if(result2.getString("correo").equals(correo)){
                            estado2  = conn.createStatement();
                            Toast.makeText(getApplicationContext(),"Hemos accedido al correo",Toast.LENGTH_LONG).show();
                            if(location==null){

                                Toast.makeText(getApplicationContext(), "No hemos podido actualizar tu posicion...            :(  " +nombre, Toast.LENGTH_LONG).show();
                                LatLng target = new LatLng(Double.parseDouble(result1.getString("latitud")), Double.parseDouble(result1.getString("longitud")));
                                CameraPosition.Builder builder = new CameraPosition.Builder();
                                builder.zoom(15);
                                builder.target(target);
                                mapa.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
                                Toast.makeText(getApplicationContext(), "Estamos usando tu última posición conocida!", Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(), "Activa tu localización o sal al exterior!", Toast.LENGTH_LONG).show();

                            }else{
                            estado2.executeUpdate("update users set latitud='"+Double.toString(location.getLatitude())+"' where correo='"+correo+"'");
                            estado2.executeUpdate("update users set longitud='" + Double.toString(location.getLongitude()) + "' where correo='" + correo + "'");
                            Toast.makeText(getApplicationContext(),"Hemos actualizado tu posición!",Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "Lat " + location.getLatitude() + " y Long " + Double.toString(location.getLongitude()) + " Actualizadas", Toast.LENGTH_LONG).show();

                            }
                            result1.afterLast();
                        }
                    }
                  }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"No hemos podido actualizar tu posicion",Toast.LENGTH_LONG);
                    toast.show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //**************************************************************************************************************************************************************

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ConexionDB extends AsyncTask<String,Void,ResultSet> {

        Connection con;
        @Override
        protected ResultSet doInBackground(String... strings) {

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                System.out.println("Registro exitoso");

            } catch (Exception e) {
                Log.d("milog", "Error de CLASE");
                System.out.println(e.toString());
            }

            try {

                con = DriverManager.getConnection("jdbc:mysql://db4free.net:3306/mixingususers?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=utf-8","adminmixingus","135790");

                Statement estado = con.createStatement();
                ResultSet result = estado.executeQuery("select * from users");

                /*while (result.next()){
                    System.out.println(result.getString("nombre"));
                };*/

                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                Log.d("milog", "Error de sql"+e.getErrorCode()+"\n"
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
                    if (!result.next()) {
                        Toast toast = Toast.makeText(getApplicationContext(),"No existen resultados con ese nombre",Toast.LENGTH_LONG);
                        toast.show();
                    }else{
                        Toast.makeText(getApplicationContext(),result.getString("nombre"), Toast.LENGTH_SHORT).show();
                        while (result.next()){
                            System.out.println(result.getString("nombre"));
                            Toast.makeText(getApplicationContext(),result.getString("nombre"), Toast.LENGTH_SHORT).show();
                        };
                    }

                    con.close();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"No hay usuarios",Toast.LENGTH_LONG);
                    toast.show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
        intentLogin.putExtra("correo", "");
        intentLogin.putExtra("contra", "");
        intentLogin.putExtra("logeado",false);

        Intent intentPerfil = new Intent(MainActivity.this, MainProfileActivity.class);


        if (id == R.id.nav_perfil) {
            // Accion del perfil
            if(logeado==true) {

                MainActivity.this.startActivity(intentPerfil);

            }else {
                MainActivity.this.startActivity(intentLogin);
            }

        } else if (id == R.id.nav_mapa) {

        } else if (id == R.id.nav_fotos) {

            if(logeado==true){

                new ConexionDBMap().execute(null, null, null);
                setUpMap();
                Toast.makeText(getApplicationContext(),"Estas logueado y actualizando", Toast.LENGTH_LONG).show();

            }else{

                setUpMap();
                Toast.makeText(getApplicationContext(),"Debes LOGUEARTE antes!!", Toast.LENGTH_LONG).show();

            }

        } else if (id == R.id.nav_cerrar) {
            logeado=false;
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}