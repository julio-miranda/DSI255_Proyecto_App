package com.cm2d.hbbc_app.modelo;

public class Bungalows {

    String id;

    String Cantidad;

    @Override
    public String toString() {
        return "Bungalows: " +
                "Numero = " + id  +
                ", Precio = " + Precio +
                ", Habitaciones: " +Habitaciones;
    }

    String Disponibilidad;
    String Habitaciones;
    String Precio;

    public void setCantidad(String cantidad) {
        Cantidad = cantidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        Disponibilidad = disponibilidad;
    }

    public void setHabitaciones(String habitaciones) {
        Habitaciones = habitaciones;
    }

    public void setPrecio(String precio) {
        Precio = precio;
    }

    public String getCantidad() {
        return Cantidad;
    }

    public String getDisponibilidad() {
        return Disponibilidad;
    }

    public String getHabitaciones() {
        return Habitaciones;
    }

    public String getPrecio() {
        return Precio;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
