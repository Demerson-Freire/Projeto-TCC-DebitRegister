package com.example.retrofit.views;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.retrofit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginAdm extends AppCompatActivity {

    private EditText txtGetEmail;
    private EditText txtGetSenha;
    Button btnLogin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_adm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtGetEmail = findViewById(R.id.txtGetEmailAdm);
        txtGetSenha = findViewById(R.id.txtGetSenhaAdm);
        btnLogin = findViewById(R.id.btnLogarUsuario);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> logarCliente());

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            usuarioLogado();
        } else {
            Toast.makeText(LoginAdm.this, "Usário deslogado!", Toast.LENGTH_SHORT).show();
        }
    }

    public void usuarioLogado(){
        Intent intent = new Intent(LoginAdm.this, MenuPrincipal.class);
        startActivity(intent);
        finish();
    }


    public void logarCliente(){
        String email = txtGetEmail.getText().toString();
        String password = txtGetSenha.getText().toString();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(LoginAdm.this, MenuPrincipal.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginAdm.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    });
        }


    }

    public void onClickTextView(View view) {
        Intent intent = new Intent(LoginAdm.this, MudarSenha.class);
        startActivity(intent);
    }

}
