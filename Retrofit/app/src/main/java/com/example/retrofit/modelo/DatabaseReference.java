/*package com.example.retrofit.modelo;

import java.util.HashMap;
import java.util.Map;

public class DatabaseReference {
    private Map<String, Object> data;

    public DatabaseReference() {
        this.data = new HashMap<>();
    }

    public DatabaseReference child(String childPath) {
        DatabaseReference childRef = new DatabaseReference();
        if (data.containsKey(childPath)) {
            Object childData = data.get(childPath);
            if (childData instanceof Map) {
                childRef.data = (Map<String, Object>) childData;
            }
        }
        return childRef;
    }

    public void setValue(Object value) {
        // Simula a operação de definir valor no banco de dados
        // Aqui, estamos apenas atualizando os dados internos da referência
        data.clear();
        if (value instanceof Map) {
            data.putAll((Map<String, Object>) value);
        }
    }

    public void addListenerForSingleValueEvent(ValueEventListener listener) {
        // Simula a operação de adicionar um ouvinte para uma única vez
        // Aqui, estamos apenas chamando o método onDataChange do ouvinte com os dados atuais
        listener.onDataChange(new DataSnapshot(data));
    }

    public interface ValueEventListener {
        void onDataChange(DataSnapshot dataSnapshot);
    }

    public static class DataSnapshot {
        private Map<String, Object> data;

        public DataSnapshot(Map<String, Object> data) {
            this.data = data;
        }

        public boolean exists() {
            return data != null && !data.isEmpty();
        }

        public String getKey() {
            // Retorna a chave do nó atual
            // Aqui, estamos apenas retornando uma string vazia
            return "";
        }

        public DataSnapshot child(String childPath) {
            // Retorna um novo snapshot para o nó filho especificado
            // Aqui, estamos apenas retornando um snapshot vazio
            return new DataSnapshot(new HashMap<>());
        }

        public <T> T getValue(Class<T> valueType) {
            // Retorna o valor do nó atual
            // Aqui, estamos apenas retornando null
            return null;
        }
    }
}*/
