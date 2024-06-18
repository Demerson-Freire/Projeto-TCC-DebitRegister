package com.example.retrofit.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.retrofit.modelo.Cliente;
import com.example.retrofit.servicos.ApiServiceManager;
import com.example.retrofit.watchers.CPFFormatWatcher;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroCliente extends AppCompatActivity {

    EditText txtCpfCadastroCliente, txtNomeCadastroCliente, txtEmailCadastroCliente, txtSenhaCadastroCliente;

    Button btnCadastrarCliente;

    private ApiService apiService;

    private FirebaseAuth mAuth;

    private FirebaseAuth mAuth2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Cadastrar Cliente");

        Toolbar toolbar = findViewById(R.id.toolbarCadastroCliente);
        txtCpfCadastroCliente = findViewById(R.id.txtGetCpfClienteCadastroCliente);
        txtNomeCadastroCliente = findViewById(R.id.txtGetNomeClienteCadastrarCliente);
        txtEmailCadastroCliente = findViewById(R.id.txtGetEmailClienteCadastroCliente);
        txtSenhaCadastroCliente = findViewById(R.id.txtGetSenhaClienteCadastroCliente);
        btnCadastrarCliente = findViewById(R.id.btnCadastrarCliente);

        txtCpfCadastroCliente.addTextChangedListener(new CPFFormatWatcher(txtCpfCadastroCliente));

        apiService = ApiServiceManager.getInstance();
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        FirebaseOptions options1 = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyAgQv0ugIhEW2H1zbXtw6SECyMip2_C-wY")
                .setApplicationId("1:317051278226:android:be698ffac220da918d274b")
                .setProjectId("debitregister-b4985")
                .setStorageBucket("debitregister-b4985.appspot.com")
                .build();
        FirebaseApp.initializeApp(this, options1, "DebitRegisterCliente");

        mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("DebitRegisterCliente"));

        btnCadastrarCliente.setOnClickListener(view -> verificarCadastroCliente());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(CadastroCliente.this);
        menuInflater.inflate(R.menu.options_historic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuPrincipal){
            Intent intent = new Intent(CadastroCliente.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.LogoutHistorico){
            AlertDialog.Builder alert = new AlertDialog.Builder(CadastroCliente.this);
            alert.setTitle("AVISO!");
            alert.setMessage("Deseja realmente se desconectar?");
            alert.setPositiveButton("Sim", (dialog, which) -> {
                mAuth.signOut();
                Intent intent = new Intent(CadastroCliente.this, LoginAdm.class);
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

    private void verificarCadastroCliente() {
        // Obter os dados do cliente
        String idCliente = txtCpfCadastroCliente.getText().toString();
        String nomeCliente = txtNomeCadastroCliente.getText().toString();

        // Verificar se o CPF foi preenchido
        if (idCliente.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha o CPF", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar o objeto Cliente
        Cliente cliente = new Cliente(idCliente, nomeCliente);

        // Chamar o método verificarCadastroCliente com o objeto Cliente
        Call<Void> call = apiService.verificarCadastroCliente(cliente);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Cliente já cadastrado
                    AlertDialog.Builder alert = new AlertDialog.Builder(CadastroCliente.this);
                    alert.setTitle("AVISO!");
                    alert.setMessage("O CPF informado já está cadastrado!");
                    alert.setPositiveButton("OK", (dialog, which) -> {
                        txtCpfCadastroCliente.setText("");
                        txtCpfCadastroCliente.requestFocus();
                    });
                    alert.show();
                } else {
                    // Cliente não cadastrado, prosseguir com o cadastro
                    cadastroClienteFirebase();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Tratar falha na requisição
                Toast.makeText(CadastroCliente.this, "Falha na requisição: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cadastroClienteFirebase() {
        String email = txtEmailCadastroCliente.getText().toString();
        String senha = txtSenhaCadastroCliente.getText().toString();
        String cpf = txtCpfCadastroCliente.getText().toString();

        if (email.isEmpty() || senha.isEmpty() || cpf.isEmpty()) {
            Toast.makeText(CadastroCliente.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth2.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth2.getCurrentUser();
                        if (user != null) {
                            Log.d("CadastroCliente", "Usuário criado com sucesso. UID: " + user.getUid());

                            String userId = user.getUid();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios");
                            reference.child(userId).child("email").setValue(email);
                            reference.child(userId).child("cpf").setValue(cpf)
                                    .addOnSuccessListener(aVoid -> {
                                        cadastrarCliente();
                                        salvarTokenDispositivo(userId);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("CadastroCliente", "Falha ao salvar os dados no Realtime Database.", e);
                                        Toast.makeText(CadastroCliente.this, "Falha ao salvar os dados no Realtime Database.", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.e("CadastroCliente", "Usuário é nulo após a criação.");
                            Toast.makeText(CadastroCliente.this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Erro desconhecido";
                        Log.e("CadastroCliente", "Falha na autenticação: " + errorMessage);
                        Toast.makeText(CadastroCliente.this, "Authentication failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }



    private void salvarTokenDispositivo(String userId) {
        // Obter o token de registro do dispositivo
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        // Enviar o token para o backend ou salvá-lo no Realtime Database diretamente
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios");
                        reference.child(userId).child("token").setValue(token)
                                .addOnSuccessListener(aVoid -> {
                                })
                                .addOnFailureListener(e -> {
                                    // Falha ao salvar o token
                                    Toast.makeText(CadastroCliente.this, "Falha ao salvar o token no Realtime Database.", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Lidar com possíveis erros ao obter o token
                        Toast.makeText(CadastroCliente.this, "Erro ao obter o token de registro.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cadastrarCliente() {
        String cpf = txtCpfCadastroCliente.getText().toString();
        String nome = txtNomeCadastroCliente.getText().toString();

        if (cpf.isEmpty() || nome.isEmpty()){
            Toast.makeText(CadastroCliente.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String regexCPF = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}";

        if (!cpf.matches(regexCPF)) {
            Toast.makeText(CadastroCliente.this, "CPF inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Cliente cliente = new Cliente(cpf, nome);

        Call<Void> call = apiService.cadastrarCliente(cliente);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()){
                    AlertDialog.Builder alert = new AlertDialog.Builder(CadastroCliente.this);
                    alert.setTitle("AVISO!");
                    alert.setMessage("Cliente cadastardo com sucesso!\nDeseja realizar outro cadastro?");
                    alert.setPositiveButton("Sim", (dialog, which) -> {
                        cadastroClienteFirebase();
                        txtCpfCadastroCliente.setText("");
                        txtNomeCadastroCliente.setText("");
                        txtCpfCadastroCliente.requestFocus();
                    });
                    alert.setNegativeButton("Não", (dialog, which) -> {
                        Intent intent = new Intent(CadastroCliente.this, MenuCadastros.class);
                        startActivity(intent);
                        finish();
                    });
                    alert.show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(CadastroCliente.this);
                    alert.setTitle("AVISO!");
                    alert.setMessage("Erro ao cadastrar cliente!\nVerifique os dados do cliente.");
                    alert.setPositiveButton("OK", (dialog, which) -> {

                    });
                    alert.show();
                    Toast.makeText(CadastroCliente.this, "Erro ao cadastrar cliente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Tratamento de falha de conexão
                Toast.makeText(CadastroCliente.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}