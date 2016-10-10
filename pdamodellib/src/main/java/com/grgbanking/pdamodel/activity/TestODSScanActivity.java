package com.grgbanking.pdamodel.activity;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.pdamodel.R;
import com.grgbanking.pdamodel.ods.IODSModel;
import com.grgbanking.pdamodel.ods.ODSFactory;
import com.grgbanking.pdamodel.ods.ODSListener;

/**      
 * 条码扫描功能测试
 */

public class TestODSScanActivity extends Activity implements ODSListener {
    TextView tv_scan_barCode;

    String code;

    String leaveTime;

    private IODSModel mODSModel;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ods);
    }

    public void odsTest(View v) {
        tv_scan_barCode = (TextView) v;
        startOrStopScan();

    }

    @Override
    public boolean onODSReader(byte[] rawData, String str) {
        try {
            Log.e("条码", new String(rawData, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        code = new String(rawData);
        tv_scan_barCode.append("\n" + code);

        return true;
    }

    @Override
    public void onStartScan() {
    }

    @Override
    public void onStopScan() {
    }

    @Override
    public void onError(String str) {
    }

    protected boolean setContinueScan(boolean isContinue) {
        boolean isSuccess = false;
        if (mODSModel == null) {
            try {
                mODSModel = (IODSModel) new ODSFactory().createModel(this);
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(this, "激光扫描启动失败:" + e.getMessage(), 1).show();
            }

        } else {
            mODSModel.close();
        }
        if (mODSModel != null) {
            mODSModel.open(this, handler, this, isContinue);
            isSuccess = true;
        }
        return isSuccess;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // if (mODSModel != null) {
        // mODSModel.close();
        // }
    }

    /**
     * 
     * @Title: startScan
     * @Description:启动pda扫描
     */
    protected void startScan() {
        if (mODSModel != null) {
            mODSModel.goOn();
        }
    }

    /**
     * @Title: stopScan
     * @Description:停止扫描
     */
    protected void stopScan() {
        if (mODSModel != null) {
            mODSModel.pause();
        }
    }

    protected void startOrStopScan() {
        if (mODSModel != null) {
            mODSModel.startOrStop();
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        try {
            mODSModel = (IODSModel) new ODSFactory().createModel(this);
            if (mODSModel != null) {
                mODSModel.open(this, handler, this, false);
            }
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, "激光扫描启动失败:" + e.getMessage(), 1).show();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mODSModel != null) {
            mODSModel.close();
        }
        super.onPause();
    }

}
