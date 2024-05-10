package com.example.civiv;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.civiv.ml.ModeloPrueba;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Capturar extends AppCompatActivity {
    Bitmap bitmap;

    public Button capturarBtn;
    public Button cargarBtn;

    public ImageView Imagen;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturar);

        capturarBtn = (Button) findViewById(R.id.capturarButton);
        cargarBtn = (Button) findViewById(R.id.cargarButton);
        Imagen = (ImageView) findViewById(R.id.imageView);


        cargarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Capturar.this, "Le picó a cargar", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });
        capturarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ModeloPrueba model = ModeloPrueba.newInstance(Capturar.this);
                    if (bitmap !=null){
                        bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                        // Creates inputs for reference.
                        TensorImage image = TensorImage.fromBitmap(bitmap);

                        // Runs model inference and gets result.
                        ModeloPrueba.Outputs outputs = model.process(image);
                        TensorBuffer locations = outputs.getLocationsAsTensorBuffer();
                        TensorBuffer classes = outputs.getClassesAsTensorBuffer();
                        TensorBuffer scores = outputs.getScoresAsTensorBuffer();
                        TensorBuffer numberOfDetections = outputs.getNumberOfDetectionsAsTensorBuffer();

                        // Releases model resources if no longer used.
                        model.close();
                        Toast.makeText(Capturar.this, "Si jaló: ", Toast.LENGTH_SHORT).show();
                        System.out.println(getMax(classes.getFloatArray())+" ");
                    }
                    else{
                        Toast.makeText(Capturar.this, "JOTO", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    Toast.makeText(Capturar.this, "No jaló: "+e, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    int getMax(float[]arr){
        int max = 0;
        for (int i =0; i < arr.length;i++){
            if (arr[i]>arr[max]) max = i;
        }
        return max;
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
}
