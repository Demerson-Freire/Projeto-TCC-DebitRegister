package com.example.retrofit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.retrofit.R;
import com.example.retrofit.modelo.Transacao;

import java.util.List;

public class HistoricoAdapter extends ArrayAdapter<Transacao> {

    private Context mContext;
    private int mResource;

    public HistoricoAdapter(Context context, int resource, List<Transacao> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }

        Transacao transacao = getItem(position);

        TextView textViewTipo = convertView.findViewById(R.id.txtGetTipoTransacaoHistorico);
        TextView textViewCodigo = convertView.findViewById(R.id.txtGetCodigoTransacaoHistorico);
        TextView textViewCpf = convertView.findViewById(R.id.txtGetCpfClienteTransacaoHistorico);
        TextView textViewValor = convertView.findViewById(R.id.txtGetValorTransacaoHistorico);
        TextView textViewData = convertView.findViewById(R.id.txtGetDataTransacaoHistorico);

        if (transacao != null) {
            textViewTipo.setText("Tipo de transação: " + transacao.getTipo());
            textViewCodigo.setText("Código: " + transacao.getCodigo());
            textViewCpf.setText("CPF: " + transacao.getIdCliente());
            textViewValor.setText("Valor: " + transacao.getValor());
            textViewData.setText("Data: " + transacao.getDia());
        }
        return convertView;
    }
}
