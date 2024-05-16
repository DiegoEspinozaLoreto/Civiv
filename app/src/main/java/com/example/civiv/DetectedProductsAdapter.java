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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DetectedProductsAdapter extends RecyclerView.Adapter<DetectedProductsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Productoss> list;
    DatabaseReference databaseProductos;
    String userId;

    public DetectedProductsAdapter(Context context, ArrayList<Productoss> list, String userId) {
        this.context = context;
        this.list = list;
        this.userId = userId;
        this.databaseProductos = FirebaseDatabase.getInstance().getReference("productos").child(userId);
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

        // Buscar y cargar la URL de la imagen
        findImageByName(productoss.getNombreProducto(), holder.imagen);
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

    private void findImageByName(String productName, ImageView imageView) {
        databaseProductos.orderByChild("nombreProducto").equalTo(productName)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (com.google.firebase.database.DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                                if (productSnapshot.child("imageUrls").exists()) {
                                    for (DataSnapshot imageUrlSnapshot : productSnapshot.child("imageUrls").getChildren()) {
                                        String imageUrl = imageUrlSnapshot.getValue(String.class);
                                        if (imageUrl != null) {
                                            Glide.with(context)
                                                    .load(imageUrl)
                                                    .apply(new RequestOptions().override(100, 100))
                                                    .into(imageView);
                                            return; // Cargar la primera imagen encontrada
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                        Toast.makeText(context, "Error al buscar imagen: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
