package com.example.civiv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ReconocidosActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Productoss> list;
    MyAdapter adapter;
    String userId;
    ImageView imageView;
    Button btnUpdate, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconocidos);

        recyclerView = findViewById(R.id.recyclerViewReconocidos);
        imageView = findViewById(R.id.imageView);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);

        list = (ArrayList<Productoss>) getIntent().getSerializableExtra("productosReconocidos");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this, list);
        recyclerView.setAdapter(adapter);

        if (!list.isEmpty() && list.get(0).getImageUrls() != null && !list.get(0).getImageUrls().isEmpty()) {
            String imageUrl = list.get(0).getImageUrls().get(0);
            Glide.with(this).load(imageUrl).into(imageView);
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabase();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Regresa a la actividad anterior (Capturar)
            }
        });
    }

    private void updateDatabase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("productos").child(userId);

        for (Productoss producto : list) {
            databaseReference.orderByChild("nombreProducto").equalTo(producto.getNombreProducto()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            dataSnapshot.getRef().child("cantidad").setValue(producto.getCantidad());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Manejar error
                }
            });
        }

        Toast.makeText(ReconocidosActivity.this, "Base de datos actualizada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ReconocidosActivity.this, Capturar.class);
        startActivity(intent);
        finish();
    }
}
