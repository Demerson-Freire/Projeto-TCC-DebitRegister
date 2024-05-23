package com.example.retrofit.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.example.retrofit.adapter.HistoricoAdapter;
import com.example.retrofit.interfaces.ApiService;
import com.example.retrofit.modelo.Transacao;
import com.example.retrofit.servicos.ApiServiceManager;
import com.example.retrofit.watchers.CPFFormatWatcher;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaPagamentos extends AppCompatActivity {

    Button btnBuscarPagamentos, btnBuscarCpf, btnRegistrarNovaCompra, btnRealizarPagamento;
    EditText txtGetCpf;
    TextView txtSaldoCliente;
    ListView listPagamentos;
    ApiService apiService;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_pagamentos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Lista de Pagamentos");

        Toolbar toolbar = findViewById(R.id.toolbarListaPagamento);
        btnBuscarPagamentos = findViewById(R.id.btnBuscarCpfClientePagamentos);
        btnBuscarCpf = findViewById(R.id.btnBuscarCpfClienteListaPagamentos);
        btnRegistrarNovaCompra = findViewById(R.id.btnRegistrarNovaCompraPagamentos);
        btnRealizarPagamento = findViewById(R.id.btnRegistrarPagamentoPagamentos);
        txtSaldoCliente = findViewById(R.id.txtGetSaldoClientePagamentos);
        txtGetCpf = findViewById(R.id.txtGetCpfClientePagamentos);
        listPagamentos = findViewById(R.id.lstGetComprasClientePagamentos);

        apiService = ApiServiceManager.getInstance();
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        txtGetCpf.addTextChangedListener(new CPFFormatWatcher(txtGetCpf));

        btnBuscarPagamentos.setOnClickListener(v -> {
            buscarPagamentos();
            String idCliente = txtGetCpf.getText().toString();
            buscarSaldoDebito(idCliente);
        });

        btnRegistrarNovaCompra.setOnClickListener(v -> {
            Intent intent = new Intent(ListaPagamentos.this, CadastroCompra.class);
            startActivity(intent);
        });

        btnRealizarPagamento.setOnClickListener(v -> {
            Intent intent = new Intent(ListaPagamentos.this, CadastroPagamento.class);
            startActivity(intent);
        });

        btnBuscarCpf.setOnClickListener(v -> {
            Intent intent = new Intent(ListaPagamentos.this, BuscaCpfPagamentos.class);
            startActivity(intent);
        });

        listPagamentos.setOnItemClickListener((parent, view, position, id) -> {
            Transacao transacao = (Transacao) parent.getItemAtPosition(position);
            int codigo = transacao.getCodigo();
            Intent intent = new Intent(ListaPagamentos.this, AtualizaPagamentos.class);
            intent.putExtra("codigo", codigo);
            startActivity(intent);
            finish();
        });

        // Recuperando o CPF passado como extra
        String cpfCliente = getIntent().getStringExtra("cpfCliente");
        // Definindo o texto da EditText
        txtGetCpf.setText(cpfCliente);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(ListaPagamentos.this);
        menuInflater.inflate(R.menu.options_historic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuPrincipal){
            Intent intent = new Intent(ListaPagamentos.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.LogoutHistorico){
            AlertDialog.Builder alert = new AlertDialog.Builder(ListaPagamentos.this);
            alert.setTitle("AVISO!");
            alert.setMessage("Deseja realmente se desconectar?");
            alert.setPositiveButton("Sim", (dialog, which) -> {
                mAuth.signOut();
                Intent intent = new Intent(ListaPagamentos.this, LoginAdm.class);
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

    private void buscarPagamentos() {
        String cpfCliente = txtGetCpf.getText().toString();

        Call<List<Transacao>> call = apiService.buscarPagamentosPorIdCliente(cpfCliente);
        call.enqueue(new Callback<List<Transacao>>() {
            @Override
            public void onResponse(@NonNull Call<List<Transacao>> call, @NonNull Response<List<Transacao>> response) {
                if (response.isSuccessful()){
                    List<Transacao> compras = response.body();
                    if (compras != null && !compras.isEmpty()) {
                        HistoricoAdapter adapter = new HistoricoAdapter(ListaPagamentos.this, R.layout.views_historico_cliente, compras);
                        listPagamentos.setAdapter(adapter);
                    } else {
                        Toast.makeText(ListaPagamentos.this, "Nenhum pagamento encontrado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ListaPagamentos.this, "Erro ao buscar pagamentos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Transacao>> call, @NonNull Throwable throwable) {
                Toast.makeText(ListaPagamentos.this, "Falha na requisição", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarSaldoDebito(String idCliente) {
        Call<Double> call = apiService.calcularSaldoDebito(idCliente);
        call.enqueue(new Callback<Double>() {
            @Override
            public void onResponse(@NonNull Call<Double> call, @NonNull Response<Double> response) {
                if (response.isSuccessful()) {
                    Double saldoDebito = response.body();
                    if (saldoDebito != null) {
                        txtSaldoCliente.setText(String.valueOf(saldoDebito));
                    } else {
                        Toast.makeText(ListaPagamentos.this, "Erro ao obter saldo de débito", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ListaPagamentos.this, "Erro ao obter saldo de débito", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Double> call, @NonNull Throwable t) {
                Toast.makeText(ListaPagamentos.this, "Falha na requisição", Toast.LENGTH_SHORT).show();
            }
        });
    }

}