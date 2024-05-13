package com.example.civiv;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class InsertarProductos extends AppCompatActivity {
    Button btnInsert, btnView, btnSeleccionarImagen;
    EditText nombreProducto, Cantidad;
    LinearLayout ImagenPreview;
    DatabaseReference databaseProductos;
    StorageReference storageReference;

    FirebaseAuth firebaseAuth;

    FirebaseUser user;

    String userId;


    Uri image;
    List<Uri> productImages = new ArrayList<>();
    int imageCounter = 1;

    Toolbar toolbar;


    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                ImagenPreview.removeAllViews();
                if (result.getData() != null) {
                    // Verificar si se seleccionaron múltiples imágenes
                    if (result.getData().getClipData() != null) {
                        int count = result.getData().getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri imageUri = result.getData().getClipData().getItemAt(i).getUri();
                            if (imageUri != null) {
                                productImages.add(imageUri);
                            }
                        }
                    } else if (result.getData().getData() != null) {
                        // Si se selecciona una sola imagen
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            productImages.add(imageUri);
                        }
                    }

                    // Actualizar la vista previa de la imagen y verificar los campos vacíos

                    updateImagePreview();
                    checkFieldsForEmptyValues();
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
        toolbar = findViewById(R.id.toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (user != null) {
             userId = user.getUid(); // Este es el ID único del usuario autenticado
        }


        btnInsert.setEnabled(false);
        addNumberInputFilter(Cantidad);
        Cantidad.setInputType(InputType.TYPE_CLASS_NUMBER);
        btnSeleccionarImagen.setEnabled(false);



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
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMultipleImages(productImages, nombreProducto.getText().toString());
            }
        });

        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagenPreview.removeAllViews();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                activityResultLauncher.launch(Intent.createChooser(intent, "Selecciona las imagenes"));
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InsertarProductos.this, ProductoList.class));

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
            if (!TextUtils.isEmpty(producto) && !TextUtils.isEmpty(cantidad) && !productImages.isEmpty()) {
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
        ImagenPreview.removeAllViews();
        productImages.clear();
    }



    private void uploadMultipleImages(List<Uri> images, final String producto) {
        final List<String> imageUrls = new ArrayList<>(Collections.nCopies(images.size(), null));  // Inicializa con nulls
        for (int i = 0; i < images.size(); i++) {
            final int index = i;  // Guarda el índice actual
            final Uri imageUri = images.get(i);
            final StorageReference reference = storageReference.child(userId+"images/" + producto + "_imagen" + UUID.randomUUID().toString());

            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrls.set(index, uri.toString());  // Establece la URL en la posición correcta
                            // Verifica si todas las posiciones están llenas (no null)
                            if (!imageUrls.contains(null)) {
                                InsertData(producto, Cantidad.getText().toString(), imageUrls, userId);
                                clearFields();
                            }
                        }
                    });
                }
            });
        }
    }







    private void InsertData(String nombreProducto, String cantidadProducto, List<String> imageUrls, String userId) {
        String id = databaseProductos.push().getKey();
        Productoss productoss = new Productoss(id, nombreProducto, cantidadProducto, imageUrls, userId);
        databaseProductos.child("productos").child(userId).child(id).setValue(productoss)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(InsertarProductos.this, "Producto insertado correctamente.", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(InsertarProductos.this, "Error al insertar producto.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private List<String> obtenerNombreDeImagenes() {
        List<String> nombresImagenes = new ArrayList<>();
        for (int i = 1; i <= productImages.size(); i++) {
            nombresImagenes.add(nombreProducto.getText().toString() + "imagen" + i);
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

    private void updateImagePreview() {
        // Clear previous images
        ImagenPreview.removeAllViews();

        // Load each selected image into ImageView
        for (Uri imageUri : productImages) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(8, 8, 8, 8);
            imageView.setLayoutParams(layoutParams);
            Glide.with(getApplicationContext()).load(imageUri).into(imageView);
            ImagenPreview.addView(imageView);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Finaliza la actividad y regresa
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

