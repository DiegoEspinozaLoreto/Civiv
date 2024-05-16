package com.example.civiv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReconocidosActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Productoss> list;
    ReconocidosAdapter adapter;
    Button btnUpdate, btnCancel;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconocidos);

        recyclerView = findViewById(R.id.recyclerViewReconocidos);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);

        list = getIntent().getParcelableArrayListExtra("productosReconocidos");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReconocidosAdapter(this, list);
        recyclerView.setAdapter(adapter);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabase();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                            String productId = dataSnapshot.getKey();
                            databaseReference.child(productId).child("cantidad").setValue(producto.getCantidad());
                        }
                        Toast.makeText(ReconocidosActivity.this, "Base de datos actualizada.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }

        // Return to Capturar activity
        Intent intent = new Intent(ReconocidosActivity.this, Capturar.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
