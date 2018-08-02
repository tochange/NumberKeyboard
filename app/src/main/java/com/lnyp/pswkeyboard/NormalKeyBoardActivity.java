package com.lnyp.pswkeyboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.lang.reflect.Method;

public class NormalKeyBoardActivity extends AppCompatActivity {
    private EditText textAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_key_board);

        initView();
    }

    private void initView() {
        textAmount = (EditText) findViewById(R.id.textAmount);
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            textAmount.setInputType(InputType.TYPE_NULL);
        } else {
            this.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus",
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(textAmount, false);
            } catch (Exception e) {
            }
        }

        final VirtualKeyboardView virtualKeyboardView = (VirtualKeyboardView) findViewById(R.id.virtualKeyboardView);
        textAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualKeyboardView.show();
            }
        });

        virtualKeyboardView.setCallback(new VirtualKeyboardView.Callback() {
            @Override
            public void onItemClick(int position, String clickContent) {
                if (position == VirtualKeyboardView.DELETE_EVENT_INDEX) {
                    String amount = textAmount.getText().toString().trim();
                    if (amount.length() > 0) {
                        amount = amount.substring(0, amount.length() - 1);
                        textAmount.setText(amount);

                        Editable ea = textAmount.getText();
                        textAmount.setSelection(ea.length());
                    }
                } else if (position == 9) {// to do 没有小数点
                    String amount = textAmount.getText().toString().trim();
                    if (!amount.contains(".")) {
                        amount = amount + clickContent;
                        textAmount.setText(amount);

                        Editable ea = textAmount.getText();
                        textAmount.setSelection(ea.length());
                    }
                } else {
                    String amount = textAmount.getText().toString().trim();
                    amount = amount + clickContent;

                    textAmount.setText(amount);

                    Editable ea = textAmount.getText();
                    textAmount.setSelection(ea.length());
                }
            }
        });
    }
}
