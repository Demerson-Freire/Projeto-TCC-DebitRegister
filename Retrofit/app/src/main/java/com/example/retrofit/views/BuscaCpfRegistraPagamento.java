package com.example.retrofit.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.retrofit.R;
import com.example.retrofit.adapter.ClienteAdapter;
import com.example.retrofit.interfaces.ApiService;
import com.example.retrofit.servicos.ApiServiceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuscaCpfRegistraPagamento extends AppCompatActivity {

    EditText txtNomeCliente;
    Button btnBuscarCliente;
    ListView txtListaNomesCPF;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_busca_cpf_registra_pagamento);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNomeCliente = findViewById(R.id.txtGetNomeClienteBuscaNomeCpfPagamentoRegistro);
        btnBuscarCliente = findViewById(R.id.btnBuscarNomeClienteNomeCpfPagamentoRegistro);
        txtListaNomesCPF = findViewById(R.id.txtListaNomeCpfPagamentoRegistro);

        apiService = ApiServiceManager.getInstance();

        btnBuscarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeCliente = txtNomeCliente.getText().toString();
                buscarClientesPorNome(nomeCliente);
            }
        });

        txtListaNomesCPF.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object[] cliente = (Object[]) parent.getItemAtPosition(position);
                if (cliente != null && cliente.length >= 2) {
                    String cpfCliente = (String) cliente[0]; // CPF do cliente está na primeira posição
                    Intent intent = new Intent(BuscaCpfRegistraPagamento.this, CadastroPagamento.class);
                    intent.putExtra("cpfCliente", cpfCliente); // Passa o CPF como extra para a próxima atividade
                    startActivity(intent);
                    finish();
                }
            }
        });

        txtNomeCliente.requestFocus();

    }

    private void buscarClientesPorNome(String nomeCliente) {
        Call<List<Object[]>> call = apiService.buscarClientePorNome(nomeCliente);
        call.enqueue(new Callback<List<Object[]>>() {
            @Override
            public void onResponse(@NonNull Call<List<Object[]>> call, @NonNull Response<List<Object[]>> response) {
                if (response.isSuccessful()) {
                    List<Object[]> clientes = response.body();
                    ClienteAdapter adapter = new ClienteAdapter(BuscaCpfRegistraPagamento.this, R.layout.views_nomes_deleta_cliente, clientes);
                    txtListaNomesCPF.setAdapter(adapter);
                } else {
                    Toast.makeText(BuscaCpfRegistraPagamento.this, "Erro ao buscar clientes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Object[]>> call, @NonNull Throwable t) {
                Toast.makeText(BuscaCpfRegistraPagamento.this, "Falha na requisição", Toast.LENGTH_SHORT).show();
            }
        });
    }

}