package com.grgbanking.baselib.ui.view.loading;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.grgbanking.baselib.R;
import com.grgbanking.baselib.ui.numberprogressbar.NumberProgressBar;


public class ShapeLoadingDialog {


    private Context mContext;
    private Dialog mDialog;
    private LoadingView mLoadingView;
    private View mDialogContentView;
    private Button btn;
    private NumberProgressBar numberProgressBar;


    public ShapeLoadingDialog(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        mDialog = new Dialog(mContext, R.style.custom_dialog);
        mDialogContentView = LayoutInflater.from(mContext).inflate(R.layout.base_dialog_loading_layout, null);
        numberProgressBar = (NumberProgressBar) mDialogContentView.findViewById(R.id.numberbar);
        mLoadingView = (LoadingView) mDialogContentView.findViewById(R.id.loadView);
        btn = (Button) mDialogContentView.findViewById(R.id.btn_cancel);
        mDialog.setContentView(mDialogContentView);
    }

    public void showProgressBar(boolean show) {
        numberProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showButton(boolean show) {
        btn.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setButtonClickListener(View.OnClickListener listener) {
        btn.setOnClickListener(listener);
    }

    public void setButtonText(CharSequence text) {
        btn.setText(text);
    }

    public void setProgress(int progress) {
        numberProgressBar.setProgress(progress);
    }

    public void incrementProgressBy(int progress) {
        numberProgressBar.incrementProgressBy(progress);
    }

    public void setBackground(int color) {
        GradientDrawable gradientDrawable = (GradientDrawable) mDialogContentView.getBackground();
        gradientDrawable.setColor(color);
    }

    public void setLoadingText(CharSequence charSequence) {
        mLoadingView.setLoadingText(charSequence);
    }

    public void show() {
        mDialog.show();

    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public void setCanceledOnTouchOutside(boolean flag) {
        mDialog.setCanceledOnTouchOutside(flag);
    }

    public void setCancelable(boolean flag) {
        mDialog.setCancelable(flag);
    }
}
