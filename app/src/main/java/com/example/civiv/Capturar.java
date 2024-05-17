package com.example.civiv;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.camera2.CameraManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import java.util.HashMap;

public class Capturar extends AppCompatActivity {
    Bitmap bitmap;
    Yolo8TFLiteDetector yolo8TFLiteDetector;
    Paint boxPaint = new Paint();
    Paint textPaint = new Paint();
    TextureView textureView;
    CameraManager cameraManager;
    Handler handler;
    CameraDevice cameraDevce;
    ArrayList<Recognition> recognitions;

    public Button capturarBtn;
    public Button cargarBtn;
    public ImageButton camaraBtn;
    public ImageView Imagen;
    Surface surface;



    public ImageButton popUp;
    Toolbar toolbar;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturar);

        capturarBtn = (Button) findViewById(R.id.capturarButton);
        cargarBtn = (Button) findViewById(R.id.cargarButton);
        camaraBtn = (ImageButton)findViewById(R.id.camaraBtn);
        Imagen = (ImageView) findViewById(R.id.imageView);
        Imagen.setImageResource(R.drawable.frame_camara);

        get_permission();
        HandlerThread handlerThread = new HandlerThread("videoThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        textureView = (TextureView) findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                // Handle surface texture available
                System.out.println("available");
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                // Handle surface texture size changed
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                // Handle surface texture destroyed
                System.out.println("destroyed");
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
                // Handle surface texture updated
                if (cameraDevce!=null){
                    System.out.println("updated");
                    bitmap =textureView.getBitmap();
                    Imagen.setImageBitmap(bitmap);
                    predecir();
                }

            }
        });

        cameraManager =(CameraManager) getSystemService(Context.CAMERA_SERVICE);


        yolo8TFLiteDetector = new Yolo8TFLiteDetector();
        yolo8TFLiteDetector.setModelFile("yolov5best-fp16.tflite");
        yolo8TFLiteDetector.initialModel(this);
        yolo8TFLiteDetector.addGPUDelegate();

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


        camaraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraDevce==null){
                    try {
                        textureView.setVisibility(View.VISIBLE);
                        open_camera();
                    } catch (CameraAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    cameraDevce.close();
                    cameraDevce=null;
                    textureView.setVisibility(View.GONE);
                }
            }
        });
        cargarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraDevce!=null){
                    cameraDevce.close();
                    cameraDevce=null;
                    textureView.setVisibility(View.GONE);
                }
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        capturarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraDevce!=null){
                    cameraDevce.close();
                    cameraDevce=null;
                    textureView.setVisibility(View.GONE);
                }
                if(recognitions!=null){recognitions.clear();}
                predecir();
                if (bitmap!=null){
                    contar();
                }else{
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

    private void contar() {
        HashMap<String,Productoss> detectedProductsMap = new HashMap<>();
        for (Recognition recognition : recognitions) {
            if (recognition.getConfidence() > 0.6) {
                String nombreProducto = recognition.getLabelName();
                if (detectedProductsMap.containsKey(nombreProducto)) {
                    Objects.requireNonNull(detectedProductsMap.get(nombreProducto)).incrementarCantidad(1);
                } else {
                    detectedProductsMap.put(nombreProducto, new Productoss("", nombreProducto, "1", null, ""));
                }
            }
        }
        // Agregar o actualizar el producto en el mapa
        // Convertir el mapa a una lista
        ArrayList<Productoss> detectedProducts = new ArrayList<>(detectedProductsMap.values());

        // Iniciar DetectedProductsActivity con la lista de productos detectados
        Intent intent = new Intent(Capturar.this, DetectedProductsActivity.class);
        intent.putParcelableArrayListExtra("detectedProducts", detectedProducts);
        startActivity(intent);
    }

    private void predecir() {
        if(bitmap!=null){
            recognitions = yolo8TFLiteDetector.detect(bitmap);
            Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
            Canvas canvas = new Canvas(mutableBitmap);
            int contmouse =0;
            for (Recognition recognition: recognitions){
                System.out.println(recognition);
                if(recognition.getConfidence()>0.6){
                    RectF location = recognition.getLocation();
                    canvas.drawRect(location,boxPaint);
                    canvas.drawText(recognition.getLabelName()+":"+recognition.getConfidence(),location.left,location.top,textPaint);
                }
            }
            System.out.println("mouse negro: "+contmouse);
            Imagen.setImageBitmap(mutableBitmap);
        }
    }

    @SuppressLint("MissingPermission")
    private void open_camera() throws CameraAccessException {
        String[] cameraIdList = cameraManager.getCameraIdList();
        cameraManager.openCamera(cameraIdList[0], new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice cameraDevice) {
                // Camera device is opened, you can start using it
                cameraDevce = cameraDevice;
                SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
                surface = new Surface(surfaceTexture);

                CaptureRequest.Builder captureRequest = null;
                try {
                    captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                } catch (CameraAccessException e) {
                    throw new RuntimeException(e);
                }
                captureRequest.addTarget(surface);
                try {
                    CaptureRequest.Builder finalCaptureRequest = captureRequest;
                    cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            // The camera is ready to capture frames
                            try {
                                session.setRepeatingRequest(finalCaptureRequest.build(),null,null);
                            } catch (CameraAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            // The camera failed to configure the capture session
                        }
                    },handler);
                } catch (CameraAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                // Camera device is disconnected, you should release it
                Toast.makeText(Capturar.this, "disconnect", Toast.LENGTH_SHORT).show();
                surface.release();

            }

            @Override
            public void onError(@NonNull CameraDevice cameraDevice, int error) {
                // An error occurred while opening the camera
                surface.release();
            }
        }, handler);

    }

    private void get_permission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA},101);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            get_permission();
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
                if (cameraDevce!=null){cameraDevce.close();}
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    };
