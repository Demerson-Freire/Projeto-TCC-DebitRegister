package com.example.retrofit.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.retrofit.R;
import com.example.retrofit.interfaces.ApiService;
import com.example.retrofit.servicos.ApiServiceManager;
import com.example.retrofit.watchers.CPFFormatWatcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeletaCliente extends AppCompatActivity {

    EditText txtCpfDeletaCadastroCliente;
    Button btnDeletarCadastroCliente, btnBuscaNomeCpf;

    ImageButton btnDeleteTeste;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleta_cliente);

        txtCpfDeletaCadastroCliente = findViewById(R.id.txtGetCpfClienteDeletaCadastro);
        btnDeletarCadastroCliente = findViewById(R.id.btnDeletarCadastroCliente);
        btnBuscaNomeCpf = findViewById(R.id.btnGetCpfClienteDeletaCadastro);
        btnDeleteTeste = findViewById(R.id.btnDeleteTeste);

        txtCpfDeletaCadastroCliente.addTextChangedListener(new CPFFormatWatcher(txtCpfDeletaCadastroCliente));

        apiService = ApiServiceManager.getInstance();

        btnDeletarCadastroCliente.setOnClickListener(view -> deletarCliente());

        btnDeleteTeste.setOnClickListener(view -> deletarCliente());

        btnBuscaNomeCpf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeletaCliente.this, BuscaCpfDeletaCliente.class);
                startActivity(intent);
                finish();
            }
        });

        // Recuperando o CPF passado como extra
        String cpfCliente = getIntent().getStringExtra("cpfCliente");
        // Definindo o texto da EditText
        txtCpfDeletaCadastroCliente.setText(cpfCliente);

    }

    private void deletarCliente() {

        String cpfCliente = txtCpfDeletaCadastroCliente.getText().toString();

        // Verifica se o campo do CPF está preenchido
        if (cpfCliente.isEmpty()) {
            Toast.makeText(this, "Por favor, insira o CPF do cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(DeletaCliente.this);
        alert.setTitle("Aviso!");
        alert.setMessage("Deseja realmente deletar o cadastro deste cliente?");
        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Faz a chamada para o método deletarCliente() da API
                Call<Void> call = apiService.deletarCliente(cpfCliente);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(DeletaCliente.this);
                            alert.setTitle("AVISO!");
                            alert.setMessage("Cliente deletado com sucesso!\nDeseja deletar outro cadastro?");
                            alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    txtCpfDeletaCadastroCliente.setText("");
                                }
                            });
                            alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(DeletaCliente.this, MenuCadastros.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alert.show();
                        } else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(DeletaCliente.this);
                            alert.setTitle("AVISO!");
                            alert.setMessage("Falha ao deletar cliente!\nVerifique se o CPF está correto.");
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
                        Toast.makeText(DeletaCliente.this, "Erro ao se comunicar com o servidor", Toast.LENGTH_SHORT).show();
                    }
                });
                txtCpfDeletaCadastroCliente.setText("");
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
