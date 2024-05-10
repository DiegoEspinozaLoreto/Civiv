package com.example.civiv;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity implements View.OnClickListener {
        public ImageButton b;
        public ImageButton calc;

        public Button btnProducto;
        @SuppressLint("MissingInflatedId")
        @Override

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.home_page);

            b = (ImageButton) findViewById(R.id.regresarMenuButton);
            b.setOnClickListener(this);
            calc = (ImageButton) findViewById(R.id.btn_calc1);
            calc.setOnClickListener(this);
            btnProducto = (Button) findViewById(R.id.btnProductos);
            btnProducto.setOnClickListener(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                ((Window) window).addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.dots_background));
            }

    }

    @Override
    public void onClick(View v) {

            if (v.getId()== R.id.regresarMenuButton){
                Intent transicion = new Intent(Home.this, Login.class);
                startActivity(transicion);

            }

            if  (v.getId()== R.id.btn_calc1){
                Intent transicion_calc = new Intent(Home.this, calculadora.class);
                startActivity(transicion_calc);

            }

            if(v.getId()== R.id.btnProductos){
                Intent transicion = new Intent(Home.this, InsertarProductos.class);
                startActivity(transicion);

            }
    }
}