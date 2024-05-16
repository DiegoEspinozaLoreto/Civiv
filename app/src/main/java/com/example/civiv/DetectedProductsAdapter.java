package com.example.civiv;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class DetectedProductsAdapter extends RecyclerView.Adapter<DetectedProductsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Productoss> list;

    public DetectedProductsAdapter(Context context, ArrayList<Productoss> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        super.onViewRecycled(holder);
        if (!((Activity) context).isDestroyed()) {
            Glide.with(context).clear(holder.imagen);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.productentry_detected, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Productoss productoss = list.get(position);
        setBoldText(holder.product, "Producto:", productoss.getNombreProducto());
        setBoldText(holder.cantidad, "Cantidad:", productoss.getCantidad());

        // Cargar la primera imagen disponible para el producto, si existe
        if (productoss.getImageUrls() != null && !productoss.getImageUrls().isEmpty()) {
            Glide.with(context)
                    .load(productoss.getImageUrls().get(0)) // Asumimos que al menos una imagen está disponible
                    .apply(new RequestOptions().override(100, 100)) // Ajusta el tamaño según tus necesidades
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView product, cantidad;
        ImageView imagen; // Agregar ImageView

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            product = itemView.findViewById(R.id.textProduct);
            cantidad = itemView.findViewById(R.id.textCantidad);
            imagen = itemView.findViewById(R.id.imagenProducto); // Asegúrate de que este ID corresponde al ImageView en tu layout XML
        }
    }

    private void setBoldText(TextView textView, String label, String value) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(label + "\n" + value);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }
}