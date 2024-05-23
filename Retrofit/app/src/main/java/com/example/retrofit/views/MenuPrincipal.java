package com.example.retrofit.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.retrofit.R;
import com.google.firebase.auth.FirebaseAuth;

public class MenuPrincipal extends AppCompatActivity {

    Button btnAdmCliente, btnAdmCompra, btnAdmHistorico, btnAdmPagamentos, btnAdmUsuarios;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Menu Principal");

        Toolbar toolbar = findViewById(R.id.toolbarMenuPrincipal);
        btnAdmCliente = findViewById(R.id.btnAdmCadastros);
        btnAdmCompra = findViewById(R.id.btnAdmComprasMenuPrincipal);
        btnAdmHistorico = findViewById(R.id.btnAdmHistoricoMenuPrincipal);
        btnAdmPagamentos = findViewById(R.id.btnAdmPagamentosMenuPrincipal);
        btnAdmUsuarios = findViewById(R.id.btnCadastrarUsuarioMenuPrincipal);

        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();


        btnAdmCompra.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, ListaCompras.class);
            startActivity(intent);
        });

        btnAdmCliente.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, MenuCadastros.class);
            startActivity(intent);
        });

        btnAdmHistorico.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, HistoricoCliente.class);
            startActivity(intent);
        });

        btnAdmPagamentos.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, ListaPagamentos.class);
            startActivity(intent);
        });

        btnAdmUsuarios.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, CadastroUsuario.class);
            startActivity(intent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(MenuPrincipal.this);
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Logout) {
            AlertDialog.Builder alert = new AlertDialog.Builder(MenuPrincipal.this);
            alert.setTitle("AVISO!");
            alert.setMessage("Deseja realmente se desconectar?");
            alert.setPositiveButton("Sim", (dialog, which) -> {
                mAuth.signOut();
                Intent intent = new Intent(MenuPrincipal.this, LoginAdm.class);
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

}