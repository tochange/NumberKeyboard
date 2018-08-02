package com.lnyp.pswkeyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class VirtualKeyboardView extends RelativeLayout {
    public static final int KEY_EVENT_00 = 1 << 1;// or KEY_EVENT_X
    public static final int KEY_EVENT_OK_CANCEL = 1 << 2;

    public static final int DELETE_EVENT_INDEX = 11;

    private GridView gridView;
    private ArrayList<String> valueList = new ArrayList<>();

    public interface Callback {
        void onItemClick(int keyEvent, String clickContent);
    }

    public VirtualKeyboardView(Context context) {
        this(context, null);
    }

    public VirtualKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.layout_virtual_keyboard, VirtualKeyboardView.this);
        view.findViewById(R.id.layoutBack).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });

        gridView = (GridView) view.findViewById(R.id.gv_keybord);
        initValueList(false, false);

        KeyBoardAdapter keyBoardAdapter = new KeyBoardAdapter(context, valueList);
        gridView.setAdapter(keyBoardAdapter);
    }

    public void hide() {
        if (View.INVISIBLE == getVisibility() || View.GONE == getVisibility()) return;
        Animation exitAnim = AnimationUtils.loadAnimation(getContext(), R.anim.push_bottom_out);
        VirtualKeyboardView.this.startAnimation(exitAnim);
        VirtualKeyboardView.this.setVisibility(View.GONE);
    }

    public void show() {
        if (View.VISIBLE == getVisibility()) return;
        setFocusable(true);
        setFocusableInTouchMode(true);

        Animation enterAnim = AnimationUtils.loadAnimation(getContext(), R.anim.push_bottom_in);
        startAnimation(enterAnim);
        setVisibility(View.VISIBLE);
    }

    public void setCallback(final Callback callback) {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (callback != null) {
                    callback.onItemClick(position, valueList.get(position));
                }
            }
        });
    }

    public void setType(int type) {
        if (gridView == null) return;
        boolean withOKAndCancel = (type & KEY_EVENT_OK_CANCEL) != 0;
        boolean double0 = (type & KEY_EVENT_00) != 0;

        initValueList(withOKAndCancel, double0);
        ((KeyBoardAdapter) gridView.getAdapter()).notifyDataSetChanged();
    }

    private void initValueList(boolean withOKAndCancel, boolean double0) {
        for (int i = 0; i < 12; i++) {
            if (i < 9) {
                valueList.add(String.valueOf(i + 1));
            } else if (i == 9) {
                valueList.add("0");
            } else if (i == 10) {
                if (double0) {
                    valueList.add("00");
                } else {
                    valueList.add("X");
                }
            } else if (i == DELETE_EVENT_INDEX) {
                valueList.add("");
            }
        }
    }
}
