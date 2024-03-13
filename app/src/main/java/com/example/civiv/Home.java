package com.example.civiv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity implements View.OnClickListener {
        public ImageButton b;
        public ImageButton calc;
        @Override

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.home_page);

            b = (ImageButton) findViewById(R.id.regresarMenuButton);
            b.setOnClickListener(this);
            calc = (ImageButton) findViewById(R.id.btn_calc1);
            calc.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {

            if (v.getId()== R.id.regresarMenuButton){
                Intent transicion = new Intent(Home.this, Login.class);
                startActivity(transicion);
            }else {
                Intent transicion_calc = new Intent(Home.this, calculadora.class);

                startActivity(transicion_calc);
            }


    }
}