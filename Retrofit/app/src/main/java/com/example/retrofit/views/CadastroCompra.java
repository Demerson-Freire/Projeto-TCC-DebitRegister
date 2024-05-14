package com.example.retrofit.views;


import android.content.Intent;
import android.os.Bundle;
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
import com.example.retrofit.modelo.Compra;
import com.example.retrofit.servicos.ApiServiceManager;
import com.example.retrofit.watchers.CPFFormatWatcher;
import com.example.retrofit.watchers.ValorWatcher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CadastroCompra extends AppCompatActivity {

    Button btnBuscaCpfCliente, btnSalvaCompra;
    EditText txtGetCpfCliente, txtGetValorCompra;
    private ApiService apiService;


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

        txtGetCpfCliente = findViewById(R.id.txtGetCpfClienteCadastrandoCompra);
        txtGetValorCompra = findViewById(R.id.txtGetValorCompraCadastrandoCompra);
        btnBuscaCpfCliente = findViewById(R.id.btnBuscarCpfClienteCadastrandoCompra);
        btnSalvaCompra = findViewById(R.id.btnSalvarCompraCadastrandoCompra);

        txtGetCpfCliente.addTextChangedListener(new CPFFormatWatcher(txtGetCpfCliente));
        txtGetValorCompra.addTextChangedListener(new ValorWatcher(txtGetValorCompra));

        apiService = ApiServiceManager.getInstance();

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

    private void buscarEmailPorCpf() {
        String cpf = txtGetCpfCliente.getText().toString();
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");
        Query query = usuariosRef.orderByChild("cpf").equalTo(cpf);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Seu código para manipular o retorno do banco de dados
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Seu código para lidar com erros no banco de dados
            }
        });
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
}