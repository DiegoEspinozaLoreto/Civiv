package com.example.civiv;

import java.util.List;

public class Productoss {
    private String id;
    private String nombreProducto;
    private String cantidad;
    private List<String> nombresImagenes;

    public Productoss() {
        // Constructor vacío requerido para Firebase
    }

    public Productoss(String id, String nombreProducto, String cantidad, List<String> nombresImagenes) {
        this.id = id;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.nombresImagenes = nombresImagenes;
    }

    public String getId() {
        return id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getCantidad() {
        return cantidad;
    }

    public List<String> getNombresImagenes() {
        return nombresImagenes;
    }

    public void setNombresImagenes(List<String> nombresImagenes) {
        this.nombresImagenes = nombresImagenes;
    }
}
