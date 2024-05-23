package com.example.retrofit.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.retrofit.R;
import com.example.retrofit.interfaces.ApiService;
import com.example.retrofit.servicos.ApiServiceManager;
import com.example.retrofit.watchers.CPFFormatWatcher;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeletaCliente extends AppCompatActivity {

    EditText txtCpfDeletaCadastroCliente;
    Button btnDeletarCadastroCliente, btnBuscaNomeCpf;
    ApiService apiService;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleta_cliente);

        setTitle("Deletar Cliente");

        Toolbar toolbar = findViewById(R.id.toolbarDeletaCliente);
        txtCpfDeletaCadastroCliente = findViewById(R.id.txtGetCpfClienteDeletaCadastro);
        btnDeletarCadastroCliente = findViewById(R.id.btnDeletarCadastroCliente);
        btnBuscaNomeCpf = findViewById(R.id.btnGetCpfClienteDeletaCadastro);

        txtCpfDeletaCadastroCliente.addTextChangedListener(new CPFFormatWatcher(txtCpfDeletaCadastroCliente));

        apiService = ApiServiceManager.getInstance();
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        btnDeletarCadastroCliente.setOnClickListener(view -> deletarCliente());


        btnBuscaNomeCpf.setOnClickListener(v -> {
            Intent intent = new Intent(DeletaCliente.this, BuscaCpfDeletaCliente.class);
            startActivity(intent);
            finish();
        });

        // Recuperando o CPF passado como extra
        String cpfCliente = getIntent().getStringExtra("cpfCliente");
        // Definindo o texto da EditText
        txtCpfDeletaCadastroCliente.setText(cpfCliente);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(DeletaCliente.this);
        menuInflater.inflate(R.menu.options_historic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuPrincipal){
            Intent intent = new Intent(DeletaCliente.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.LogoutHistorico){
            AlertDialog.Builder alert = new AlertDialog.Builder(DeletaCliente.this);
            alert.setTitle("AVISO!");
            alert.setMessage("Deseja realmente se desconectar?");
            alert.setPositiveButton("Sim", (dialog, which) -> {
                mAuth.signOut();
                Intent intent = new Intent(DeletaCliente.this, LoginAdm.class);
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

    private void deletarCliente() {

        String cpfCliente = txtCpfDeletaCadastroCliente.getText().toString();

        // Verifica se o campo do CPF está preenchido
        if (cpfCliente.isEmpty()) {
            Toast.makeText(this, "Por favor, insira o CPF do cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(DeletaCliente.this);
        alert.setTitle("Aviso!");
        alert.setMessage("Deseja realmente deletar o cadastro deste cliente?");
        alert.setPositiveButton("Sim", (dialog, which) -> {
            // Faz a chamada para o método deletarCliente() da API
            Call<Void> call = apiService.deletarCliente(cpfCliente);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        AlertDialog.Builder alert1 = new AlertDialog.Builder(DeletaCliente.this);
                        alert1.setTitle("AVISO!");
                        alert1.setMessage("Cliente deletado com sucesso!\nDeseja deletar outro cadastro?");
                        alert1.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txtCpfDeletaCadastroCliente.setText("");
                            }
                        });
                        alert1.setNegativeButton("Não", (dialog1, which1) -> {
                            Intent intent = new Intent(DeletaCliente.this, MenuCadastros.class);
                            startActivity(intent);
                            finish();
                        });
                        alert1.show();
                    } else {
                        AlertDialog.Builder alert1 = new AlertDialog.Builder(DeletaCliente.this);
                        alert1.setTitle("AVISO!");
                        alert1.setMessage("Falha ao deletar cliente!\nVerifique se o CPF está correto.");
                        alert1.setPositiveButton("OK", (dialog12, which12) -> {

                        });
                        alert1.show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(DeletaCliente.this, "Erro ao se comunicar com o servidor", Toast.LENGTH_SHORT).show();
                }
            });
            txtCpfDeletaCadastroCliente.setText("");
        });
        alert.setNegativeButton("Não", (dialog, which) -> {

        });
        alert.show();
    }
}
