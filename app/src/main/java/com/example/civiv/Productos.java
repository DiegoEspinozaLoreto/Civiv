package com.example.civiv;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Productos {
    private String name;
    private String descripcion;
    private String imagen;

    public Productos() {
    }

    public String getName() {
        return name;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public Productos(String name, String descripcion, String imagen) {
        this.name = name;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }
}
