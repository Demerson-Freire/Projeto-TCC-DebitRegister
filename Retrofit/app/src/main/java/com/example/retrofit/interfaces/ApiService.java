package com.example.retrofit.interfaces;

import com.example.retrofit.modelo.Cliente;
import com.example.retrofit.modelo.Compra;
import com.example.retrofit.modelo.Pagamento;
import com.example.retrofit.modelo.Transacao;
import com.example.retrofit.modelo.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface ApiService {

    //=========== GET ==============================================================================
    @GET("/api_debit/cliente/{nome}/")
    Call<List<Object[]>> buscarClientePorNome(@Path("nome") String nome);
    @GET("/api_debit/transacoes/{idCliente}/")
    Call<List<Transacao>> buscarHistoricoPorIdCliente(@Path("idCliente") String idCliente);
    @GET("api_debit/compras/{idCliente}/")
    Call<List<Transacao>> buscarComprasPorIdCliente(@Path("idCliente") String idCliente);
    @GET("/api_debit/compra/editar/{codigo}/")
    Call<Compra> buscaCompraPorCodigo(@Path("codigo") int codigo);
    @GET("/api_debit/pagamento/editar/{codigo}/")
    Call<Pagamento> buscaPagamentoPorCodigo(@Path("codigo") int codigo);
    @GET("api_debit/pagamentos/{idCliente}/")
    Call<List<Transacao>> buscarPagamentosPorIdCliente(@Path("idCliente") String idCliente);
    @GET("/api_debit/saldo/{idCliente}/")
    Call<Double> calcularSaldoDebito(@Path("idCliente") String idCliente);
    /*@GET("/api_debit/cliente/{idCliente}/nome/")
    Call<String> buscarNomeClientePorId(@Path("idCliente") String idCliente);*/

    //============ POST ============================================================================
    @POST("/api_debit/login/")
    Call<Void> login(@Body Usuario usuario);
    @POST("/api_debit/cliente/")
    Call<Void> cadastrarCliente(@Body Cliente cliente);
    @POST("/api_debit/compra/")
    Call<Void> cadastrarCompra(@Body Compra compra);
    @POST("/api_debit/pagamento/")
    Call<Void> cadastrarPagamento(@Body Pagamento pagamento);
    @POST("/api_debit/cliente/verificarCadastro/")
    Call<Void> verificarCadastroCliente(@Body Cliente cliente);

    //======================= PUT ==================================================================
    @PUT("/api_debit/compra/update/{codigo}/")
    Call<Void> editaCompraPorCodigo(@Path("codigo") int codigo, @Body Compra compra);
    @PUT("/api_debit/pagamento/update/{codigo}/")
    Call<Void> editarPagementoPorCodigo(@Path("codigo") int codigo, @Body Pagamento pagamento);

    //============ DELETE ==========================================================================
    @DELETE("/api_debit/cliente/{cpf}/")
    Call<Void> deletarCliente(@Path("cpf") String cpf);
    @DELETE("/api_debit/cliente/{idCliente}/limparHistorico/")
    Call<Void> deletarHistorico(@Path("idCliente") String idCliente);
    @DELETE("/api_debit/compra/{codigo}/")
    Call<Void> deletarCompra(@Path("codigo") int codigo);
    @DELETE("/api_debit/pagamento/{codigo}/")
    Call<Void> deletarPagamento(@Path("codigo") int codigo);

}