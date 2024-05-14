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

public class CadastroUsuario extends AppCompatActivity {

    EditText txtGetEmail, txtGetSenha, txtGetConfirmaSenha;
    Button btnCadastrarUsuario;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Cadastrar Usuário");

        Toolbar toolbar = findViewById(R.id.toolbarCadastroUsuario);
        txtGetEmail = findViewById(R.id.txtGetEmailCadastroUsuario);
        txtGetSenha = findViewById(R.id.txtGetSenhaCadastroUsuario);
        txtGetConfirmaSenha = findViewById(R.id.txtGetConfirmaSenhaCadastroUsuario);
        btnCadastrarUsuario = findViewById(R.id.btnCadastrarUsuarioCadastroUsuario);

        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        btnCadastrarUsuario.setOnClickListener(v -> validaCadastro());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(CadastroUsuario.this);
        menuInflater.inflate(R.menu.options_historic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuPrincipal){
            Intent intent = new Intent(CadastroUsuario.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.LogoutHistorico){
            AlertDialog.Builder alert = new AlertDialog.Builder(CadastroUsuario.this);
            alert.setTitle("AVISO!");
            alert.setMessage("Deseja realmente se desconectar?");
            alert.setPositiveButton("Sim", (dialog, which) -> {
                mAuth.signOut();
                Intent intent = new Intent(CadastroUsuario.this, LoginAdm.class);
                startActivity(intent);
                finish();
            });
            alert.setNegativeButton("Não", (dialog, which) -> {

            });
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void validaCadastro(){
        String email = txtGetEmail.getText().toString();
        String senha = txtGetSenha.getText().toString();
        String confirmaSenha = txtGetConfirmaSenha.getText().toString();

        if (email.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()){
            Toast.makeText(this, "Todos os campos precisam ser preechidos!", Toast.LENGTH_SHORT).show();
        } else if (txtGetSenha.getText().toString().trim().length() < 6) {
            Toast.makeText(this, "A senha precisa ter pelo menos 6 caracteres!", Toast.LENGTH_SHORT).show();
        } else {
            cadastarUsuario();
        }
    }

    public void cadastarUsuario(){
        String email = txtGetEmail.getText().toString();
        String senha = txtGetSenha.getText().toString();
        String confirmaSenha = txtGetConfirmaSenha.getText().toString();

        if(!senha.equals(confirmaSenha)){
            AlertDialog.Builder alert = new AlertDialog.Builder(CadastroUsuario.this);
            alert.setTitle("AVISO!");
            alert.setMessage("A senha e a confirmação da senha precisam ser iguais!");
            alert.setPositiveButton("OK", (dialog, which) -> {
                txtGetSenha.setText("");
                txtGetConfirmaSenha.setText("");
                txtGetSenha.requestFocus();
            });
            alert.show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(CadastroUsuario.this);
                            alert.setTitle("AVISO!");
                            alert.setMessage("Administrador cadastrado com sucesso!\nDeseja cadastrar outro administrador novamente?");
                            alert.setPositiveButton("SIM", (dialog, which) -> {
                                txtGetEmail.setText("");
                                txtGetSenha.setText("");
                                txtGetConfirmaSenha.setText("");
                                txtGetEmail.requestFocus();
                            });
                            alert.setNegativeButton("NÃO", (dialog, which) -> {
                                Intent intent = new Intent(CadastroUsuario.this, MenuPrincipal.class);
                                startActivity(intent);
                                finish();
                            });
                            alert.show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(CadastroUsuario.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

}