package com.example.civiv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetectedProductsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Productoss> list;
    DetectedProductsAdapter adapter;
    Button btnUpdate, btnCancel;

    DatabaseReference databaseProductos;

    StorageReference storageReference;

    FirebaseAuth firebaseAuth;

    FirebaseUser user;

    String userId;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detected_products);

        recyclerView = findViewById(R.id.recyclerViewDetectedProducts);
        btnUpdate = findViewById(R.id.buttonUpdate);
        btnCancel = findViewById(R.id.buttonCancel);

        // Obtener la lista de productos detectados del intent
        list = getIntent().getParcelableArrayListExtra("detectedProducts");
        if (list == null) {
            list = new ArrayList<>();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetectedProductsAdapter(this, list);
        recyclerView.setAdapter(adapter);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabase();
                finish();
            }

        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetectedProductsActivity.this, "Operación cancelada", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void findProductIdAndImageByName(String productName, String quantity, int[] updatesRemaining, Productoss product) {
        databaseProductos.child(userId).orderByChild("nombreProducto").equalTo(productName)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (com.google.firebase.database.DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                                String productId = productSnapshot.getKey();
                                ArrayList<String> imageUrls = new ArrayList<>();
                                if (productSnapshot.child("imageUrls").exists()) {
                                    for (DataSnapshot imageUrlSnapshot : productSnapshot.child("imageUrls").getChildren()) {
                                        imageUrls.add(imageUrlSnapshot.getValue(String.class));
                                    }
                                }
                                product.setId(productId);
                                product.setImageUrls(imageUrls);
                                updateProductQuantity(productId, quantity, updatesRemaining);
                            }
                        } else {
                            Toast.makeText(DetectedProductsActivity.this, "Producto no encontrado: " + productName, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                        Toast.makeText(DetectedProductsActivity.this, "Error al buscar producto: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void updateDatabase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("productos").child(userId);

        int[] remainingUpdates = {list.size()};
        ArrayList<Productoss> updatedProducts = new ArrayList<>();

        for (Productoss producto : list) {
            databaseReference.orderByChild("nombreProducto").equalTo(producto.getNombreProducto()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String productId = dataSnapshot.getKey();
                            Log.d("updateDatabase", "Producto encontrado: " + producto.getNombreProducto() + ", ID: " + productId);
                            databaseReference.child(productId).child("cantidad").setValue(producto.getCantidad());

                            // Obtener y establecer la URL de la imagen
                            ArrayList<String> imageUrls = new ArrayList<>();
                            for (DataSnapshot imageSnapshot : dataSnapshot.child("imageUrls").getChildren()) {
                                String imageUrl = imageSnapshot.getValue(String.class);
                                imageUrls.add(imageUrl);
                            }
                            producto.setImageUrls(imageUrls);
                            updatedProducts.add(producto);
                            Log.d("updateDatabase", "URL de la imagen: " + imageUrls.toString());
                        }
                    } else {
                        Log.d("updateDatabase", "Producto no encontrado: " + producto.getNombreProducto());
                    }
                    remainingUpdates[0]--;
                    if (remainingUpdates[0] == 0) {
                        Toast.makeText(DetectedProductsActivity.this, "Base de datos actualizada.", Toast.LENGTH_SHORT).show();

                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Manejar el error
                    Log.e("updateDatabase", "Error: " + error.getMessage());
                    remainingUpdates[0]--;
                    if (remainingUpdates[0] == 0) {
                        Toast.makeText(DetectedProductsActivity.this, "Base de datos actualizada.", Toast.LENGTH_SHORT).show();

                        finish();
                    }
                }
            });
        }
    }


}
