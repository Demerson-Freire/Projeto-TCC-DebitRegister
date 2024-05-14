package com.example.retrofit.watchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class ValorWatcher implements TextWatcher {

    private final EditText editText;
    private final DecimalFormat decimalFormat;

    public ValorWatcher(EditText editText) {
        this.editText = editText;
        this.decimalFormat = new DecimalFormat("#.00");
        DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.'); // Define o ponto como separador decimal
        decimalFormat.setDecimalFormatSymbols(symbols);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Não é necessário implementar
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Não é necessário implementar
    }

    @Override
    public void afterTextChanged(Editable s) {
        editText.removeTextChangedListener(this);

        String cleanString = s.toString().replaceAll("\\D", "");
        if (cleanString.isEmpty()) {
            editText.setText("");
        } else {
            double parsed = Double.parseDouble(cleanString) / 100;
            String formatted = decimalFormat.format(parsed);
            editText.setText(formatted);
        }

        editText.setSelection(editText.getText().length());
        editText.addTextChangedListener(this);
    }
}



