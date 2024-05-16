package com.example.civiv;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Productoss implements Parcelable {

    private String id;
    private String nombreProducto;
    private String cantidad;
    private List<String> imageUrls;
    private String userId;

    public Productoss() {
        // Constructor vacío requerido para Firebase
    }

    public Productoss(String id, String nombreProducto, String cantidad, List<String> imageUrls, String userId) {
        this.id = id;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.imageUrls = imageUrls;
        this.userId = userId;
    }

    protected Productoss(Parcel in) {
        id = in.readString();
        nombreProducto = in.readString();
        cantidad = in.readString();
        imageUrls = in.createStringArrayList();
        userId = in.readString();
    }

    public static final Creator<Productoss> CREATOR = new Creator<Productoss>() {
        @Override
        public Productoss createFromParcel(Parcel in) {
            return new Productoss(in);
        }

        @Override
        public Productoss[] newArray(int size) {
            return new Productoss[size];
        }
    };

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombreProducto);
        dest.writeString(cantidad);
        dest.writeStringList(imageUrls);
        dest.writeString(userId);
    }
}
