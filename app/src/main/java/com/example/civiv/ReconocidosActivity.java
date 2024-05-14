package com.example.civiv;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReconocidosActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button btnActualizar, btnCancelar;
    ReconocidosAdapter adapter;
    ArrayList<Productoss> list;
    DatabaseReference databaseProductos;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconocidos);

        recyclerView = findViewById(R.id.recyclerView);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnCancelar = findViewById(R.id.btnCancelar);

        list = (ArrayList<Productoss>) getIntent().getSerializableExtra("productosReconocidos");

        adapter = new ReconocidosAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
        databaseProductos = FirebaseDatabase.getInstance().getReference("productos");

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProductQuantities();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Close the activity
            }
        });
    }

    private void updateProductQuantities() {
        final int[] updatesRemaining = {list.size()};
        for (Productoss product : list) {
            findProductIdByName(product.getNombreProducto(), product.getCantidad(), updatesRemaining);
        }
    }

    private void findProductIdByName(String productName, String quantity, int[] updatesRemaining) {
        databaseProductos.child(userId).orderByChild("nombreProducto").equalTo(productName)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (com.google.firebase.database.DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                                String productId = productSnapshot.getKey();
                                updateProductQuantity(productId, quantity, updatesRemaining);
                            }
                        } else {
                            Toast.makeText(ReconocidosActivity.this, "Producto no encontrado: " + productName, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                        Toast.makeText(ReconocidosActivity.this, "Error al buscar producto: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProductQuantity(String productId, String quantity, int[] updatesRemaining) {
        databaseProductos.child(userId).child(productId).child("cantidad").setValue(quantity)
                .addOnCompleteListener(task -> {
                    updatesRemaining[0]--;
                    if (updatesRemaining[0] == 0) {
                        Toast.makeText(ReconocidosActivity.this, "Base de datos actualizada", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
