package com.example.retrofit.watchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CPFFormatWatcher implements TextWatcher {

    private boolean isFormatting;
    private EditText editText;

    public CPFFormatWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // No action needed
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // No action needed
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!isFormatting) {
            isFormatting = true;

            String cpf = s.toString().replaceAll("[^\\d]", "");
            StringBuilder formatted = new StringBuilder();

            for (int i = 0; i < cpf.length(); i++) {
                if (i == 3 || i == 6) {
                    formatted.append('.');
                } else if (i == 9) {
                    formatted.append('-');
                }
                formatted.append(cpf.charAt(i));
            }

            editText.removeTextChangedListener(this);
            editText.setText(formatted.toString());
            editText.setSelection(formatted.length());
            editText.addTextChangedListener(this);

            isFormatting = false;
        }
    }
}
