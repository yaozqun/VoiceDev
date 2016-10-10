package com.grgbanking.pdamodel.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.pdamodel.R;
import com.grgbanking.pdamodel.fingerprint.FingerPrintFactory;
import com.grgbanking.pdamodel.fingerprint.FingerPrintListener;
import com.grgbanking.pdamodel.fingerprint.FingerPrintModel;
import com.grgbanking.pdamodel.fingerprint.dahua.DahuaFingerPrintModel.FingerData;

/**   
 * 指纹扫描测试  
 */

public class TestFingerPrintActivity extends Activity implements FingerPrintListener {

    ImageView test_imageView_fp;

    TextView test_textView_tips;

    EditText editText_option_name;

    private FingerPrintModel fpModel; // 指纹操作句柄

    boolean insertData = false, scanImage = true;

    ScrollView scrollView;

    private List<MyFingerData> fingerDatas;

    private int step;

    private MyFingerData tempMyFingerData;

    Handler handler = new Handler();
    Vibrator vib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_finger);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        test_imageView_fp = (ImageView) findViewById(R.id.test_imageView_fp);
        test_textView_tips = (TextView) findViewById(R.id.test_textView_tips);
        editText_option_name = (EditText) findViewById(R.id.editText_option_name);
        scrollView = (ScrollView) findViewById(R.id.test_ScrollView);
        fingerDatas = new ArrayList<MyFingerData>();
        try {
            fpModel = FingerPrintFactory.createModel(this);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, "激光扫描启动失败:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        if (fpModel != null) {

            fpModel.open(this);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (fpModel != null) {
            fpModel.close();
        }
        super.onPause();
    }

    @Override
    public boolean onReader(byte[] rawData, String hexStr, Bitmap bp) {
        if (bp != null) {
            test_imageView_fp.setImageBitmap(bp);
        }
        vib.vibrate(500);
        if (!insertData) {

            if (hexStr != null) {
                setText("\n掃描成功:\n" + hexStr + "---length:" + hexStr.length());
            }

        } else {
            switch (step) {
            case 1:
                tempMyFingerData.finger1[0] = rawData;
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        setText("\n第一次扫描成功，请再次扫描:");
                        step = 2;
                        fpModel.setContinuous(false);
                        fpModel.setHasImage(false);
                        fpModel.goOn();
                    }
                }, 3000);

                break;

            case 2:
                tempMyFingerData.finger1[1] = rawData;
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        setText("\n第二次扫描成功，请换个手指扫描:");
                        step = 3;
                        fpModel.setContinuous(false);
                        fpModel.setHasImage(false);
                        fpModel.goOn();
                    }
                }, 3000);

                break;
            case 3:
                tempMyFingerData.finger2[0] = rawData;
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        setText("\n第三次扫描成功，请再次扫描:");
                        step = 4;
                        fpModel.setContinuous(false);
                        fpModel.setHasImage(false);
                        fpModel.goOn();
                    }
                }, 3000);

                break;
            case 4:
                step = 0;
                tempMyFingerData.finger2[1] = rawData;
                fingerDatas.add(tempMyFingerData);
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        setText("\n指纹成功录入:当前指纹数量   "+fingerDatas.size());
                    }
                }, 1000);

                break;
            case 0:
                tempMyFingerData = null;
                setText("\n取消录入指纹:");
                break;
            }
        }
        return false;
    }

    private void setText(String str) {
        test_textView_tips.append(str);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 1000);

    }

    @Override
    public void onStartScan() {
        setText("\n开始扫描啦，请将手指放入感应区...");

    }

    @Override
    public void onStopScan() {
        setText("\n停止扫描啦...");

    }

    @Override
    public void onError(String str) {
        setText("\n扫描出错：" + str);
    }

    public void testSinglescan(View v) {
        insertData = false;
        fpModel.setContinuous(false);
        fpModel.setHasImage(scanImage);
        fpModel.startOrStop();

    }

    public void testMulscan(View v) {
        insertData = false;
        fpModel.setContinuous(true);
        fpModel.setHasImage(scanImage);
        fpModel.startOrStop();

    }

    public void testscanImage(View v) {
        TextView btn = (TextView) v;
        if ("扫描带图片".equals(btn.getText().toString())) {
            scanImage = true;
            btn.setText("扫描不带图片");
        } else {
            scanImage = false;
            btn.setText("扫描带图片");
        }
    }

    public void testinsertFinger(View v) {
        String name = editText_option_name.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入姓名进行指纹录入", Toast.LENGTH_LONG).show();
            return;
        }
        if (step == 0) {
            step = 1;
        } else {
            step = 0;
            return;
        }
        setText("\n开始指纹录入功能...");

        tempMyFingerData = new MyFingerData();
        tempMyFingerData.name = name;
        insertData = true;
        fpModel.setContinuous(false);
        fpModel.setHasImage(scanImage);
        fpModel.goOn();
    }

    public void testsearchFinger(View v) {
        setText("\n开始指纹检索功能...");
        List<FingerData> ff = new ArrayList<FingerData>();
        for (MyFingerData mm : fingerDatas) {
            ff.add(mm);
        }
        fpModel.compareFinger(ff);

    }

    class MyFingerData implements FingerData {
        public String name;

        public byte[][] finger1;

        public byte[][] finger2;

        public MyFingerData() {
            finger1 = new byte[2][];
            finger2 = new byte[2][];
        }

        @Override
        public List<byte[]> getFingerData() {
            List<byte[]> list = new ArrayList<byte[]>();
            list.add(finger1[0]);
            list.add(finger1[1]);
            list.add(finger2[0]);
            list.add(finger2[1]);
            return list;
        }

    }

    @Override
    public void onCompareSuccess(int score, FingerData fingerData, byte[] fingerByte) {
        if(score>44){
            MyFingerData mMyFingerData = (MyFingerData) fingerData;
            Toast.makeText(this, "你的姓名是：" + mMyFingerData.name, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "不匹配"+score, Toast.LENGTH_LONG).show();
        }

    }
}
