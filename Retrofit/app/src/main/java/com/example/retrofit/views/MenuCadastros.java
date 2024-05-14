package com.example.retrofit.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.retrofit.R;

public class MenuCadastros extends AppCompatActivity {

    Button btnCadastrarClienteMenu, btnDeletarCadastroMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_cadastros);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnCadastrarClienteMenu = findViewById(R.id.btnCadastrarClienteMenuCadastro);
        btnDeletarCadastroMenu = findViewById(R.id.btnDeletarClienteMenuCadastro);

        btnCadastrarClienteMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuCadastros.this, CadastroCliente.class);
                startActivity(intent);
            }
        });

        btnDeletarCadastroMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MenuCadastros.this, DeletaCliente.class);
                startActivity(intent2);
            }
        });
    }
}