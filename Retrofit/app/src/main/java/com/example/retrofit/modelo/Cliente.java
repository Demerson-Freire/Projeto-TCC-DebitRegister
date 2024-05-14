package com.example.retrofit.modelo;

public class Cliente {
    private String idCliente;
    private String nome;

    public Cliente(String idCliente, String nome) {
        this.idCliente = idCliente;
        this.nome = nome;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
