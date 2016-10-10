package com.grgbanking.pdamodel.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.Tools;
import com.grgbanking.pdamodel.IModelFactory;
import com.grgbanking.pdamodel.R;
import com.grgbanking.pdamodel.uhf.IUHFModel;
import com.grgbanking.pdamodel.uhf.PowerGrade;
import com.grgbanking.pdamodel.uhf.UHFCardBean;
import com.grgbanking.pdamodel.uhf.UHFFactory;
import com.grgbanking.pdamodel.uhf.UHFListener;
import com.grgbanking.pdamodel.uhf.dahua.DistanceConstant;

import java.util.ArrayList;
import java.util.List;

public class TestUHFActivity extends Activity implements UHFListener {
    private IUHFModel mUHFModel;

    private ListView boxList;

    private ProgressBar pb;

    private Handler handler = new Handler();

    private int powerGrade;

    private ArrayList<String> data;

    private HandOverBoxAdapter handOverBoxAdapter;

    boolean isflite = false;

    private EditText et_data;

    private Button bt_write;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_uhf);
        powerGrade = getIntent().getIntExtra("PowerGrade", PowerGrade.GRADE_ONE);
        data = new ArrayList<String>();
        initView();
        initData();
    }

    public void initUHFModel() {
        if (mUHFModel == null) {
            IModelFactory factory = new UHFFactory();
            try {
                mUHFModel = (IUHFModel) factory.createModel(this);
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(this, "error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        if (mUHFModel != null) {
            mUHFModel.open(this, handler, this, false, false, powerGrade);
        }

    }

    private void initData() {
        handOverBoxAdapter = new HandOverBoxAdapter();
        boxList.setAdapter(handOverBoxAdapter);
    }

    private void initView() {
        pb = (ProgressBar) findViewById(R.id.test_ProgressBar);
        boxList = (ListView) findViewById(R.id.test_ListView);

        et_data = (EditText) findViewById(R.id.et_data);
        bt_write = (Button) findViewById(R.id.bt_write);

        bt_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUHFModel.writeData(TestUHFActivity.this,handler,TestUHFActivity.this, DistanceConstant.SCAN_DIS_CLOSE,et_data.getText().toString());
            }
        });
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        // UHFHelper.getInstance().close();
        if (mUHFModel != null) {
            mUHFModel.close();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initUHFModel();
    }

    @Override
    public boolean onUHFReader(List<byte[]> epcList, List<String> epcHexStrList, List<UHFCardBean> beanList) {
        if (beanList == null || beanList.size() <= 0) {
            return true;
        }
        for (UHFCardBean bean : beanList) {
            String tid = Tools.Bytes2HexString(bean.getTidArr(), bean.getTidArr().length);
            String ecp = Tools.Bytes2HexString(bean.getEpcArr(), bean.getEpcArr().length);
            String dStr = "TID：  " + tid + "\nRCP：  " + ecp;
            if (isflite) {
                if (!data.contains(dStr)) {
                    data.add(dStr);
                } else {
                    Toast.makeText(this, "扫描到重复数据：\n" + dStr, Toast.LENGTH_SHORT).show();
                }
            } else {
                data.add(dStr);
            }

        }

        handOverBoxAdapter.notifyDataSetChanged();
        if (data.size() > 0) {
            boxList.setSelection(data.size() - 1);
        }
        return true;
    }

    @Override
    public void onStartScan() {
        if (pb != null) {
            pb.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onStopScan() {
        if (pb != null) {
            pb.setVisibility(View.GONE);
        }

    }

    @Override
    public void onError(String str) {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeData(List<UHFCardBean> beanList,boolean writeSuccess) {
        if(writeSuccess){
            Toast.makeText(TestUHFActivity.this,"数据写入成功",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(TestUHFActivity.this,"数据写入失败",Toast.LENGTH_LONG).show();
        }
    }

    public void startOrStopScan() {
        if (mUHFModel != null) {
            mUHFModel.startOrStop();
        }
    }

    public void startScan() {
        if (mUHFModel != null) {
            mUHFModel.goOn();
        }
    }

    public void setContinuousScan(boolean isContinuous) {
        if (mUHFModel != null) {
            mUHFModel.setContinuous(isContinuous);
        }
    }

    public boolean isScaning() {
        if (mUHFModel != null) {
            return mUHFModel.isScaning();
        }
        return false;
    }

    public void testSinglescan(View v) {
        setContinuousScan(false);
        startOrStopScan();
    }

    public void testMulscan(View v) {
        setContinuousScan(true);
        startOrStopScan();
    }

    public void fliteData(View v) {
        TextView btn = (TextView) v;
        String text = btn.getText().toString();
        if ("过滤重复数据".equals(text)) {
            btn.setText("不过滤重复数据");
            isflite = true;
        } else {
            btn.setText("过滤重复数据");
            isflite = false;
        }
    }

    public void clearData(View v) {
        data.clear();
        handOverBoxAdapter.notifyDataSetChanged();
    }

    public class HandOverBoxAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data != null ? data.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            String str = data.get(position);
            TextView tv = new TextView(getBaseContext());
            tv.setText("扫描到第  " + (position + 1) + "张卡\n" + str);
            return tv;
        }

    }

}
