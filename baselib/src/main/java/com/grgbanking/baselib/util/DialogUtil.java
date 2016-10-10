package com.grgbanking.baselib.util;

import android.content.Context;

import com.grgbanking.baselib.ui.dialog.CustomDialog;
import com.grgbanking.baselib.ui.dialog.Effectstype;
import com.grgbanking.baselib.R;

/**
 * Created by Administrator on 2016/6/20.
 */
public class DialogUtil {
    private static final int DURATION = 300;
    public static CustomDialog getDialog(Context context) {
        CustomDialog dialog = new CustomDialog(context, R.style.dialog_untran);
        dialog.withTitle("提 示")                                  //.withTitle(null)  no title
                //  .withTitleColor("#99000000")                                  //def
                //   .withDividerColor("#11000000")                              //def
                .withMessage("This is a modal Dialog.")                     //.withMessage(null)  no Msg
                        //  .withMessageColor("#aa000000")                              //def  | withMessageColor(int resid)
                .withDialogColor("#883696DF")
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .withDuration(DURATION)                                          //def
                .withEffect(Effectstype.RotateBottom)                                         //def Effectstype.Slidetop
                .withButton1Text("确 定")
                .withButton2Text("取 消")                                  //def gone
                .setCustomView(R.layout.base_dialog_custom_view, context)         //.setCustomView(View or ResId,context)
        //   .setButton1TextColor("#aa000000")
        //  .setButton2TextColor("#aa000000")
        ;

        return dialog;
    }

}

