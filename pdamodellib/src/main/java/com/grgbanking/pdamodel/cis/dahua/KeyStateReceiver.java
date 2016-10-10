package com.grgbanking.pdamodel.cis.dahua;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class KeyStateReceiver extends BroadcastReceiver {
    private boolean defaultdown = false;
    private boolean defaultup = false;
    private CisKeyListerner keyL;


    public KeyStateReceiver(CisKeyListerner keyL) {
        this.keyL = keyL;
    }

    public interface CisKeyListerner {
        /**
         * 松开F1按键
         */
        public void onKeyUpF1();

        /**
         * 按下F1按键
         */
        public void onKeyDownF1();

        /**
         * 松开F2按键
         */
        public void onKeyUpF2();

        /**
         * 按下F2按键
         */
        public void onKeyDownF2();

        /**
         * 松开左键
         */
        public void onKeyUpLeft();

        /**
         * 按下左键
         */
        public void onKeyDownLeft();

        /**
         * 松开右键按键
         */
        public void onKeyUpRight();

        /**
         * 按下右键
         */
        public void onKeyDownRight();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        int keycode = intent.getIntExtra("keycode", 0);
        Log.e("keycode", "按键" + keycode);
        boolean keydown = intent.getBooleanExtra("keydown", defaultdown);
        boolean keyup = intent.getBooleanExtra("keyup", defaultup);
        switch (keycode) {
            case 133:
                if (keydown) {
                    // 左键被按下
                    if (keyL != null) {
                        keyL.onKeyDownLeft();
                    }
                } else {
                    // 左键松开
                    if (keyL != null) {
                        keyL.onKeyUpLeft();
                    }
                }
                break;

            case 134:
                if (keydown) {
                    // 右键被按下
                    if (keyL != null) {
                        keyL.onKeyDownRight();
                    }
                } else {
                    // 右键松开
                    if (keyL != null) {
                        keyL.onKeyUpRight();
                    }
                }
                break;

            case 131:
                if (keydown) {
                    // "F1"键被按下
                    if (keyL != null) {
                        keyL.onKeyDownF1();
                    }
                } else {
                    //"F1"键松开
                    if (keyL != null) {
                        keyL.onKeyUpF1();

                    }
                }
                break;

            case 132:
                if (keydown) {
                    // "F2"键被按下
                    if (keyL != null) {
                        keyL.onKeyDownF2();
                    }
                } else {
                    // "F2"键松开
                    if (keyL != null) {
                        keyL.onKeyUpF2();
                    }
                }
                break;
        }

    }

}
