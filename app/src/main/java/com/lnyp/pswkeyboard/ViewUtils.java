package com.lnyp.pswkeyboard;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;


public class ViewUtils {
    public static Dialog createCommonDialog(Context context, int layoutResId, int positionXDp, int positionYDp) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layoutResId);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = (int) (positionXDp * Resources.getSystem().getDisplayMetrics().density);
        wl.y = (int) (positionYDp * Resources.getSystem().getDisplayMetrics().density);
        wl.gravity = Gravity.TOP | Gravity.LEFT;
        window.setAttributes(wl);
        return dialog;
    }

}
