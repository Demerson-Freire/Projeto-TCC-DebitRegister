package com.example.retrofit.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class AtualizaCompras extends AppCompatActivity {

    TextView txtGetCodigo;

    EditText txtGetIdCliente, txtGetValorCompra;

    Button btnSalvaCompra, btnDeletaCompra;
    ApiService apiService;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_atualiza_compras);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Atualizar Compras");

        Toolbar toolbar = findViewById(R.id.toolbarAtualizaCompras);
        txtGetCodigo = findViewById(R.id.txtGetCodigoCompraEditandoCompra);
        txtGetIdCliente = findViewById(R.id.txtGetCpfClienteEditandoCompra);
        txtGetValorCompra = findViewById(R.id.txtGetValorCompraEditandoCompra);
        btnSalvaCompra = findViewById(R.id.btnRegistrarCompraEditandoCompra);
        btnDeletaCompra = findViewById(R.id.btnExcluirCompraEditandoCompra);

        txtGetIdCliente.addTextChangedListener(new CPFFormatWatcher(txtGetIdCliente));
        txtGetValorCompra.addTextChangedListener(new ValorWatcher(txtGetValorCompra));

        apiService = ApiServiceManager.getInstance();
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        recebeCodigo();

        btnSalvaCompra.setOnClickListener(v -> editarCompra());

        btnDeletaCompra.setOnClickListener(v -> deletaCompra());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(AtualizaCompras.this);
        menuInflater.inflate(R.menu.options_historic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuPrincipal){
            Intent intent = new Intent(AtualizaCompras.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.LogoutHistorico){
            AlertDialog.Builder alert = new AlertDialog.Builder(AtualizaCompras.this);
            alert.setTitle("AVISO!");
            alert.setMessage("Deseja realmente se desconectar?");
            alert.setPositiveButton("Sim", (dialog, which) -> {
                mAuth.signOut();
                Intent intent = new Intent(AtualizaCompras.this, LoginAdm.class);
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


    private void recebeCodigo(){
        int codigo = getIntent().getIntExtra("codigo", 0);

        Call<Compra> call = apiService.buscaCompraPorCodigo(codigo);
        call.enqueue(new Callback<Compra>() {
            @Override
            public void onResponse(@NonNull Call<Compra> call, @NonNull Response<Compra> response) {
                if (response.isSuccessful()){
                    Compra compra = response.body();
                    if (compra != null){
                        int codigo = compra.getCodigo();
                        String cpf = compra.getIdCliente();
                        Double valor = compra.getValor();

                        txtGetCodigo.setText(String.valueOf(codigo));
                        txtGetIdCliente.setText(cpf);
                        txtGetValorCompra.setText(String.valueOf(valor));
                    } else {
                        Toast.makeText(AtualizaCompras.this, "Nenhuma compra encontrada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AtualizaCompras.this, "Erro ao buscar compra", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Compra> call, @NonNull Throwable throwable) {
                Toast.makeText(AtualizaCompras.this, "Falha na requisição", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editarCompra(){
        String idCliente = txtGetIdCliente.getText().toString();
        Double valor = Double.parseDouble(txtGetValorCompra.getText().toString());

        Compra compraAtualizada = new Compra(idCliente, valor);

        int codigo = getIntent().getIntExtra("codigo", 0);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("AVISO!");
        alert.setMessage("Deseja realmente editar essa compra?");
        alert.setPositiveButton("Sim", (dialog, which) -> {
            Call<Void> call = apiService.editaCompraPorCodigo(codigo, compraAtualizada);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()){
                        fetchToken(idCliente);
                        Toast.makeText(AtualizaCompras.this, "Compra editada com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AtualizaCompras.this, "Erro ao editar compra", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                    Toast.makeText(AtualizaCompras.this, "Falha na requisição", Toast.LENGTH_SHORT).show();
                }
            });
        });
        alert.setNegativeButton("Não", (dialog, which) -> {

        });
        alert.show();
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
                    Toast.makeText(AtualizaCompras.this, "Token não encontrado!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(AtualizaCompras.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarNotificacao(String token) {

        double valor = Double.parseDouble(txtGetValorCompra.getText().toString());

        String title = "Compra alterada!";
        String body = "O valor de uma compra foi alterada para o valor de R$:"+valor+", na sua conta Debit Register!\n" +
                "Caso você não reconheça essa alteração, entre em contato com o vendedor.";

        // Envia a notificação usando o token
        apiService.sendNotification(token, title, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Notificação enviada com sucesso
                    Toast.makeText(AtualizaCompras.this, "Notificação enviada com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AtualizaCompras.this, ListaCompras.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Falha ao enviar a notificação
                    Toast.makeText(AtualizaCompras.this, "Erro ao enviar a notificação!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Trate a falha na requisição
                Toast.makeText(AtualizaCompras.this, "Falha ao tentar enviar notificação", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletaCompra(){
        int codigo = getIntent().getIntExtra("codigo", 0);
        String idCliente = txtGetIdCliente.getText().toString();

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("AVISO!");
        alert.setMessage("Deseja realmente deletar essa compra de maneira permanente?");
        alert.setPositiveButton("SIM", (dialog, which) -> {
            Call<Void> call = apiService.deletarCompra(codigo);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(AtualizaCompras.this, "Compra deletada com sucesso", Toast.LENGTH_SHORT).show();
                        fetchTokenDel(idCliente);
                    } else {
                        Toast.makeText(AtualizaCompras.this, "Falha ao deletar compra", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                    Toast.makeText(AtualizaCompras.this, "Erro ao se comunicar com o servidor", Toast.LENGTH_SHORT).show();
                }
            });
        });
        alert.setNegativeButton("Não", (dialog, which) -> {

        });
        alert.show();
    }

    private void fetchTokenDel(String cpf) {
        Retrofit retrofit = RetrofitClient.getClient();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<String> call = apiService.getToken(cpf);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body();
                    enviarNotificacaoDel(token);
                } else {
                    Toast.makeText(AtualizaCompras.this, "Token não encontrado!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(AtualizaCompras.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarNotificacaoDel(String token) {

        double valor = Double.parseDouble(txtGetValorCompra.getText().toString());

        String title = "Compra deletada!";
        String body = "Uma compra foi deletada no valor de R$:"+valor+", na sua conta Debit Register!\n" +
                "Caso você não reconheça essa alteração, entre em contato com o vendedor.";

        // Envia a notificação usando o token
        apiService.sendNotification(token, title, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Notificação enviada com sucesso
                    Toast.makeText(AtualizaCompras.this, "Notificação enviada com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AtualizaCompras.this, MenuPrincipal.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Falha ao enviar a notificação
                    Toast.makeText(AtualizaCompras.this, "Erro ao enviar a notificação!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Trate a falha na requisição
                Toast.makeText(AtualizaCompras.this, "Falha ao tentar enviar notificação", Toast.LENGTH_SHORT).show();
            }
        });
    }

}