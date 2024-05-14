package com.example.retrofit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.retrofit.R;

import java.util.List;

public class ClienteAdapter extends ArrayAdapter<Object[]> {

    private Context mContext;
    private int mResource;

    public ClienteAdapter(Context context, int resource, List<Object[]> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }

        Object[] cliente = getItem(position);

        TextView textViewNome = convertView.findViewById(R.id.txtGetNomeBuscandoCPF);
        TextView textViewCpf = convertView.findViewById(R.id.txtGetBuscandoCPF);

        if (cliente != null) {
            String cpf = (String) cliente[0]; // Supondo que o CPF está na primeira posição do array
            String nome = (String) cliente[1]; // Supondo que o nome está na segunda posição do array

            textViewCpf.setText("CPF: " + cpf);
            textViewNome.setText("Nome: " + nome);
        }

        return convertView;
    }
}
