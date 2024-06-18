package com.example.retrofit.servicos;

import com.example.retrofit.interfaces.ApiService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceManager {
    private static ApiService instance;

    public static ApiService getInstance() {
        if (instance == null) {
            // Configurar o Logging Interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            // Construir o Retrofit com o cliente configurado
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api-debit.onrender.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            instance = retrofit.create(ApiService.class);
        }
        return instance;
    }
}
