package com.example.civiv;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registro extends AppCompatActivity implements View.OnClickListener {
    private Button b;

    private EditText editUser;
    private EditText editPassword;

    private FirebaseAuth mAuth;
    public ImageButton buttonRegresar;
    Toolbar toolbar;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        b = findViewById(R.id.RegistroButton);
        b.setOnClickListener(this);
        editUser = findViewById(R.id.editTextMail);
        editPassword = findViewById(R.id.editTextPasssword);
        mAuth = FirebaseAuth.getInstance();
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



    @Override
    public void onClick(View v) {
        String user = editUser.getText().toString();
        String password = editPassword.getText().toString();
        if(!user.isEmpty() && !password.isEmpty()){
            if(password.length() >=6 ){
                mAuth.createUserWithEmailAndPassword(user, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Registro exitoso, iniciar sesión automáticamente
                                    Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                    Intent transicion = new Intent(Registro.this, Home.class);
                                    startActivity(transicion);
                                } else {
                                    // Si el registro falla, mostrar un mensaje de error
                                    Toast.makeText(Registro.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else {
                Toast.makeText(Registro.this, "La contraseña debe contener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(Registro.this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Finaliza la actividad y regresa
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}