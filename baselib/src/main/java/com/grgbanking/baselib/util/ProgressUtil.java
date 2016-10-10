package com.grgbanking.baselib.util;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressUtil {
	
	public static ProgressDialog show(Context context,String msg){
		ProgressDialog dialog = ProgressDialog.show(context, "", msg, false, true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		return dialog;
	}
	
}
