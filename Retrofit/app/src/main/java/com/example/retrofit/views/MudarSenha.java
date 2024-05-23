package com.example.retrofit.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.retrofit.R;
import com.google.firebase.auth.FirebaseAuth;

public class MudarSenha extends AppCompatActivity {

    EditText txtGetEmail;

    Button btnEnviarEmail;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mudar_senha);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Alterar Senha");

        //Toolbar toolbar = findViewById(R.id.toolbarMudarSenha);
        txtGetEmail = findViewById(R.id.txtGetEmailMudarSenha);
        btnEnviarEmail = findViewById(R.id.btnEnviarEmailMudarSenha);

        mAuth = FirebaseAuth.getInstance();
        //setSupportActionBar(toolbar);

        btnEnviarEmail.setOnClickListener(v -> {
            mudarSenha();
        });

    }

    public void mudarSenha(){

        String emailAddress = txtGetEmail.getText().toString();
        mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                AlertDialog.Builder alert = new AlertDialog.Builder(MudarSenha.this);
                alert.setTitle("AVISO!");
                alert.setMessage("E-mail enviado com sucesso!\n" +
                        "Vale lembrar que a solicitação\n" +
                        "para mudar a senha será enviada\n" +
                        "mesmo se o e-mail informado não\n" +
                        "estiver cadastrado em nosso\n" +
                        "banco de dados!");
                alert.setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(MudarSenha.this, LoginAdm.class);
                    startActivity(intent);
                    finish();
                });
                alert.show();
            } else {
                Toast.makeText(MudarSenha.this, "Informe um e-mail válido!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}