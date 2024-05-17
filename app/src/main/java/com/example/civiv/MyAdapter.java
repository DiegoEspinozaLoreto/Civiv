package com.example.civiv;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    Context context;
    ArrayList<Productoss> list;

    String userId;

    public MyAdapter(Context context, ArrayList<Productoss> list, String userId) {
        this.context = context;
        this.list = list;
        this.userId = userId;
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(context).clear(holder.imagen);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.productentry, parent, false);
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


        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar Producto")
                    .setMessage("¿Estás seguro de que deseas eliminar este producto?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("productos")
                                .child(productoss.getUserId()).child(productoss.getId());
                        productRef.child("eliminado").setValue(1)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Producto eliminado correctamente.", Toast.LENGTH_SHORT).show();
                                        list.remove(position);
                                        notifyItemRemoved(position);
                                    } else {
                                        Toast.makeText(context, "Error al eliminar producto.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView product, cantidad, id;
        ImageView imagen, btnDelete; // Agregar ImageView



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            product = itemView.findViewById(R.id.textProduct);
            cantidad = itemView.findViewById(R.id.textCantidad);
            imagen = itemView.findViewById(R.id.imagenProducto);
            btnDelete = itemView.findViewById(R.id.btnDelete);// Asegúrate de que este ID corresponde al ImageView en tu layout XML
        }
    }


    private void setBoldText(TextView textView, String label, String value) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(label + "\n" + value);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }


    private void markProductAsDeleted(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("productos").child(userId).child(productId);
        productRef.child("eliminado").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Producto marcado como eliminado.", Toast.LENGTH_SHORT).show();
                    // Actualizar la lista y notificar el cambio
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getId().equals(productId)) {
                            list.remove(i);
                            notifyItemRemoved(i);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(context, "Error al marcar producto como eliminado.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}



