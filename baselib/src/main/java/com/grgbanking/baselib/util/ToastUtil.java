
package com.grgbanking.baselib.util;

import android.content.Context;
import android.widget.Toast;

/**
 * @author G0210181
 */
public final class ToastUtil {

    private static Toast toast = null;// 提醒框

    public static void init(Context ctx) {
        toast = Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
    }

    private ToastUtil() {
    }

    public static void shortShow(CharSequence cs) {
        toastShow(cs, Toast.LENGTH_SHORT);
    }

    public static void longShow(CharSequence cs) {
        toastShow(cs, Toast.LENGTH_LONG);
    }

    private static void toastShow(CharSequence cs, int duration) {
        if (toast == null) {
            throw new RuntimeException("ToastUtil must be init(),before use");
        }
        toast.setText(cs);
        toast.setDuration(duration);
        toast.show();
    }

}
