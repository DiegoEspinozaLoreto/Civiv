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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class InsertarProductos extends AppCompatActivity {
    Button btnInsert, btnView, btnSeleccionarImagen;
    EditText nombreProducto, Cantidad;
    ImageView ImagenPreview;
    DatabaseReference databaseProductos;
    StorageReference storageReference;
    Uri image;
    List<Uri> productImages = new ArrayList<>();
    int imageCounter = 1;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    image = result.getData().getData();
                    productImages.add(image);
                    Glide.with(getApplicationContext()).load(image).into(ImagenPreview);
                } else {
                    Toast.makeText(InsertarProductos.this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_datos_lista);
        btnInsert = findViewById(R.id.btninsert);
        btnView = findViewById(R.id.btnview);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        ImagenPreview = findViewById(R.id.ImagenPreview);
        nombreProducto = findViewById(R.id.editProducto);
        Cantidad = findViewById(R.id.editCantidad);
        databaseProductos = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        btnInsert.setEnabled(false);
        addNumberInputFilter(Cantidad);
        Cantidad.setInputType(InputType.TYPE_CLASS_NUMBER);

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertData();
                uploadMultipleImages(productImages, nombreProducto.getText().toString());
                clearFields();
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

        nombreProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Verificar si el campo de producto está vacío
                if (TextUtils.isEmpty(s)) {
                    btnSeleccionarImagen.setEnabled(false);
                } else {
                    btnSeleccionarImagen.setEnabled(true);
                }
                // Llamar a la función para verificar todos los campos
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
    }

    private void checkFieldsForEmptyValues() {
        String producto = nombreProducto.getText().toString().trim();
        String cantidad = Cantidad.getText().toString().trim();

        if (producto != null && cantidad != null) {
            if (!TextUtils.isEmpty(producto) && !TextUtils.isEmpty(cantidad) && image != null) {
                btnInsert.setEnabled(true);
            } else {
                btnInsert.setEnabled(false);
            }
        } else {
            btnInsert.setEnabled(false);
        }
    }

    private void clearFields() {
        nombreProducto.setText("");
        Cantidad.setText("");
        ImagenPreview.setImageResource(android.R.color.transparent);
        productImages.clear();
        imageCounter = 1;
    }


    private void uploadMultipleImages(List<Uri> images, String Producto) {
        for (Uri imageUri : images) {
            StorageReference reference = storageReference.child("images/" + Producto + "imagen" + imageCounter);
            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully
                    imageCounter++;
                    Toast.makeText(InsertarProductos.this, "Imagen subida", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle image upload failure
                    Toast.makeText(InsertarProductos.this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private List<String> obtenerNombresDeImagenes(String Producto, int imageCounter) {
        List<String> nombresImagenes = new ArrayList<>();
        for (int i = 1; i <= imageCounter; i++) {
            nombresImagenes.add(Producto + "imagen" + i);
        }
        return nombresImagenes;
    }



    private void InsertData() {
        String productosProducto = nombreProducto.getText().toString();
        String productosCantidad = Cantidad.getText().toString();
        String id = databaseProductos.push().getKey();
        List<String> imagenes = obtenerNombreDeImagenes();

        Productoss productoss = new Productoss(id, productosProducto, productosCantidad, imagenes);
        databaseProductos.child("productos").child(id).setValue(productoss)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(InsertarProductos.this, "Producto insertado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(InsertarProductos.this, "Error al insertar producto", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private List<String> obtenerNombreDeImagenes() {
        List<String> nombresImagenes = new ArrayList<>();
        for (int i = 1; i <= imageCounter; i++) {
            nombresImagenes.add(nombreProducto + "imagen" + i);
        }
        return nombresImagenes;
    }



    private void addNumberInputFilter(EditText editText) {
        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return ""; // Reject the character if it's not a digit
                    }
                }
                return null; // Accept characters only if they are digits
            }
        }});
    }
}

