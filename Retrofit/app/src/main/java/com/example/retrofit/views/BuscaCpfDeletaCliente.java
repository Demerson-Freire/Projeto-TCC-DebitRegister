package com.example.retrofit.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.retrofit.R;
import com.example.retrofit.adapter.ClienteAdapter;
import com.example.retrofit.interfaces.ApiService;
import com.example.retrofit.servicos.ApiServiceManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuscaCpfDeletaCliente extends AppCompatActivity {

    EditText txtNomeCliente;
    Button btnBuscarCliente;
    ListView txtListaNomesCPF;
    ApiService apiService;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_cpf_deleta_cliente);

        setTitle("Buscando CPF");

        //Toolbar toolbar = findViewById(R.id.toolbarBuscaCpfDeletaCliente);
        txtNomeCliente = findViewById(R.id.txtGetNomeClienteBuscaNomeCpf);
        btnBuscarCliente = findViewById(R.id.btnBuscarNomeClienteNomeCpf);
        txtListaNomesCPF = findViewById(R.id.txtListaNomeCpf);

        apiService = ApiServiceManager.getInstance();
        //setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        btnBuscarCliente.setOnClickListener(v -> {
            String nomeCliente = txtNomeCliente.getText().toString();
            buscarClientesPorNome(nomeCliente);
        });

        txtListaNomesCPF.setOnItemClickListener((parent, view, position, id) -> {
            Object[] cliente = (Object[]) parent.getItemAtPosition(position);
            if (cliente != null && cliente.length >= 2) {
                String cpfCliente = (String) cliente[0]; // CPF do cliente está na primeira posição
                Intent intent = new Intent(BuscaCpfDeletaCliente.this, DeletaCliente.class);
                intent.putExtra("cpfCliente", cpfCliente); // Passa o CPF como extra para a próxima atividade
                startActivity(intent);
            }
        });

        txtNomeCliente.requestFocus();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(BuscaCpfDeletaCliente.this);
        menuInflater.inflate(R.menu.options_historic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuPrincipal){
            Intent intent = new Intent(BuscaCpfDeletaCliente.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.LogoutHistorico){
            AlertDialog.Builder alert = new AlertDialog.Builder(BuscaCpfDeletaCliente.this);
            alert.setTitle("AVISO!");
            alert.setMessage("Deseja realmente se desconectar?");
            alert.setPositiveButton("Sim", (dialog, which) -> {
                mAuth.signOut();
                Intent intent = new Intent(BuscaCpfDeletaCliente.this, LoginAdm.class);
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

    private void buscarClientesPorNome(String nomeCliente) {
        Call<List<Object[]>> call = apiService.buscarClientePorNome(nomeCliente);
        call.enqueue(new Callback<List<Object[]>>() {
            @Override
            public void onResponse(@NonNull Call<List<Object[]>> call, @NonNull Response<List<Object[]>> response) {
                if (response.isSuccessful()) {
                    List<Object[]> clientes = response.body();
                    ClienteAdapter adapter = new ClienteAdapter(BuscaCpfDeletaCliente.this, R.layout.views_nomes_deleta_cliente, clientes);
                    txtListaNomesCPF.setAdapter(adapter);
                } else {
                    Toast.makeText(BuscaCpfDeletaCliente.this, "Erro ao buscar clientes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Object[]>> call, @NonNull Throwable t) {
                Toast.makeText(BuscaCpfDeletaCliente.this, "Falha na requisição", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
