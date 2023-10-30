package com.cm2d.hbbc_app.modelo;

public class Reserva {
    String Bungalow;
    String fechaFinal;
    String fechaInicio;

    String precioTotal;

    public void setPrecioTotal(String precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getPrecioTotal() {
        return precioTotal;
    }

    public void setBungalow(String bungalow) {
        Bungalow = bungalow;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getBungalow() {
        return Bungalow;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }
}
