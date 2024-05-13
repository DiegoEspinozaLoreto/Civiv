package com.example.civiv;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.civiv.R;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private ImageButton back;
    private Button train;
    private Button capturar;
    private ImageButton b;
    private ImageButton calc;
    private Button btnProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // Configuración del botón de regreso
        back = findViewById(R.id.regresarMenuButton);
        back.setOnClickListener(this);

        // Configuración de los botones de entrenar y capturar
        train = findViewById(R.id.trainingButton);
        train.setOnClickListener(this);

        capturar = findViewById(R.id.capturarButton);
        capturar.setOnClickListener(this);

        // Configuración del botón de productos
        btnProducto = findViewById(R.id.btnProductos);
        btnProducto.setOnClickListener(this);

        // Configuración del botón de los tres puntos
        b = findViewById(R.id.popUpButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.dots_background));
        }
    }

    @Override
    public void onClick(View v) {
        Intent transicion;
        if (v.getId() == R.id.regresarMenuButton) {
            transicion = new Intent(Home.this, Login.class);
            startActivity(transicion);
        } else if (v.getId() == R.id.trainingButton) {
            transicion = new Intent(Home.this, Training.class);
            startActivity(transicion);
        } else if (v.getId() == R.id.capturarButton) {
            transicion = new Intent(Home.this, Capturar.class);
            startActivity(transicion);
        } else if (v.getId() == R.id.btnProductos) {
            transicion = new Intent(Home.this, InsertarProductos.class);
            startActivity(transicion);
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
                    Toast.makeText(Home.this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Home.this, Login.class);
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
}
