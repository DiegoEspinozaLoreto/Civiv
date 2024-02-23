package com.example.civiv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity implements View.OnClickListener {
        public ImageButton b;
        @Override

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.home_page);

            b = (ImageButton) findViewById(R.id.regresarMenuButton);
            b.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent transicion = new Intent(Home.this, Login.class);
        startActivity(transicion);
    }
}