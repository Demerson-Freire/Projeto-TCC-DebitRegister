package com.example.retrofit.modelo;

import java.util.Date;

public class Pagamento {
    private int codigo;
    private String idCliente;
    private Double valor;
    private Date dia;

    public Pagamento(String idCliente, Double valor) {
        this.idCliente = idCliente;
        this.valor = valor;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public Date getDia() {
        return dia;
    }

    public void setDia(Date dia) {
        this.dia = dia;
    }
}
