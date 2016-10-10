package com.grgbanking.pdamodel.uhf.dahua;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
import com.grgbanking.pdamodel.PDAManaufacturers;
import com.grgbanking.pdamodel.uhf.BlockConstant;
import com.grgbanking.pdamodel.uhf.IUHFModel;
import com.grgbanking.pdamodel.uhf.PowerGrade;
import com.grgbanking.pdamodel.uhf.UHFCardBean;
import com.grgbanking.pdamodel.uhf.UHFListener;
import com.grgbanking.pdamodel.uhf.dahua.KeyStateReceiver.ScanKeyListener;

/**
 * 版权所有：2015-GRGBANKING 项目名称：PDAModel
 * <p>
 * 类描述：达华UHF模块实现 类名称：com.grgbanking.pdamodel.uhf.dahua.DaHuaUHFModel
 * 创建人：G0212269 创建时间：2015-8-24 下午3:22:54 修改人： 修改时间：2015-8-24 下午3:22:54 修改备注：
 *
 * @version V1.0
 */
public class DaHuaUHFModel implements IUHFModel {
    public static DaHuaUHFModel model;

    public static Object lock = new Object();

    private UhfReader reader;

    private ScreenStateReceiver screenReceiver;

    private KeyStateReceiver keyReceiver;

    private Context context;

    private UHFWorkThread workThread;

    private boolean isPause = false;

    private int power = DistanceConstant.SCAN_DIS_MIDDLE;

    /**
     * 是否连续扫描
     */
    private boolean isContinuous;

    private Handler handler;

    private UHFListener uhfL;

    /**
     * 是否只读取epc区
     */
    private boolean isEpcOnly;

    private int scanCards = 0; //扫描到的卡的数量

    private static String ON_ERROR = "对不起，未扫描到标签，请确认！";

    private DaHuaUHFModel() {
    }

    public static DaHuaUHFModel getInstance() {
        if (model == null) {
            synchronized (lock) {
                if (model == null) {
                    model = new DaHuaUHFModel();
                }
            }
        }
        return model;
    }

    @Override
    public String getModelInfo() {
        // TODO Auto-generated method stub
        return "方法暂未实现";
    }

    @Override
    public boolean open(Context context, Handler handler, UHFListener uhfL, boolean isContinuous, boolean isEpcOnly,
                        int powerGrade) {
        // TODO Auto-generated method stub
        boolean isSuccess = false;
        close();
        try {
            reader = UhfReader.getInstance();
            if (reader != null) {
                // byte[] versionBytes = reader.getFirmware();
                this.context = context;
                this.power = getPower(powerGrade);
                this.handler = handler;
                this.isContinuous = isContinuous;
                this.isEpcOnly = isEpcOnly;
                this.uhfL = uhfL;
                screenReceiver = new ScreenStateReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_SCREEN_ON);
                filter.addAction(Intent.ACTION_SCREEN_OFF);
                context.registerReceiver(screenReceiver, filter);
                keyReceiver = new KeyStateReceiver(new ScanKeyListener() {

                    @Override
                    public void onKeyUp() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onKeyDown() {
                        // TODO Auto-generated method stub
                        startOrStop();
                    }
                });
                filter = new IntentFilter();
                filter.addAction("android.intent.action.FUN_KEY");
                context.registerReceiver(keyReceiver, filter);
                workThread = new UHFWorkThread();
                workThread.run = true;
                isPause = true;
                workThread.start();
                isSuccess = true;

            } else {
                isSuccess = false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            if (e != null && e.getMessage() != null) {
                e.printStackTrace();
            }

            isSuccess = false;
        }
        if (!isSuccess) {
            close();
        }
        return isSuccess;
    }

    public void pause() {
        isPause = true;
    }

    public void goOn() {
        isPause = false;
        if (workThread != null) {
            workThread.interrupt();
        }
    }

