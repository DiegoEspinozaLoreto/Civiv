package com.example.civiv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userId;


    Toolbar toolbar;



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
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        adapter = new DetectedProductsAdapter(this, list, userId);
        recyclerView.setAdapter(adapter);

        toolbar = findViewById(R.id.toolbar2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            ((Window) window).addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.dots_background));
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }

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

    private void updateDatabase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("productos").child(userId);

        // Mantener un contador de actualizaciones pendientes
        final int[] updatesRemaining = {list.size()};

        for (Productoss producto : list) {
            findProductIdByNameAndUpdate(producto.getNombreProducto(), producto.getCantidad(), updatesRemaining);
        }
    }

    private void findProductIdByNameAndUpdate(String productName, String cantidad, int[] updatesRemaining) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseProductos = FirebaseDatabase.getInstance().getReference("productos").child(userId);

        databaseProductos.orderByChild("nombreProducto").equalTo(productName)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (com.google.firebase.database.DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                                String productId = productSnapshot.getKey();
                                databaseProductos.child(productId).child("cantidad").setValue(cantidad)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                // Decrementar el contador de actualizaciones pendientes
                                                updatesRemaining[0]--;
                                                if (updatesRemaining[0] == 0) {
                                                    Toast.makeText(DetectedProductsActivity.this, "Base de datos actualizada.", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            } else {
                                                Toast.makeText(DetectedProductsActivity.this, "Error al actualizar producto: " + productName, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            updatesRemaining[0]--;
                            if (updatesRemaining[0] == 0) {
                                Toast.makeText(DetectedProductsActivity.this, "Base de datos actualizada.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                        Toast.makeText(DetectedProductsActivity.this, "Error al buscar producto: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Finaliza la actividad y regresa
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

