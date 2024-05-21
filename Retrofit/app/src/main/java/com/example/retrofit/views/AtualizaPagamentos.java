package com.example.retrofit.views;

import android.content.DialogInterface;
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
import com.example.retrofit.modelo.Pagamento;
import com.example.retrofit.servicos.ApiServiceManager;
import com.example.retrofit.servicos.RetrofitClient;
import com.example.retrofit.watchers.CPFFormatWatcher;
import com.example.retrofit.watchers.ValorWatcher;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AtualizaPagamentos extends AppCompatActivity {

    TextView txtGetCodigo;

    EditText txtGetIdCliente, txtGetValorPagamento;

    Button btnSalvaPagamento, btnDeletaPagemento;
    ApiService apiService;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_atualiza_pagamentos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Edição de pagamentos");

        Toolbar toolbar = findViewById(R.id.toolbarAtualizaPagamentos);
        txtGetCodigo = findViewById(R.id.txtGetCodigoPagamentoEditandoPagamento);
        txtGetIdCliente = findViewById(R.id.txtGetCpfClienteEditandoPagamento);
        txtGetValorPagamento = findViewById(R.id.txtGetValorPagamentoEditandoPagamento);
        btnSalvaPagamento = findViewById(R.id.btnRegistrarPagamentoEditandoPagamento);
        btnDeletaPagemento = findViewById(R.id.btnExcluirPagamentoEditandoPagamento);

        apiService = ApiServiceManager.getInstance();
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        txtGetIdCliente.addTextChangedListener(new CPFFormatWatcher(txtGetIdCliente));
        txtGetValorPagamento.addTextChangedListener(new ValorWatcher(txtGetValorPagamento));

        recebeCodigo();

        btnSalvaPagamento.setOnClickListener(v -> editarPagamento());

        btnDeletaPagemento.setOnClickListener(v -> deletarPagamento());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(AtualizaPagamentos.this);
        menuInflater.inflate(R.menu.options_historic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuPrincipal){
            Intent intent = new Intent(AtualizaPagamentos.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.LogoutHistorico){
            AlertDialog.Builder alert = new AlertDialog.Builder(AtualizaPagamentos.this);
            alert.setTitle("AVISO!");
            alert.setMessage("Deseja realmente se desconectar?");
            alert.setPositiveButton("Sim", (dialog, which) -> {
                mAuth.signOut();
                Intent intent = new Intent(AtualizaPagamentos.this, LoginAdm.class);
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

    private void recebeCodigo() {
        int codigo = getIntent().getIntExtra("codigo", 0);

        Call<Pagamento> call = apiService.buscaPagamentoPorCodigo(codigo);
        call.enqueue(new Callback<Pagamento>() {
            @Override
            public void onResponse(@NonNull Call<Pagamento> call, @NonNull Response<Pagamento> response) {
                if (response.isSuccessful()){
                    Pagamento pagamento = response.body();
                    if (pagamento != null){
                        int codigo = pagamento.getCodigo();
                        String cpf = pagamento.getIdCliente();
                        Double valor = pagamento.getValor();

                        txtGetCodigo.setText(String.valueOf(codigo));
                        txtGetIdCliente.setText(cpf);
                        txtGetValorPagamento.setText(String.valueOf(valor));
                    } else {
                        Toast.makeText(AtualizaPagamentos.this, "Nenhum pagamento encontrado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AtualizaPagamentos.this, "Erro ao buscar pagamento", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Pagamento> call, @NonNull Throwable throwable) {
                Toast.makeText(AtualizaPagamentos.this, "Falha na requisição", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editarPagamento(){
        String idCliente = txtGetIdCliente.getText().toString();
        Double valor = Double.parseDouble(txtGetValorPagamento.getText().toString());

        Pagamento pagamentoAtualizado = new Pagamento(idCliente, valor);

        int codigo = getIntent().getIntExtra("codigo", 0);

        Call<Void> call = apiService.editarPagementoPorCodigo(codigo, pagamentoAtualizado);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()){
                    fetchToken(idCliente);
                    Toast.makeText(AtualizaPagamentos.this, "Pagamento editado com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AtualizaPagamentos.this, "Erro ao editar pagamento", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                Toast.makeText(AtualizaPagamentos.this, "Falha na requisição", Toast.LENGTH_SHORT).show();
            }
        });
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
                    Toast.makeText(AtualizaPagamentos.this, "Token não encontrado!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(AtualizaPagamentos.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void enviarNotificacao(String token) {

        double valor = Double.parseDouble(txtGetValorPagamento.getText().toString());

        String title = "Pagamento alterado!";
        String body = "O valor de uma pagamento foi alterado para o valor de R$:"+ valor +", na sua conta Debit Register!\n" +
                "Caso você não reconheça essa alteração, entre em contato com o vendedor.";

        // Envia a notificação usando o token
        apiService.sendNotification(token, title, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Notificação enviada com sucesso
                    Toast.makeText(AtualizaPagamentos.this, "Notificação enviada com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    // Falha ao enviar a notificação
                    Toast.makeText(AtualizaPagamentos.this, "Erro ao enviar a notificação!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Trate a falha na requisição
                Toast.makeText(AtualizaPagamentos.this, "Falha ao tentar enviar notificação", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletarPagamento(){
        int codigo = getIntent().getIntExtra("codigo", 0);
        String idCliente = txtGetIdCliente.getText().toString();

       if (idCliente.isEmpty()){
           Toast.makeText(this, "Erro ao atualizar pagamento!\n Verifique se os dados estão corretos.", Toast.LENGTH_SHORT).show();
       } else {
           AlertDialog.Builder alert = new AlertDialog.Builder(AtualizaPagamentos.this);
           alert.setTitle("AVISO!");
           alert.setMessage("Deseja realmente deletar esse pagamento?");
           alert.setPositiveButton("Sim", (dialog, which) -> {
               Call<Void> call = apiService.deletarPagamento(codigo);
               call.enqueue(new Callback<Void>() {
                   @Override
                   public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                       if (response.isSuccessful()){
                           fetchTokenDel(idCliente);
                           Toast.makeText(AtualizaPagamentos.this, "Pagamento deletado com sucesso", Toast.LENGTH_SHORT).show();
                       } else {
                           Toast.makeText(AtualizaPagamentos.this, "Falha ao deletar pagamento", Toast.LENGTH_SHORT).show();
                       }
                   }

                   @Override
                   public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                       Toast.makeText(AtualizaPagamentos.this, "Erro ao se comunicar com o servidor", Toast.LENGTH_SHORT).show();
                   }
               });
           });
           alert.setNegativeButton("Não", (dialog, which) -> {

           });
           alert.show();
       }
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
                    Toast.makeText(AtualizaPagamentos.this, "Token não encontrado!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(AtualizaPagamentos.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void enviarNotificacaoDel(String token) {

        double valor = Double.parseDouble(txtGetValorPagamento.getText().toString());

        String title = "Pagamento cancelado!";
        String body = "Um pagamento no valor de R$:"+ valor +", foi cancelado na sua conta Debit Register!\n" +
                "Caso você não reconheça esse cancelamento, entre em contato com o vendedor.";

        // Envia a notificação usando o token
        apiService.sendNotification(token, title, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Notificação enviada com sucesso
                    Toast.makeText(AtualizaPagamentos.this, "Notificação enviada com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    // Falha ao enviar a notificação
                    Toast.makeText(AtualizaPagamentos.this, "Erro ao enviar a notificação!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Trate a falha na requisição
                Toast.makeText(AtualizaPagamentos.this, "Falha ao tentar enviar notificação", Toast.LENGTH_SHORT).show();
            }
        });
    }

}