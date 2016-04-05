# MixingUs
Aplicación con geolocaclización y mapas, conexion a base de datos mysql entre otras cosas


La aplicación trata de una red social basada en geolocalización. Los usuarios pueden registrarse y logearse.
Una vez logueados, pueden geolocalizar a los usuarios que se encuentren en la red social y ver una breve descripción
de cada uno de ellos. Los usuarios aparecerán como "Markers" en el mapa. El mapa está basado en la APi de google y usa
GPS provider (locaclización vía saélite). El mapa está integrado en un fragment. Además, guarda la información de los usuarios 
en una base de datos MySQL (db4free.net) usando el Mysql-Connector Driver. Los métodos de consulta se realizan a través 
de un AsyncTask que ejecuta el código sin ser visible para el usuario. Esta clase devuelve un ResultSet con el resultado 
de la consulta para operar posteriormente con los datos obtenidos.

Toda la aplicación está basada en la plantilla Navigation Activity. Los iconos del menú se han cambiado con vectores y se han
cambiado las listas y su disposición.

Clase Conexion con Async Task 

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
    
    
    
    
    Preparar y cargar mapa
    
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
    
    
    
    
    Se realizan varias conexionesDB() en el proyecto considerando el hecho de que sería una app viva y necesitariamos tener
    todos los datos actualizadods en todo momento

    //**************************************************************************************************************************************************************
