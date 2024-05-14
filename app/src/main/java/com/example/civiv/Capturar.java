package com.example.civiv;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference.CompletionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Capturar extends AppCompatActivity {
    Bitmap bitmap;
    Yolo8TFLiteDetector yolo8TFLiteDetector;

    Paint boxPaint = new Paint();
    Paint textPaint = new Paint();

    public Button capturarBtn;
    public Button cargarBtn;

    public ImageView Imagen;

    public ImageButton back;

    DatabaseReference databaseProductos;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturar);

        capturarBtn = findViewById(R.id.capturarButton);
        cargarBtn = findViewById(R.id.cargarButton);
        back = findViewById(R.id.regresarMenuButton);
        Imagen = findViewById(R.id.imageView);

        yolo8TFLiteDetector = new Yolo8TFLiteDetector();
        yolo8TFLiteDetector.setModelFile("yolov5best-fp16.tflite");
        yolo8TFLiteDetector.initialModel(this);

        boxPaint.setStrokeWidth(5);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setColor(Color.RED);

        textPaint.setTextSize(50);
        textPaint.setColor(Color.GREEN);
        textPaint.setStyle(Paint.Style.FILL);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        databaseProductos = FirebaseDatabase.getInstance().getReference("productos");

        cargarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent transicion = new Intent(Capturar.this, Home.class);
                startActivity(transicion);
                finish();
            }
        });

        capturarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    ArrayList<Recognition> recognitions = yolo8TFLiteDetector.detect(bitmap);
                    Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas canvas = new Canvas(mutableBitmap);

                    // Contar productos
                    Map<String, Integer> productCounts = new HashMap<>();
                    for (Recognition recognition : recognitions) {
                        System.out.println(recognition);
                        if (recognition.getConfidence() > 0.1) {
                            String label = recognition.getLabelName();
                            productCounts.put(label, productCounts.getOrDefault(label, 0) + 1);

                            RectF location = recognition.getLocation();
                            canvas.drawRect(location, boxPaint);
                            canvas.drawText(label + ":" + recognition.getConfidence(), location.left, location.top, textPaint);
                        }
                    }

                    // Mostrar el conteo y preguntar al usuario si desea actualizar la base de datos
                    showUpdateDialog(productCounts);

                    Imagen.setImageBitmap(mutableBitmap);
                } else {
                    Toast.makeText(Capturar.this, "Carga una imagen primero", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showUpdateDialog(Map<String, Integer> productCounts) {
        StringBuilder message = new StringBuilder();
        for (Map.Entry<String, Integer> entry : productCounts.entrySet()) {
            message.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Productos reconocidos")
                .setMessage(message.toString())
                .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateProductQuantities(productCounts);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void updateProductQuantities(Map<String, Integer> productCounts) {
        final int[] updatesRemaining = {productCounts.size()};
        for (Map.Entry<String, Integer> entry : productCounts.entrySet()) {
            String productName = entry.getKey();
            int quantity = entry.getValue();

            findProductIdByName(productName, quantity, updatesRemaining);
        }
    }

    private void findProductIdByName(String productName, int quantity, int[] updatesRemaining) {
        databaseProductos.child(userId).orderByChild("nombreProducto").equalTo(productName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                                String productId = productSnapshot.getKey();
                                updateProductQuantity(productId, quantity, updatesRemaining);
                            }
                        } else {
                            Toast.makeText(Capturar.this, "Producto no encontrado: " + productName, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Capturar.this, "Error al buscar producto: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProductQuantity(String productId, int quantity, int[] updatesRemaining) {
        databaseProductos.child(userId).child(productId).child("cantidad").setValue(String.valueOf(quantity))
                .addOnCompleteListener(task -> {
                    updatesRemaining[0]--;
                    if (updatesRemaining[0] == 0) {
                        Toast.makeText(Capturar.this, "Base de datos actualizada", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    Imagen.setImageBitmap(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
