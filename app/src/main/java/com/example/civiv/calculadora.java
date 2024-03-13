package com.example.civiv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class calculadora extends AppCompatActivity implements View.OnClickListener {
    public ImageButton btn_regresar;

    private EditText Num1,Num2;
    private TextView resultado_txtview;
    private Button suma_btn;
    private Button resta_btn;
    private Button mult_btn;
    private Button div_btn;

    private VideoView moai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculadora);

        btn_regresar = (ImageButton) findViewById(R.id.regresarMenuButton2);
        btn_regresar.setOnClickListener(this);

        Num1= (EditText) findViewById(R.id.num1);
        Num2= (EditText) findViewById(R.id.num_2);
        resultado_txtview = (TextView) findViewById(R.id.resultado);
        suma_btn = (Button) findViewById(R.id.suma);
        resta_btn = (Button) findViewById(R.id.resta);
        mult_btn = (Button) findViewById(R.id.mult);
        div_btn = (Button) findViewById(R.id.div);

        suma_btn.setOnClickListener(this);
        resta_btn.setOnClickListener(this);
        mult_btn.setOnClickListener(this);
        div_btn.setOnClickListener(this);

        moai = (VideoView) findViewById(R.id.videoView);

        String videopath = "android.resource://"+getPackageName()+'/'+ R.raw.moai;
        Uri uri = Uri.parse(videopath);
        moai.setVideoURI(uri);

        MediaController mediaController = new MediaController(this);

        moai.setMediaController(mediaController);
        mediaController.setAnchorView(moai);

        moai.start();

        }
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.regresarMenuButton2) {
            Intent transicion = new Intent(calculadora.this, Home.class);
            startActivity(transicion);
        } else {
            Button btn = (Button) v;
            String operador = btn.getText().toString();
            realizarOperacion(operador);
        }
//
    }
    private void realizarOperacion(String operador) {
        String n1 = Num1.getText().toString();
        String n2 = Num2.getText().toString();

        if (!n1.isEmpty() && !n2.isEmpty()) {
            double numero1 = Double.parseDouble(n1);
            double numero2 = Double.parseDouble(n2);
            double resultado = 0;

            switch (operador) {
                case "+":
                    resultado = numero1 + numero2;
                    break;

                case "-":
                    resultado = numero1 - numero2;
                    break;

                case "*":
                    resultado = numero1 * numero2;
                    break;

                case "/":
                    if (numero2 != 0) {
                        resultado = numero1 / numero2;
                    } else {
                        resultado_txtview.setText("Error: División por cero");
                        return;
                    }
                    break;
            }

            resultado_txtview.setText("El resultado es: " + resultado);
        } else {
            resultado_txtview.setText("Por favor, ingresa ambos números");
        }

    }
}
