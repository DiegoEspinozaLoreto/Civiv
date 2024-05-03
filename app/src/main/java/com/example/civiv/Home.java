package com.example.civiv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity implements View.OnClickListener {
        public ImageButton back;
        public Button train;

        @Override

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.home_page);

            back = (ImageButton) findViewById(R.id.regresarMenuButton);
            back.setOnClickListener(this);

            train = (Button) findViewById(R.id.trainingButton);
            train.setOnClickListener(this::onClick);
        }

    @Override
    public void onClick(View v) {

            if (v.getId()== R.id.regresarMenuButton){
                Intent transicion = new Intent(Home.this, Login.class);
                startActivity(transicion);
            }
            if(v.getId() == R.id.trainingButton){
                Intent transicion = new Intent(Home.this,Training.class);
                startActivity(transicion);
            }

    }
}