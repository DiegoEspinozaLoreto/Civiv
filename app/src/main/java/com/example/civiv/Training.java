package com.example.civiv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


import androidx.appcompat.app.AppCompatActivity;



public class Training extends AppCompatActivity implements View.OnClickListener {

    private View v;
    public ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_view);
        back = (ImageButton) findViewById(R.id.regresarHomeButton);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId()== R.id.regresarHomeButton){
            Intent transicion = new Intent(Training.this, Home.class);
            startActivity(transicion);
        }

    }
}
