package com.example.civiv;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;



public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button b;

    private EditText editUser;
    private EditText editPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);


        b = findViewById(R.id.RegistroButton);
        b.setOnClickListener(this);

        editUser = findViewById(R.id.editTextMail);
        editPassword = findViewById(R.id.editTextPasssword);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        String user = editUser.getText().toString();
        String password = editPassword.getText().toString();

        if(!user.isEmpty() && !password.isEmpty()){
            mAuth.signInWithEmailAndPassword(user, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Inicia la actividad Home si el inicio de sesión es exitoso
                                Intent transicion = new Intent(Login.this, Home.class);
                                startActivity(transicion);
                            } else {
                                // Muestra un mensaje de error si las credenciales son incorrectas
                                Toast.makeText(Login.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            Toast.makeText(Login.this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
        }

    }
    public void goToSignUp(View view) {
        // Aquí maneja la acción de clic en el texto de registro
        // Por ejemplo, puedes abrir la actividad de registro
        Intent intent = new Intent(this, Registro.class);
        startActivity(intent);
    }

}