    private int getPower(int powerGrade) {
        // TODO Auto-generated method stub
        int power = DistanceConstant.SCAN_DIS_MIDDLE;
        switch (powerGrade) {
            case PowerGrade.GRADE_ONE:
                power = DistanceConstant.SCAN_DIS_CLOSE;
                break;
            case PowerGrade.GRADE_TWO:
                power = DistanceConstant.SCAN_DIS_MIDDLE;
                break;
            case PowerGrade.GRADE_THREE:
                power = DistanceConstant.SCAN_DIS_FAR;
                break;
            default:
                break;
        }
        return power;
    }

    @Override
    public void startOrStop() {
        // TODO Auto-generated method stub
        if (isPause && reader != null) {
            try {
                reader.setOutputPower(power);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            isPause = false;
        } else {
            isPause = true;
        }
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        try {
            if (workThread != null) {
                workThread.run = false;
                workThread = null;
            }
            if (reader != null) {
                reader.close();
            }

            if (context != null) {
                context.unregisterReceiver(screenReceiver);
                context.unregisterReceiver(keyReceiver);
            }
        } catch (Exception e) {
            // TODO: handle exception
            if (e != null && e.getMessage() != null) {
                e.printStackTrace();
            }

        }
    }

    private class UHFWorkThread extends Thread {
        public boolean run;

        private boolean isHandled = true;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (run) {
                try {
                    if (!isPause && isHandled) {
                        if (handler != null && uhfL != null) {
                            Message.obtain(handler, new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    uhfL.onStartScan();
                                }
                            }).sendToTarget();
                        }

                        final List<byte[]> uhfList = reader.inventoryRealTime();
                        if (handler != null && uhfList != null && uhfList.size() > 0 && uhfL != null) {
                            final List<String> epcStrList = new ArrayList<String>();
                            final List<UHFCardBean> beanList = new ArrayList<UHFCardBean>();
                            for (int i = 0; i < uhfList.size(); i++) {
                                byte[] data = uhfList.get(i);
                                if (data != null && data.length >= 12) {
                                    if (!isEpcOnly) {// 如果不是只读epc,则还要读tid区
                                        reader.selectEPC(data);
                                        byte[] tid = reader.readFrom6C(BlockConstant.TID_NO, 0, BlockConstant.SIZE_TID,
                                                BlockConstant.PASSWORD_TID);
                                        if (tid != null && tid.length >= 12) {
                                            UHFCardBean bean = new UHFCardBean();
                                            bean.setEpcArr(data);
                                            bean.setTidArr(tid);
                                            beanList.add(bean);
                                        } else {// 如果首次读取失败，则最多再读三次
                                            int time = 3;
                                            for (int j = 0; j < time; j++) {
                                                reader.selectEPC(data);
                                                tid = reader.readFrom6C(BlockConstant.TID_NO, 0,
                                                        BlockConstant.SIZE_TID, BlockConstant.PASSWORD_TID);
                                                if (tid != null && tid.length >= 24) {
                                                    UHFCardBean bean = new UHFCardBean();
                                                    bean.setEpcArr(data);
                                                    bean.setTidArr(tid);
                                                    beanList.add(bean);
                                                    break;
                                                }
                                                Log.i("time", "---------" + j + "---------");
                                            }

                                        }
                                    }
                                    epcStrList.add(Tools.Bytes2HexString(data, data.length));

                                }
                            }
                            if (!isContinuous) {
                                isPause = true;
                            }
                            isHandled = false;
                            Message.obtain(handler, new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    if (beanList.size() > 0) {
                                        uhfL.onUHFReader(uhfList, epcStrList, beanList);
                                    } else {
                                        uhfL.onError(ON_ERROR);
                                    }

                                    isHandled = true;
                                }
                            }).sendToTarget();
                        }
                    } else {
                        if (handler != null && uhfL != null) {
                            Message.obtain(handler, new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    uhfL.onStopScan();
                                }
                            }).sendToTarget();
                        }
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO: handle exception
                        }

                    }

                } catch (final Exception e) {
                    // TODO: handle exception
                    if (e != null && e.getMessage() != null) {
                        e.printStackTrace();
                        if (handler != null && uhfL != null) {
                            Message.obtain(handler, new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    uhfL.onError(e.getMessage());
                                }
                            }).sendToTarget();
                        }
                    }
                }
            }
            if (handler != null && uhfL != null) {
                Message.obtain(handler, new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        uhfL.onStopScan();
                    }
                }).sendToTarget();
            }
        }
    }

    @Override
    public boolean isScaning() {
        return !isPause;
    }

    @Override
    public void setContinuous(boolean isContinuous) {
        this.isContinuous = isContinuous;
    }

    @Override
    public boolean writeData(Context context, Handler handler, UHFListener uhfL, int powerGrade, String writeData) {
        this.context = context;
        this.power = getPower(powerGrade);
        this.handler = handler;
        this.uhfL = uhfL;

        reader = UhfReader.getInstance();

        boolean writeFlag = false;
        final List<byte[]> uhfList = reader.inventoryRealTime();
        final List<String> epcStrList = new ArrayList<String>();
        final List<UHFCardBean> beanList = new ArrayList<UHFCardBean>();
        if (handler != null && uhfList != null && uhfList.size() > 0 && uhfL != null) {
            for (int i = 0; i < uhfList.size(); i++) {
                byte[] data = uhfList.get(i);
                if (data != null && data.length >= 12) {
                    if (!isEpcOnly) {// 如果不是只读epc,则还要读tid区
                        reader.selectEPC(data);
                        byte[] tid = reader.readFrom6C(BlockConstant.TID_NO, 0, BlockConstant.SIZE_TID,
                                BlockConstant.PASSWORD_TID);
                        if (tid != null && tid.length >= 12) {
                            UHFCardBean bean = new UHFCardBean();
                            bean.setEpcArr(data);
                            bean.setTidArr(tid);
                            beanList.add(bean);
                        } else {// 如果首次读取失败，则最多再读三次
                            int time = 3;
                            for (int j = 0; j < time; j++) {
                                reader.selectEPC(data);
                                tid = reader.readFrom6C(BlockConstant.TID_NO, 0,
                                        BlockConstant.SIZE_TID, BlockConstant.PASSWORD_TID);
                                if (tid != null && tid.length >= 24) {
                                    UHFCardBean bean = new UHFCardBean();
                                    bean.setEpcArr(data);
                                    bean.setTidArr(tid);
                                    beanList.add(bean);
                                    break;
                                }
                                Log.i("time", "---------" + j + "---------");
                            }
                            epcStrList.add(Tools.Bytes2HexString(data, data.length));
                        }
                    }
                }
            }
        }

        Log.i("beanList.size()", beanList.size() + "");
        if (beanList == null || beanList.size() < 1) {
            uhfL.onError(ON_ERROR);
            uhfL.onStopScan();
            return writeFlag;
        } else if (beanList.size() > 1) {
            uhfL.onError("已扫描到" + beanList.size() + "张标签，请确保每次只写一张标签！");
            uhfL.onStopScan();
            return writeFlag;
        } else {
            byte[] dataBytes = Tools.HexString2Bytes("00" + writeData);
            //dataLen = dataBytes/2 dataLen是以字为单位的
            writeFlag = reader.writeTo6C(Tools.HexString2Bytes("00000000"), BlockConstant.EPC_NO, 5, dataBytes.length / 2, dataBytes);
            uhfL.writeData(beanList,writeFlag);
            if (!writeFlag) {
                uhfL.onError("写数据失败，请重试！");
            }
            uhfL.onStopScan();
            return writeFlag;
        }

    }

    public class ScreenStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (PDAManaufacturers.DAHUA.equals(android.os.Build.MANUFACTURER)||PDAManaufacturers.DAHUA1.equals(android.os.Build.MANUFACTURER)||PDAManaufacturers.DAHUA2.equals(android.os.Build.MANUFACTURER)) {
                if (reader == null) {
                    return;
                }
                // 屏亮
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    reader.powerOn();
                    Log.i("ScreenStateReceiver", "screen on");

                }// 屏灭
                else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    reader.powerOff();
                    Log.i("ScreenStateReceiver", "screen off");
                }
            }

        }

    }
}
