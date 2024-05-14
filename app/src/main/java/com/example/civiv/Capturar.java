package com.example.civiv;

import android.annotation.SuppressLint;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

                    // Crear lista de productos reconocidos
                    ArrayList<Productoss> productosReconocidos = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : productCounts.entrySet()) {
                        Productoss producto = new Productoss();
                        producto.setNombreProducto(entry.getKey());
                        producto.setCantidad(String.valueOf(entry.getValue()));
                        productosReconocidos.add(producto);
                    }

                    // Iniciar ReconocidosActivity con la lista de productos reconocidos
                    Intent intent = new Intent(Capturar.this, ReconocidosActivity.class);
                    intent.putExtra("productosReconocidos", productosReconocidos);
                    startActivity(intent);

                    Imagen.setImageBitmap(mutableBitmap);
                } else {
                    Toast.makeText(Capturar.this, "Carga una imagen primero", Toast.LENGTH_SHORT).show();
                }
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
