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
