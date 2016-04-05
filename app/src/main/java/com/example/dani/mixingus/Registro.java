package com.example.dani.mixingus;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Registro extends AppCompatActivity implements View.OnClickListener{

    EditText email;
    EditText nombre;
    EditText contraseña;
    EditText ciudad;
    EditText descripcion;
    Button BotonRegistro;
    String nombreReg,emailReg,contraReg;
    Connection con;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        //Ignorar Seguridad
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        contraseña = (EditText) findViewById(R.id.cajacontraseñareg);
        email = (EditText) findViewById(R.id.cajacorreoreg);
        nombre = (EditText) findViewById(R.id.cajanombrereg);
        ciudad = (EditText) findViewById(R.id.cajaCiudadReg);
        descripcion = (EditText) findViewById(R.id.cajaRegDec);
        BotonRegistro = (Button) findViewById(R.id.botonreg);
        BotonRegistro.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {

        new ConexionDB().execute(null, null, null);

    }

    public class ConexionDB extends AsyncTask<String,Void,ResultSet> {

        Statement estado;
        @Override
        protected ResultSet doInBackground(String... strings) {

            try {

                con = DriverManager.getConnection("jdbc:mysql://db4free.net:3306/mixingususers?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=utf-8", "adminmixingus", "135790");
                System.out.println("CONECTANDOOOOOOOOO");
                estado = con.createStatement();

                ResultSet result = estado.executeQuery("select * from users");
                /*while (result.next()){

                    System.out.println(result.getString("nombre"));

                };*/

                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                Log.d("milog", "Error de sql" + e.getErrorCode() + "\n"
                        + e.getSQLState()
                        + "\n" +
                        e.getMessage()
                        + "\n" +
                        e.getLocalizedMessage()
                        + "\n" +
                        e.getCause());
                return null;
            }


        }

        @Override
        protected void onPostExecute(ResultSet result) {

            try {
                if (result != null){

                    boolean UsuarioDupli=false;


                    while (result.next()) {
                        System.out.println("Leyendo datos base");
                        if (result.getString("correo").equals(email.getText().toString()) || email.getText().toString().equals("")) {
                            System.out.println("Usuario duplicado");
                            UsuarioDupli = true;

                        }
                    }


                    if(UsuarioDupli==false){
                        System.out.println("Insertando en la base...");

                        Double def=0.0;

                        System.out.println("Statement creado");
                        estado.executeUpdate("INSERT INTO users(nombre,correo,contra,ciudad,descripcion,longitud,latitud) VALUES ('" + nombre.getText().toString() + "','" + email.getText().toString() + "','" + contraseña.getText().toString() + "','" + ciudad.getText().toString() + "','"+descripcion.getText().toString()+"','"+def+"','"+def+"')");
                        System.out.println("insertado en la base listo");
                        email.setText("");
                        nombre.setText("");
                        contraseña.setText("");
                        ciudad.setText("");
                        Toast.makeText(getApplicationContext(), "¡Bienvenido a MixingUS!!!", Toast.LENGTH_LONG).show();

                        finish();
                    }else{

                        email.setText("");
                        Toast.makeText(getApplicationContext(),"¡Correo ya registrado o invalido! \n Use otra cuenta de correo, GRACIAS!!", Toast.LENGTH_LONG).show();

                    }


                }else{

                    System.out.println("No HAY Datos");
                    Toast toast = Toast.makeText(getApplicationContext(),"No se a podido acceder a la base de datos... \n            Intentalo de nuevo mas tarde",Toast.LENGTH_LONG);
                    toast.show();

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }


}
