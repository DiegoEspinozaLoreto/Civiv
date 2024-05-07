package com.example.civiv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class InsertarProductos extends AppCompatActivity {
    Button btnInsert, btnView, btnSeleccionarImagen;
    EditText Producto, Cantidad, Imagen;

    TextView avisoCampoVacio;
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
        storageReference = FirebaseStorage.getInstance().getReference();
        avisoCampoVacio = findViewById(R.id.avisoCampoVacio);

        avisoCampoVacio.setVisibility(View.GONE);

        btnInsert.setEnabled(false);

        addNumberInputFilter(Cantidad);
        Cantidad.setInputType(InputType.TYPE_CLASS_NUMBER);









        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertData();
                uploadImage(image);
                clearFields();
                btnInsert.setEnabled(false);
                avisoCampoVacio.setVisibility(View.GONE);
            }


        });

        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InsertarProductos.this, ProductoList.class));
                finish();
            }
        });

        // Verificar campos antes de habilitar el botón de subir
        Producto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFieldsForEmptyValues();

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Cantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFieldsForEmptyValues();

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Imagen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFieldsForEmptyValues();

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });
    }

    // Método para verificar si los campos están vacíos
    private void checkFieldsForEmptyValues() {
        String producto = Producto.getText().toString().trim();
        String cantidad = Cantidad.getText().toString().trim();
        String imagen = Imagen.getText().toString().trim();

        // Verificar que los campos no sean nulos
        if (producto != null && cantidad != null && imagen != null) {
            // Verificar que los campos no estén vacíos
            if (!TextUtils.isEmpty(producto) && !TextUtils.isEmpty(cantidad) && !TextUtils.isEmpty(imagen) && image != null) {
                btnInsert.setEnabled(true);
                avisoCampoVacio.setVisibility(View.GONE);
            } else {
                btnInsert.setEnabled(false);
                avisoCampoVacio.setVisibility(View.VISIBLE);
            }
        } else {
            // Si alguno de los campos es nulo, deshabilitar el botón
            btnInsert.setEnabled(false);
            avisoCampoVacio.setVisibility(View.VISIBLE);
        }
    }

    private void clearFields() {
        Producto.setText("");
        Cantidad.setText("");
        Imagen.setText("");
        ImagenPreview.setImageResource(android.R.color.transparent); // Limpiar la imagen previa
    }



    private void uploadImage(Uri image) {
        StorageReference reference = storageReference.child("images/"+ UUID.randomUUID().toString());
        reference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(InsertarProductos.this, "Imagen subida", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InsertarProductos.this, "Imagen no subida", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                Toast.makeText(InsertarProductos.this, "Subiendo imagen...", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(InsertarProductos.this, "Producto insertado",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void addNumberInputFilter(EditText editText) {
        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return ""; // Rechazar el carácter si no es un número
                    }
                }
                return null; // Aceptar los caracteres solo si son números
            }
        }});
    }
}


