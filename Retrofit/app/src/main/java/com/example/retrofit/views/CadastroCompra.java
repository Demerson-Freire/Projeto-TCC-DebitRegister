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
import com.example.retrofit.modelo.Compra;
import com.example.retrofit.servicos.ApiServiceManager;
import com.example.retrofit.servicos.RetrofitClient;
import com.example.retrofit.watchers.CPFFormatWatcher;
import com.example.retrofit.watchers.ValorWatcher;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CadastroCompra extends AppCompatActivity {

    Button btnBuscaCpfCliente, btnSalvaCompra;
    EditText txtGetCpfCliente, txtGetValorCompra;
    private ApiService apiService;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_compra);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Cadastrar Compra");

        Toolbar toolbar = findViewById(R.id.toolbarCadastroCompra);
        txtGetCpfCliente = findViewById(R.id.txtGetCpfClienteCadastrandoCompra);
        txtGetValorCompra = findViewById(R.id.txtGetValorCompraCadastrandoCompra);
        btnBuscaCpfCliente = findViewById(R.id.btnBuscarCpfClienteCadastrandoCompra);
        btnSalvaCompra = findViewById(R.id.btnSalvarCompraCadastrandoCompra);

        txtGetCpfCliente.addTextChangedListener(new CPFFormatWatcher(txtGetCpfCliente));
        txtGetValorCompra.addTextChangedListener(new ValorWatcher(txtGetValorCompra));

        apiService = ApiServiceManager.getInstance();
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        btnSalvaCompra.setOnClickListener(v -> cadastrarCompra());

        btnBuscaCpfCliente.setOnClickListener(v -> {
            Intent intent = new Intent(CadastroCompra.this, BuscaCpfRegistraCompra.class);
            startActivity(intent);
        });

        // Recuperando o CPF passado como extra
        String cpfCliente = getIntent().getStringExtra("cpfCliente");
        // Definindo o texto da EditText
        txtGetCpfCliente.setText(cpfCliente);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(CadastroCompra.this);
        menuInflater.inflate(R.menu.options_historic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuPrincipal){
            Intent intent = new Intent(CadastroCompra.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.LogoutHistorico){
            AlertDialog.Builder alert = new AlertDialog.Builder(CadastroCompra.this);
            alert.setTitle("AVISO!");
            alert.setMessage("Deseja realmente se desconectar?");
            alert.setPositiveButton("Sim", (dialog, which) -> {
                mAuth.signOut();
                Intent intent = new Intent(CadastroCompra.this, LoginAdm.class);
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

    private void cadastrarCompra() {
        String idCliente = txtGetCpfCliente.getText().toString();
        double valor = Double.parseDouble(txtGetValorCompra.getText().toString());

        if (valor <= 0) {
            Toast.makeText(CadastroCompra.this, "O valor precisa ser maior que 0!", Toast.LENGTH_SHORT).show();
        } else {
            // Criar objeto Compra
            Compra compra = new Compra(idCliente, valor);

            // Enviar requisição para a API
            Call<Void> call = apiService.cadastrarCompra(compra);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        fetchToken(idCliente);
                        AlertDialog.Builder alert = new AlertDialog.Builder(CadastroCompra.this);
                        alert.setTitle("AVISO!");
                        alert.setMessage("Compra registrada com sucesso!\nDeseja registrar uma nova compra?");
                        alert.setPositiveButton("Sim", (dialog, which) -> {
                            txtGetValorCompra.setText("");
                            txtGetValorCompra.requestFocus();
                        });
                        alert.setNegativeButton("Não", (dialog, which) -> {
                            Intent intent = new Intent(CadastroCompra.this, ListaCompras.class);
                            startActivity(intent);
                            finish();
                        });
                        alert.show();
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(CadastroCompra.this);
                        alert.setTitle("AVISO!");
                        alert.setMessage("Erro ao registrar compra!\n Verifique se os dados estão corretos.");
                        alert.setPositiveButton("OK!", (dialog, which) -> txtGetCpfCliente.requestFocus());
                        alert.show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(CadastroCompra.this, "Falha na requisição", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchToken(String cpf) {
        Retrofit retrofit = RetrofitClient.getClient();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<String> call = apiService.getToken(cpf);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body();
                    enviarNotificacao(token);
                } else {
                    Toast.makeText(CadastroCompra.this, "Token não encontrado!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(CadastroCompra.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void enviarNotificacao(String token) {

        double valor = Double.parseDouble(txtGetValorCompra.getText().toString());

        String title = "Nova compra realizada";
        String body = "Uma nova compra no valor de R$:" + valor + ", foi registrada na sua conta Debit Register!\n" +
                "Caso você não reconheça essa compra, entre em contato com o vendedor.";

        // Envia a notificação usando o token
        apiService.sendNotification(token, title, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Notificação enviada com sucesso
                    Toast.makeText(CadastroCompra.this, "Notificação enviada com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    // Falha ao enviar a notificação
                    Toast.makeText(CadastroCompra.this, "Erro ao enviar a notificação!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Trate a falha na requisição
                Toast.makeText(CadastroCompra.this, "Falha ao tentar enviar notificação", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
