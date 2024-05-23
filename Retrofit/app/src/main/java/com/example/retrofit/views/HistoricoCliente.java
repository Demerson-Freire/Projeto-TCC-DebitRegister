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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class HistoricoCliente extends AppCompatActivity {

    Button btnRegistrarCompra, btnRealizarPagamento, btnBuscaHistorico, btnBuscaCpfCliente, btnLimparHistorico;

    EditText txtGetCpfCliente;

    TextView txtSaldoCliente;

    ListView listHistorico;

    ApiService apiService;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_cliente);

        setTitle("Histórico");

        //Toolbar toolbar = findViewById(R.id.toolbarHistoricoCliente);
        txtGetCpfCliente = findViewById(R.id.txtGetCpfClienteHistorico);
        btnRegistrarCompra = findViewById(R.id.btnRegistrarNovaCompraHistorico);
        btnRealizarPagamento = findViewById(R.id.btnRegistrarPagamentoHistorico);
        btnBuscaHistorico = findViewById(R.id.btnBuscarHistoricoCliente);
        btnBuscaCpfCliente = findViewById(R.id.btnBuscarCpfClienteHistorico);
        txtSaldoCliente = findViewById(R.id.txtGetValorDebitoHistorico);
        btnLimparHistorico = findViewById(R.id.btnLimpraHistoricoHistorico);
        listHistorico = findViewById(R.id.lstListaHistorico);

        txtGetCpfCliente.addTextChangedListener(new CPFFormatWatcher(txtGetCpfCliente));

        apiService = ApiServiceManager.getInstance();

        //setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        btnBuscaHistorico.setOnClickListener(v -> {
            buscarHistorico();
        });

        btnBuscaCpfCliente.setOnClickListener(v -> {
            Intent intent = new Intent(HistoricoCliente.this, BuscaCpfHistorico.class);
            startActivity(intent);
            finish();
        });

        btnRegistrarCompra.setOnClickListener(v -> {
            Intent intent = new Intent(HistoricoCliente.this, CadastroCompra.class);
            startActivity(intent);
            finish();
        });

        btnRealizarPagamento.setOnClickListener(v -> {
            Intent intent = new Intent(HistoricoCliente.this, CadastroPagamento.class);
            startActivity(intent);
            finish();
        });

        btnLimparHistorico.setOnClickListener(v -> {
            String cliente = txtGetCpfCliente.getText().toString();
            if (!cliente.isEmpty()) { // Verifica se o campo cliente NÃO está vazio
                Intent intent = new Intent(HistoricoCliente.this, LimpaHistorico.class);
                intent.putExtra("cpfCliente", cliente); // Passa o CPF como extra para a próxima atividade
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(HistoricoCliente.this, LimpaHistorico.class);
                startActivity(intent);
                finish();
            }
        });

        listHistorico.setOnItemClickListener((parent, view, position, id) -> {
            Transacao transacao = (Transacao) parent.getItemAtPosition(position);
            String tipo = transacao.getTipo();

            Intent intent;
            if ("Compra".equals(tipo)) {
                intent = new Intent(HistoricoCliente.this, AtualizaCompras.class);
            } else if ("Pagamento".equals(tipo)) {
                intent = new Intent(HistoricoCliente.this, AtualizaPagamentos.class);
            } else {
                Toast.makeText(HistoricoCliente.this, "Nenhuma transação encontrada", Toast.LENGTH_SHORT).show();
                return;
            }

            // Passando o código da transação para a próxima atividade
            intent.putExtra("codigo", transacao.getCodigo());
            startActivity(intent);
            finish();
        });

        // Recuperando o CPF passado como extra
        String cpfCliente = getIntent().getStringExtra("cpfCliente");
        // Definindo o texto da EditText
        txtGetCpfCliente.setText(cpfCliente);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(HistoricoCliente.this);
        menuInflater.inflate(R.menu.options_historic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuPrincipal){
            Intent intent = new Intent(HistoricoCliente.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.LogoutHistorico){
            AlertDialog.Builder alert = new AlertDialog.Builder(HistoricoCliente.this);
            alert.setTitle("AVISO!");
            alert.setMessage("Deseja realmente se desconectar?");
            alert.setPositiveButton("Sim", (dialog, which) -> {
                mAuth.signOut();
                Intent intent = new Intent(HistoricoCliente.this, LoginAdm.class);
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

    private void buscarHistorico() {
        String cpfCliente = txtGetCpfCliente.getText().toString();

        Call<List<Transacao>> call = apiService.buscarHistoricoPorIdCliente(cpfCliente);
        call.enqueue(new Callback<List<Transacao>>() {
            @Override
            public void onResponse(@NonNull Call<List<Transacao>> call, @NonNull Response<List<Transacao>> response) {
                if (response.isSuccessful()) {
                    List<Transacao> historico = response.body();
                    if (historico != null && !historico.isEmpty()) {
                        HistoricoAdapter adapter = new HistoricoAdapter(HistoricoCliente.this, R.layout.views_historico_cliente, historico);
                        listHistorico.setAdapter(adapter);
                        String idCliente = txtGetCpfCliente.getText().toString();
                        buscarSaldoDebito(idCliente);
                    } else {
                        Toast.makeText(HistoricoCliente.this, "Nenhum histórico encontrado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HistoricoCliente.this, "Erro ao buscar histórico", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Transacao>> call, @NonNull Throwable t) {
                Toast.makeText(HistoricoCliente.this, "Falha na requisição", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(HistoricoCliente.this, "Erro ao obter saldo de débito", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HistoricoCliente.this, "Erro ao obter saldo de débito", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Double> call, @NonNull Throwable t) {
                Toast.makeText(HistoricoCliente.this, "Falha na requisição", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
