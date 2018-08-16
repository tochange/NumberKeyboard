package com.lnyp.pswkeyboard;


import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

import static com.lnyp.pswkeyboard.ViewUtils.createCommonDialog;

public class AuthDialog implements View.OnClickListener {
    public static final int TYPE_PHONE = 1;
    public static final int TYPE_PAY = 2;
    public static final int TYPE_IC = 3;
    public static final int TYPE_VIP = 4;

    private int mInputType = TYPE_PHONE;

    private final Dialog mDialog;
    private final EditText mInputText;
    private final TextView mInputTitle;
    private Callback mCallback;

    public interface Callback {
        void onResult(int type, String content);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public AuthDialog(Context context) {
        Dialog dialog = createCommonDialog(context, R.layout.dialog_layout, 20, 40);

        dialog.findViewById(R.id.phone).setOnClickListener(this);
        dialog.findViewById(R.id.pay_code).setOnClickListener(this);
        dialog.findViewById(R.id.ic_card).setOnClickListener(this);
        dialog.findViewById(R.id.vip).setOnClickListener(this);

        mInputText = (EditText) dialog.findViewById(R.id.input_content);
        mInputTitle = (TextView) dialog.findViewById(R.id.input_title);
        mDialog = dialog;

        initView(context, dialog, mInputText);
    }

    private void clearBackgroundColor(View view) {
        LinearLayout parent = (LinearLayout) view.getParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            parent.getChildAt(i).setBackgroundColor(view.getResources().getColor(R.color.default_title_bg));
        }
    }

    public void show() {
        if (mDialog != null) mDialog.show();
    }

    @Override
    public void onClick(View view) {
        clearBackgroundColor(view);
        String content = "";
        if (view.getId() == R.id.phone) {
            mInputType = TYPE_PHONE;
            content = "手机号";

        } else if (view.getId() == R.id.pay_code) {
            mInputType = TYPE_PAY;
            content = "付款码";

        } else if (view.getId() == R.id.ic_card) {
            mInputType = TYPE_IC;
            content = "IC号码";


        } else if (view.getId() == R.id.vip) {
            mInputType = TYPE_VIP;
            content = "会员号码";
        }

        view.setBackgroundColor(view.getResources().getColor(R.color.select_title_bg));
        mInputTitle.setText("请输入" + content);
    }


    private void initView(final Context context, Dialog dialog, final EditText inputText) {
        setInputType(dialog, inputText);

        final NumberKeyboard virtualKeyboardView = (NumberKeyboard) dialog.findViewById(R.id.key_board);
        virtualKeyboardView.setCallback(new NumberKeyboard.Callback() {
            @Override
            public void onItemClick(int position, String clickContent) {
                if (position == NumberKeyboard.DELETE_EVENT_INDEX) {
                    String amount = inputText.getText().toString().trim();
                    if (amount.length() > 0) {
                        amount = amount.substring(0, amount.length() - 1);
                        inputText.setText(amount);

                        Editable ea = inputText.getText();
                        inputText.setSelection(ea.length());
                    }
                } else {
                    if (position == NumberKeyboard.TYPE_EVENT_INDEX) {
                    }
                    String amount = inputText.getText().toString().trim();
                    amount = amount + clickContent;

                    inputText.setText(amount);
                    Editable ea = inputText.getText();
                    inputText.setSelection(ea.length());
                }
            }

            @Override
            public void onOKOrCancel(boolean isOk) {
                if (isOk && (mCallback != null)) {
                    mCallback.onResult(mInputType, inputText.getText().toString());
                }
                mDialog.dismiss();
            }
        });
    }

    private void setInputType(Dialog dialog, EditText inputText) {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            inputText.setInputType(InputType.TYPE_NULL);
        } else {
            dialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus",
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(inputText, false);
            } catch (Exception e) {
            }
        }
    }

}
