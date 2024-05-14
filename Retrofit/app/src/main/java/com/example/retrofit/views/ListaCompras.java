package com.example.retrofit.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.retrofit.R;
import com.example.retrofit.adapter.HistoricoAdapter;
import com.example.retrofit.interfaces.ApiService;
import com.example.retrofit.modelo.Transacao;
import com.example.retrofit.servicos.ApiServiceManager;
import com.example.retrofit.watchers.CPFFormatWatcher;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaCompras extends AppCompatActivity {

    Button btnBuscarCompras, btnBuscarCpf, btnRegistrarNovaCompra, btnRealizarPagamento;
    EditText txtGetCpf;
    TextView txtSaldoCliente;
    ListView listCompras;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_compras);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBuscarCompras = findViewById(R.id.btnBuscarCpfClienteCompras);
        btnBuscarCpf = findViewById(R.id.btnBuscarCpfClienteListaCompras);
        btnRegistrarNovaCompra = findViewById(R.id.btnRegistrarNovaCompraCompras);
        btnRealizarPagamento = findViewById(R.id.btnRegistrarPagamentoCompras);
        txtSaldoCliente = findViewById(R.id.txtGetSaldoClienteCompras);
        txtGetCpf = findViewById(R.id.txtGetCpfClienteCompras);
        listCompras = findViewById(R.id.lstGetComprasClienteCompras);

        txtGetCpf.addTextChangedListener(new CPFFormatWatcher(txtGetCpf));

        apiService = ApiServiceManager.getInstance();

        btnBuscarCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarCompras();
                String idCliente = txtGetCpf.getText().toString();
                buscarSaldoDebito(idCliente);
            }
        });

        btnRegistrarNovaCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaCompras.this, CadastroCompra.class);
                startActivity(intent);
            }
        });

        btnRealizarPagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaCompras.this, CadastroPagamento.class);
                startActivity(intent);
            }
        });

        btnBuscarCpf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaCompras.this, BuscaCpfCompras.class);
                startActivity(intent);
            }
        });

        listCompras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Transacao transacao = (Transacao) parent.getItemAtPosition(position);
                int codigo = transacao.getCodigo();
                Intent intent = new Intent(ListaCompras.this, AtualizaCompras.class);
                intent.putExtra("codigo", codigo);
                startActivity(intent);
                finish();
            }
        });

        // Recuperando o CPF passado como extra
        String cpfCliente = getIntent().getStringExtra("cpfCliente");
        // Definindo o texto da EditText
        txtGetCpf.setText(cpfCliente);

    }

    private void  buscarCompras(){
        String cpfCliente = txtGetCpf.getText().toString();

        Call<List<Transacao>> call = apiService.buscarComprasPorIdCliente(cpfCliente);
        call.enqueue(new Callback<List<Transacao>>() {
            @Override
            public void onResponse(@NonNull Call<List<Transacao>> call, @NonNull Response<List<Transacao>> response) {
                if (response.isSuccessful()){
                    List<Transacao> compras = response.body();
                    if (compras != null && !compras.isEmpty()) {
                        HistoricoAdapter adapter = new HistoricoAdapter(ListaCompras.this, R.layout.views_historico_cliente, compras);
                        listCompras.setAdapter(adapter);
                    } else {
                        Toast.makeText(ListaCompras.this, "Nenhuma compra encontrada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ListaCompras.this, "Erro ao buscar compras", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Transacao>> call, @NonNull Throwable throwable) {
                Toast.makeText(ListaCompras.this, "Falha na requisição", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ListaCompras.this, "Erro ao obter saldo de débito", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ListaCompras.this, "Erro ao obter saldo de débito", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Double> call, @NonNull Throwable t) {
                Toast.makeText(ListaCompras.this, "Falha na requisição", Toast.LENGTH_SHORT).show();
            }
        });
    }

}