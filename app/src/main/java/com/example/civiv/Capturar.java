package com.example.civiv;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Capturar extends AppCompatActivity {
    Bitmap bitmap;
    Yolo8TFLiteDetector yolo8TFLiteDetector;

    Paint boxPaint = new Paint();
    Paint textPaint = new Paint();


    public Button capturarBtn;
    public Button cargarBtn;

    public ImageView Imagen;

    public ImageButton back;
    Toolbar toolbar;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturar);

        capturarBtn = (Button) findViewById(R.id.capturarButton);
        cargarBtn = (Button) findViewById(R.id.cargarButton);
        Imagen = (ImageView) findViewById(R.id.imageView);

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
                startActivityForResult(intent,10);
            }
        });


        capturarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap!=null){
                    ArrayList<Recognition>recognitions = yolo8TFLiteDetector.detect(bitmap);
                    Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
                    Canvas canvas = new Canvas(mutableBitmap);
                    int contmouse =0;
                    for (Recognition recognition: recognitions){
                        System.out.println(recognition);
                        if(recognition.getConfidence()>0.1){
                            RectF location = recognition.getLocation();
                            canvas.drawRect(location,boxPaint);
                            canvas.drawText(recognition.getLabelName()+":"+recognition.getConfidence(),location.left,location.top,textPaint);
                        }
                    }
                    System.out.println("mouse negro: "+contmouse);
                    Imagen.setImageBitmap(mutableBitmap);
                }else {
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
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 10){
            if(data!=null){
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
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
