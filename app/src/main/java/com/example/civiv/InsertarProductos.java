package com.example.civiv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;

public class InsertarProductos extends AppCompatActivity {
    Button btnInsert, btnView, btnSeleccionarImagen;
    EditText Producto, Cantidad, Imagen;

    ImageView ImagenPreview;
    DatabaseReference databaseProductos;

    StorageReference  storageReference;

    Uri image;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
            public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == RESULT_OK) {
               if (result.getData() != null) {
                   image = result.getData().getData();
                   btnInsert.setEnabled(true);
                   Glide.with(getApplicationContext()).load(image).into(ImagenPreview);

               } else {
                   Toast.makeText(InsertarProductos.this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
               }

            }

            }
    });
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_datos_lista);
        btnInsert = findViewById(R.id.btninsert);
        btnView = findViewById(R.id.btnview);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        ImagenPreview = findViewById(R.id.ImagenPreview);
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

        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
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

    private void selectImage() {

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
                            Toast.makeText(InsertarProductos.this, "Producto insertado",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
