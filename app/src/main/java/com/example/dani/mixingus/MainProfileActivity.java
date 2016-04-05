package com.example.dani.mixingus;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainProfileActivity extends AppCompatActivity implements View.OnClickListener{

    EditText email;
    EditText nombre;
    EditText ciudad;
    EditText descripcion;
    Button BotonVolver;
    Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profile);

        email = (EditText) findViewById(R.id.editCorreoPerf);
        nombre = (EditText) findViewById(R.id.editNombrePerf);
        ciudad = (EditText) findViewById(R.id.editCiudadPerf);
        descripcion = (EditText) findViewById(R.id.editDescPerf);
        BotonVolver = (Button) findViewById(R.id.botonVolverPerf);
        BotonVolver.setOnClickListener(this);


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

                            if (result.getString("correo").equals(MainActivity.correo)) {
                                System.out.println("Leyendo perfil...");
                                Toast.makeText(getApplicationContext(), "Estamos mostrando tus datos " + result.getString("nombre"), Toast.LENGTH_LONG).show();

                                nombre.setText(result.getString("nombre"));
                                ciudad.setText(result.getString("ciudad"));
                                email.setText(result.getString("correo"));
                                descripcion.setText(result.getString("descripcion"));

                            }

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



    @Override
    public void onClick(View v) {

        finish();

    }
}
