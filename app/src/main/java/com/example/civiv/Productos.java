package com.example.civiv;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Productos {
    private String name;
    private String cantidad;
    private String imagen;

    public Productos() {
    }

    public String getName() {
        return name;
    }

    public String getCantidad() {
        return cantidad;
    }

    public String getImagen() {
        return imagen;
    }

    public Productos(String name, String cantidad, String imagen) {
        this.name = name;
        this.cantidad = cantidad;
        this.imagen = imagen;
    }
}
