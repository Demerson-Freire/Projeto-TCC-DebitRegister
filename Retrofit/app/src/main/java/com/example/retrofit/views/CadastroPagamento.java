package com.example.retrofit.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.retrofit.R;
import com.example.retrofit.interfaces.ApiService;
import com.example.retrofit.modelo.Pagamento;
import com.example.retrofit.servicos.ApiServiceManager;
import com.example.retrofit.watchers.CPFFormatWatcher;
import com.example.retrofit.watchers.ValorWatcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroPagamento extends AppCompatActivity {

    Button btnBuscaCpfCliente, btnSalvaPagamento;
    EditText txtGetCpfCliente, txtGetValorPagamento;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_pagamento);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtGetCpfCliente = findViewById(R.id.txtGetCpfClienteRegistrandoPagamento);
        txtGetValorPagamento = findViewById(R.id.txtGetValorPagamentoRegistrandoPagamento);
        btnBuscaCpfCliente = findViewById(R.id.btnBuscaCpfClienteRegistrandoPagamento);
        btnSalvaPagamento = findViewById(R.id.btnSalvarPagamentoRegistrandoPagamento);

        txtGetCpfCliente.addTextChangedListener(new CPFFormatWatcher(txtGetCpfCliente));
        txtGetValorPagamento.addTextChangedListener(new ValorWatcher(txtGetValorPagamento));

        apiService = ApiServiceManager.getInstance();

        btnSalvaPagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarPagameneto();
            }
        });

        btnBuscaCpfCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CadastroPagamento.this, BuscaCpfRegistraPagamento.class);
                startActivity(intent);
            }
        });

        // Recuperando o CPF passado como extra
        String cpfCliente = getIntent().getStringExtra("cpfCliente");
        // Definindo o texto da EditText
        txtGetCpfCliente.setText(cpfCliente);

    }

    private void cadastrarPagameneto() {
        String idCliente = txtGetCpfCliente.getText().toString();
        Double valor = Double.parseDouble(txtGetValorPagamento.getText().toString());

        // Criar objeto Pagamento
        Pagamento pagamento = new Pagamento(idCliente, valor);

        // Enviar requisição para a API
        Call<Void> call = apiService.cadastrarPagamento(pagamento);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(CadastroPagamento.this);
                    alert.setTitle("AVISO!");
                    alert.setMessage("Pagamento registrado com sucesso!\nDeseja registrar um novo pagamento?");
                    alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    txtGetValorPagamento.setText("");
                                    txtGetValorPagamento.requestFocus();
                                }
                    });
                    alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(CadastroPagamento.this, ListaPagamentos.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    alert.show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(CadastroPagamento.this);
                    alert.setTitle("AVISO!");
                    alert.setMessage("Erro ao registrar compra!\nVerifique se os dados estão corretos.");
                    alert.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            txtGetCpfCliente.requestFocus();
                        }
                    });
                    alert.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(CadastroPagamento.this, "Falha na requisição", Toast.LENGTH_SHORT).show();
            }
        });
    }

}