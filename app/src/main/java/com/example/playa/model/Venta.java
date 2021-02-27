package com.example.playa.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class Venta {
    private String uid;
    private String patente;
    private Date entrada;
    private Date salida;
    private double costo;
    private String ubicacion = "Pringles 1";
    private String usuario = "Pedro";
    private Object timestamp;


    public Venta(String uid, String patente, Date entrada, Date salida, double costo, String ubicacion, String usuario) {
        this.uid = uid;
        this.patente = patente;
        this.entrada = entrada;
        this.salida = salida;
        this.costo = costo;
        this.ubicacion = ubicacion;
        this.usuario = usuario;
    }

    public Venta() {
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public Date getEntrada() {
        return entrada;
    }

    public void setEntrada(Date entrada) {
        this.entrada = entrada;
    }

    public Date getSalida() {
        return salida;
    }

    public void setSalida(Date salida) {
        this.salida = salida;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @Exclude
    public long timestamp() {
        return (long) timestamp;
    }

    @Override
    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.entrada);
        if (salida == null){

            return patente +" Ent: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        }else{
            Calendar calendarS = Calendar.getInstance();
            calendarS.setTime(this.salida);
            return patente +" Ent: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE)+" Sal: " + calendarS.get(Calendar.HOUR) + ":" + calendarS.get(Calendar.MINUTE)+ " [$"+ this.costo + "]";

        }


       }

    private String getDate(long time) {
        Date date = new Date(time*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); // the format of your date
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(date);
    }
}
