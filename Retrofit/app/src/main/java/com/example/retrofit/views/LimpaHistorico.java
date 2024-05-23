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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.retrofit.R;
import com.example.retrofit.interfaces.ApiService;
import com.example.retrofit.servicos.ApiServiceManager;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LimpaHistorico extends AppCompatActivity {

    EditText txtCpfCliente;

    Button btnLimparHistorico, btnBuscarCpf;

    ApiService apiService;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_limpa_historico);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Limpar Histórico");

        //Toolbar toolbar = findViewById(R.id.toolbarLimpaHistorico);
        txtCpfCliente = findViewById(R.id.txtGetCpfClienteLimparHistorico);
        btnLimparHistorico = findViewById(R.id.btnLimparHistoricoLimparHistorico);
        btnBuscarCpf = findViewById(R.id.btnBuscaCpfClienteLimparHistorico);

        apiService = ApiServiceManager.getInstance();
        //setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        btnBuscarCpf.setOnClickListener(v -> {
            Intent intent = new Intent(LimpaHistorico.this, BuscaCpfLimparHistorico.class);
            startActivity(intent);
            finish();
        });

        btnLimparHistorico.setOnClickListener(v -> limparHistorico());
        String cpfCliente = getIntent().getStringExtra("cpfCliente");
        // Definindo o texto da EditText
        txtCpfCliente.setText(cpfCliente);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(LimpaHistorico.this);
        menuInflater.inflate(R.menu.options_historic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuPrincipal){
            Intent intent = new Intent(LimpaHistorico.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.LogoutHistorico){
            AlertDialog.Builder alert = new AlertDialog.Builder(LimpaHistorico.this);
            alert.setTitle("AVISO!");
            alert.setMessage("Deseja realmente se desconectar?");
            alert.setPositiveButton("Sim", (dialog, which) -> {
                mAuth.signOut();
                Intent intent = new Intent(LimpaHistorico.this, LoginAdm.class);
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

    private void limparHistorico(){
        String cpfCliente = txtCpfCliente.getText().toString();

        if (cpfCliente.isEmpty()) {
            Toast.makeText(this, "Por favor, insira o CPF do cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(LimpaHistorico.this);
        alert.setTitle("Aviso!");
        alert.setMessage("Deseja realmente deletar o histórico deste cliente?");
        alert.setPositiveButton("Sim", (dialog, which) -> {
            // Faz a chamada para o método deletarCliente() da API
            Call<Void> call = apiService.deletarHistorico(cpfCliente);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        AlertDialog.Builder alert1 = new AlertDialog.Builder(LimpaHistorico.this);
                        alert1.setTitle("AVISO!");
                        alert1.setMessage("Histórico deletado com sucesso!\nDeseja deletar outro histórico?");
                        alert1.setPositiveButton("Sim", (dialog1, which1) -> txtCpfCliente.setText(""));
                        alert1.setNegativeButton("Não", (dialog12, which12) -> {
                            Intent intent = new Intent(LimpaHistorico.this, HistoricoCliente.class);
                            startActivity(intent);
                            finish();
                        });
                        alert1.show();
                    } else {
                        AlertDialog.Builder alert1 = new AlertDialog.Builder(LimpaHistorico.this);
                        alert1.setTitle("AVISO!");
                        alert1.setMessage("Falha ao deletar Histórico!\nVerifique se o CPF está correto.");
                        alert1.setPositiveButton("OK", (dialog13, which13) -> {

                        });
                        alert1.show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(LimpaHistorico.this, "Erro ao se comunicar com o servidor", Toast.LENGTH_SHORT).show();
                }
            });
            txtCpfCliente.setText("");
        });
        alert.setNegativeButton("Não", (dialog, which) -> {

        });
        alert.show();

    }

}