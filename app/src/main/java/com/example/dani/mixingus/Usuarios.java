package com.example.dani.mixingus;

/**
 * Created by Dani on 27/03/2016.
 */
public class Usuarios {

    String nombre;
    String descripcion;
    String latitud;
    String longitud;

    public Usuarios() {
    }

    public Usuarios(String nombre, String descripcion, String latitud, String longitud) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getLatitud() {
        return Double.parseDouble(latitud);
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return Double.parseDouble(longitud);
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
