package com.example.civiv;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Capturar extends AppCompatActivity {

    private Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturar);

        // Nombre de tu modelo en la carpeta assets
        String modelName = "modelo_prueba.tflite";

        try {
            // Cargar el modelo TensorFlow Lite desde los recursos de la aplicación
            tflite = new Interpreter(loadModelFile(modelName));

            // Ahora puedes utilizar 'tflite' para realizar inferencias
            // Ejemplo: tflite.run(input, output);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar el modelo", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para cargar el archivo del modelo desde la carpeta assets
    private MappedByteBuffer loadModelFile(String modelFilename) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(getAssets().openFd(modelFilename).getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = getAssets().openFd(modelFilename).getStartOffset();
        long declaredLength = getAssets().openFd(modelFilename).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Asegúrate de liberar recursos cuando tu actividad se destruya
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
    }
}
