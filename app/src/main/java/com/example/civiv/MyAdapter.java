package com.example.civiv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    Context context;
    ArrayList<Productoss> list;
    public MyAdapter(Context context, ArrayList<Productoss> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.productentry,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Productoss productoss = list.get(position);
        holder.product.setText(productoss.getNombreProducto());
        holder.cantidad.setText(productoss.getCantidad());
        holder.id.setText(productoss.getId());

        // Cargar la primera imagen disponible para el producto, si existe
        if (productoss.getImageUrls() != null && !productoss.getImageUrls().isEmpty()) {
            Glide.with(context)
                    .load(productoss.getImageUrls().get(0)) // Asumimos que al menos una imagen está disponible
                    .centerCrop() // Esta opción ajusta la imagen en el ImageView
                    .into(holder.imagen);
        } else {
            // Opcionalmente puedes poner una imagen predeterminada si no hay imágenes
            holder.imagen.setImageResource(R.drawable.civiv2); // Coloca aquí tu imagen predeterminada
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView product, cantidad, id;
        ImageView imagen; // Agregar ImageView

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            product = itemView.findViewById(R.id.textproduct);
            cantidad = itemView.findViewById(R.id.textCantidad);
            id = itemView.findViewById(R.id.textID);
            imagen = itemView.findViewById(R.id.imagenProducto); // Asegúrate de que este ID corresponde al ImageView en tu layout XML
        }
    }

}
