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
import com.example.retrofit.servicos.ApiServiceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LimpaHistorico extends AppCompatActivity {

    EditText txtCpfCliente;

    Button btnLimparHistorico, btnBuscarCpf;

    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_limpa_historico);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtCpfCliente = findViewById(R.id.txtGetCpfClienteLimparHistorico);
        btnLimparHistorico = findViewById(R.id.btnLimparHistoricoLimparHistorico);
        btnBuscarCpf = findViewById(R.id.btnBuscaCpfClienteLimparHistorico);

        apiService = ApiServiceManager.getInstance();

        btnBuscarCpf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LimpaHistorico.this, BuscaCpfLimparHistorico.class);
                startActivity(intent);
                finish();
            }
        });

        btnLimparHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limparHistorico();
            }
        });
        String cpfCliente = getIntent().getStringExtra("cpfCliente");
        // Definindo o texto da EditText
        txtCpfCliente.setText(cpfCliente);
    }

    private void limparHistorico(){
        String cpfCliente = txtCpfCliente.getText().toString();

        if (cpfCliente.isEmpty()) {
            Toast.makeText(this, "Por favor, insira o CPF do cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(LimpaHistorico.this);
        alert.setTitle("Aviso!");
        alert.setMessage("Deseja realmente deletar o histórico deste cliente?");
        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Faz a chamada para o método deletarCliente() da API
                Call<Void> call = apiService.deletarHistorico(cpfCliente);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(LimpaHistorico.this);
                            alert.setTitle("AVISO!");
                            alert.setMessage("Histórico deletado com sucesso!\nDeseja deletar outro histórico?");
                            alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    txtCpfCliente.setText("");
                                }
                            });
                            alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(LimpaHistorico.this, HistoricoCliente.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alert.show();
                        } else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(LimpaHistorico.this);
                            alert.setTitle("AVISO!");
                            alert.setMessage("Falha ao deletar Histórico!\nVerifique se o CPF está correto.");
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alert.show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(LimpaHistorico.this, "Erro ao se comunicar com o servidor", Toast.LENGTH_SHORT).show();
                    }
                });
                txtCpfCliente.setText("");
            }
        });
        alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();

    }

}