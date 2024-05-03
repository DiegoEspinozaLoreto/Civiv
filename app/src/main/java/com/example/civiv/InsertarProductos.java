package com.example.civiv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertarProductos extends AppCompatActivity {
    Button btnInsert, btnView;
    EditText Producto, Cantidad, Imagen;
    DatabaseReference databaseProductos;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_datos_lista);
        btnInsert = findViewById(R.id.btninsert);
        btnView = findViewById(R.id.btnview);
        Producto = findViewById(R.id.editProducto);
        Cantidad = findViewById(R.id.editCantidad);
        Imagen = findViewById(R.id.editImagen);
        databaseProductos = FirebaseDatabase.getInstance().getReference();

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertData();
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InsertarProductos.this, ProductoList.class));
                finish();
            }
        });
    }

    private void InsertData() {
        String productosProducto = Producto.getText().toString();
        String productosCantidad = Cantidad.getText().toString();
        String productosImagen = Imagen.getText().toString();
        String id = databaseProductos.push().getKey();

        Productos productos = new Productos(productosProducto,productosCantidad,productosImagen);
        databaseProductos.child("productos").child(id).setValue(productos)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(InsertarProductos.this, "Producto insertado",Toast.LENGTH_SHORT);
                        }
                    }
                });

    }
}
