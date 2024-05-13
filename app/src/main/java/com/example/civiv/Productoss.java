package com.example.civiv;

import java.util.List;

public class Productoss {


    private String id;
    private String nombreProducto;
    private String cantidad;
    private List<String> imageUrls;

    public Productoss() {
        // Constructor vacío requerido para Firebase
    }

    public Productoss(String id, String nombreProducto, String cantidad, List<String> imageUrls) {
        this.id = id;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.imageUrls = imageUrls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}