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
import com.example.retrofit.modelo.Pagamento;
import com.example.retrofit.servicos.ApiServiceManager;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private void deletarPagamento(){
        int codigo = getIntent().getIntExtra("codigo", 0);

        Call<Void> call = apiService.deletarPagamento(codigo);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()){
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
    }

}