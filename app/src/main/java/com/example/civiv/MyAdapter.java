package com.example.civiv;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        if (context != null && !((Activity) context).isDestroyed()) {
            Glide.with(context).clear(holder.imagen);
        }
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
                    .load(productoss.getImageUrls().get(0))
                    .apply(new RequestOptions().override(100, 100))
                    .into(holder.imagen);
        } else {
            holder.imagen.setImageResource(R.drawable.civiv2);
        }

        // Establecer el icono de eliminación
        holder.btnDelete.setImageResource(R.drawable.delete);

        // Manejar el clic en el icono de eliminación
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar Producto")
                    .setMessage("¿Estás seguro de que deseas eliminar este producto?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        String productId = productoss.getId();
                        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("productos").child(userId).child(productId);
                        productRef.child("eliminado").setValue(1).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                int adapterPosition = holder.getAdapterPosition();
                                if (adapterPosition != RecyclerView.NO_POSITION) {
                                    list.remove(adapterPosition);
                                    notifyItemRemoved(adapterPosition);
                                    Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "Error al eliminar producto", Toast.LENGTH_SHORT).show();
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
        TextView product, cantidad;
        ImageView imagen, btnDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            product = itemView.findViewById(R.id.textProduct);
            cantidad = itemView.findViewById(R.id.textCantidad);
            imagen = itemView.findViewById(R.id.imagenProducto);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private void setBoldText(TextView textView, String label, String value) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(label + "\n" + value);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }
}
