package com.example.civiv;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Capturar extends AppCompatActivity {
    Bitmap bitmap;
    Yolo8TFLiteDetector yolo8TFLiteDetector;

    Paint boxPaint = new Paint();
    Paint textPaint = new Paint();

    public Button capturarBtn;
    public Button cargarBtn;
    public ImageView Imagen;
    public ImageButton popUp;
    Toolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturar);

        capturarBtn = findViewById(R.id.capturarButton);
        cargarBtn = findViewById(R.id.cargarButton);
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
        popUp = findViewById(R.id.popUpButton);
        popUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        cargarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        capturarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    ArrayList<Recognition> recognitions = yolo8TFLiteDetector.detect(bitmap);
                    Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas canvas = new Canvas(mutableBitmap);

                    // Crear mapa de productos detectados para agrupar por nombre
                    HashMap<String, Productoss> detectedProductsMap = new HashMap<>();
                    for (Recognition recognition : recognitions) {
                        if (recognition.getConfidence() > 0.1) {
                            RectF location = recognition.getLocation();
                            canvas.drawRect(location, boxPaint);
                            canvas.drawText(recognition.getLabelName() + ":" + recognition.getConfidence(), location.left, location.top, textPaint);

                            // Agregar o actualizar el producto en el mapa
                            String nombreProducto = recognition.getLabelName();
                            if (detectedProductsMap.containsKey(nombreProducto)) {
                                detectedProductsMap.get(nombreProducto).incrementarCantidad(1);
                            } else {
                                detectedProductsMap.put(nombreProducto, new Productoss("", nombreProducto, "1", null, ""));
                            }
                        }
                    }

                    // Convertir el mapa a una lista
                    ArrayList<Productoss> detectedProducts = new ArrayList<>(detectedProductsMap.values());

                    // Iniciar DetectedProductsActivity con la lista de productos detectados
                    Intent intent = new Intent(Capturar.this, DetectedProductsActivity.class);
                    intent.putParcelableArrayListExtra("detectedProducts", detectedProducts);
                    startActivity(intent);

                    Imagen.setImageBitmap(mutableBitmap);
                } else {
                    Toast.makeText(Capturar.this, "Carga una imagen primero", Toast.LENGTH_SHORT).show();
                }
            }
        });


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

        byte[] byteArray = getIntent().getByteArrayExtra("bitmap");
        if (byteArray != null) {
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Imagen.setImageBitmap(bitmap);
        }

    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(Capturar.this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Capturar.this, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                } else {
                    return false;
                }
            }
        });
        popup.show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Finaliza la actividad y regresa
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
