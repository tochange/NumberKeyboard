package com.lnyp.pswkeyboard;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class NumberKeyboard extends RelativeLayout implements View.OnClickListener {
    public static final int KEY_EVENT_00 = 1 << 1;
    public static final int KEY_EVENT_X = 1 << 2;
    public static final int KEY_EVENT_DOT = 1 << 3;

    public static final int TYPE_EVENT_INDEX = 10;
    public static final int DELETE_EVENT_INDEX = 11;


    private static final int DEFAULT_KEY_EVENT = KEY_EVENT_00;
    private Callback callback;
    private GridView gridView;
    private View okCancelPanel;
    private View controlPanel;
    private ArrayList<String> valueList = new ArrayList<>();

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.layoutBack) {
            hide();
            return;
        }
        if (callback == null) return;
        if (view.getId() == R.id.ok) {
            callback.onOKOrCancel(true);
        } else if (view.getId() == R.id.cancel) {
            callback.onOKOrCancel(false);
        }
    }

    public interface Callback {
        void onItemClick(int keyEvent, String clickContent);

        void onOKOrCancel(boolean isOk);
    }

    public NumberKeyboard(Context context) {
        this(context, null);
    }

    public NumberKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        final View view = View.inflate(context, R.layout.number_keyboard, NumberKeyboard.this);
        view.findViewById(R.id.layoutBack).setOnClickListener(this);

        gridView = (GridView) view.findViewById(R.id.gv_keyboard);
        okCancelPanel = view.findViewById(R.id.ok_cancel_panel);
        controlPanel = view.findViewById(R.id.closing_panel);
        view.findViewById(R.id.ok).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);

        final KeyBoardAdapter keyBoardAdapter = new KeyBoardAdapter(context, valueList);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                int numColumns = getResources().getInteger(R.integer.numColumns);
                final int itemHeight = getItemHeight(view) / (keyBoardAdapter.getCount() / numColumns);
                keyBoardAdapter.setItemHeight(itemHeight);
                keyBoardAdapter.notifyDataSetChanged();
            }
        }, 100);
        gridView.setAdapter(keyBoardAdapter);


        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.NumberKeyboard);
        int type = a.getInt(R.styleable.NumberKeyboard_key_type, DEFAULT_KEY_EVENT);
        boolean withOkCancel = a.getBoolean(R.styleable.NumberKeyboard_with_ok_cancel, true);
        boolean withControl = a.getBoolean(R.styleable.NumberKeyboard_with_control, false);
        int width = a.getInteger(R.styleable.NumberKeyboard_width_dp, -1);
        int height = a.getInteger(R.styleable.NumberKeyboard_height_dp, -1);


        a.recycle();
        adjustSize(width, height);
        initValueList(withOkCancel, withControl, type);
    }

    private void adjustSize(int width, int height) {
        if (width > 0 && height > 0) {
            View root = findViewById(R.id.root);
            LayoutParams layoutParams = (LayoutParams) root.getLayoutParams();
            layoutParams.height = (int) (height * Resources.getSystem().getDisplayMetrics().density);
            layoutParams.width = (int) (width * Resources.getSystem().getDisplayMetrics().density);
            root.setLayoutParams(layoutParams);
        }
    }

    private int getItemHeight(View view) {
        View root = view.findViewById(R.id.root);
        View top = view.findViewById(R.id.closing_panel);
        if (root != null && top != null) {
            return root.getMeasuredHeight() - top.getMeasuredHeight();
        }
        return 0;
    }

    public void hide() {
        if (View.INVISIBLE == getVisibility() || View.GONE == getVisibility()) return;
        Animation exitAnim = AnimationUtils.loadAnimation(getContext(), R.anim.push_bottom_out);
        NumberKeyboard.this.startAnimation(exitAnim);
        NumberKeyboard.this.setVisibility(View.GONE);
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
        this.callback = callback;
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (callback != null) {
                    callback.onItemClick(position, valueList.get(position));
                }
            }
        });
    }

    public void setType(boolean withOkCancel, boolean withControl, int specialType) {
        if (gridView == null) return;

        initValueList(withOkCancel, withControl, specialType);
        ((KeyBoardAdapter) gridView.getAdapter()).notifyDataSetChanged();
    }

    private void initValueList(boolean withOKAndCancel, boolean withControl, int specialType) {
        setSomeViewVisibility(withOKAndCancel, withControl);
        for (int i = 0; i < 12; i++) {
            if (i < 9) {
                valueList.add(String.valueOf(i + 1));
            } else if (i == 9) {
                valueList.add("0");
            } else if (i == 10) {
                if ((specialType & KEY_EVENT_00) != 0) {
                    valueList.add("00");
                } else if ((specialType & KEY_EVENT_X) != 0) {
                    valueList.add("X");
                } else if ((specialType & KEY_EVENT_DOT) != 0) {
                    valueList.add(".");
                }
            } else if (i == DELETE_EVENT_INDEX) {
                valueList.add("");
            }
        }
    }

    private void setSomeViewVisibility(boolean withOKAndCancel, boolean withControl) {
        if (okCancelPanel != null) {
            okCancelPanel.setVisibility(withOKAndCancel ? VISIBLE : GONE);
        }
        if (controlPanel != null) {
            controlPanel.setVisibility(withControl ? VISIBLE : GONE);
        }
    }
}
