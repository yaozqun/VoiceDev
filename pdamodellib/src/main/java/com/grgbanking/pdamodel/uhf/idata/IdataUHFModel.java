package com.grgbanking.pdamodel.uhf.idata;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.hdhe.uhf.reader.Tools;
import com.grgbanking.pdamodel.PDAManaufacturers;
import com.grgbanking.pdamodel.uhf.BlockConstant;
import com.grgbanking.pdamodel.uhf.IUHFModel;
import com.grgbanking.pdamodel.uhf.PowerGrade;
import com.grgbanking.pdamodel.uhf.UHFCardBean;
import com.grgbanking.pdamodel.uhf.UHFListener;
import com.impinj1.rfidapi.MemoryBank;
import com.impinj1.rfidapi.RadioCtrl;
import com.impinj1.rfidapi.ReadParms;
import com.impinj1.rfidapi.ReadResult;
import com.impinj1.rfidapi.SingulationCriteria;
import com.impinj1.rfidapi.SingulationCriteriaStatus;
import com.impinj1.rfidapi.ctrlOperateResult;
import com.impinj1.rfidapi.matchType;
import com.impinj1.rfidapi.radioBusyException;

/**   
 * 版权所有：2015-GRGBANKING
 * 项目名称：PDAModel   
 *
 * 类描述：使用子线程实现循环定时功能
 * 类名称：com.grgbanking.pdamodel.uhf.idata.IdataUHFModel     
 * 创建人：jqian
 * 创建时间：2015-11-23 下午3:03:16   
 * 修改人：
 * 修改时间：2015-11-23 下午3:03:16   
 * 修改备注：   
 * @version   V1.0    
 */

public class IdataUHFModel implements IUHFModel {
    public static IdataUHFModel model;

    private ScreenStateReceiver screenReceiver;

    private Context context;

    private boolean isPause = false;

    private int power;

    ReadParms parms;

    /**
     * 是否连续扫描
     */
    private boolean isContinuous;

    private Handler handler;

    private UHFListener uhfL;

    private RadioCtrl myRadio;

    /**
     * 是否只读取epc区
     */
    private boolean isEpcOnly;

    private UHFWorkThread workThread;

    private IdataUHFModel() {
        myRadio = new RadioCtrl();
    }

    public static IdataUHFModel getInstance() {
        if (model == null) {
            synchronized (IdataUHFModel.class) {
                if (model == null) {
                    model = new IdataUHFModel();
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
        this.context = context;
        this.power = getPower(powerGrade);
        this.handler = handler;
        this.isContinuous = isContinuous;
        this.isEpcOnly = isEpcOnly;
        this.uhfL = uhfL;
        try {

            screenReceiver = new ScreenStateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            context.registerReceiver(screenReceiver, filter);
            workThread = new UHFWorkThread();
            workThread.run = true;
            isPause = true;
            workThread.start();
            isSuccess = true;
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

    private int getPower(int powerGrade) {
        // 范围 5 ~ 23
        int power = 23;
        switch (powerGrade) {
        case PowerGrade.GRADE_ONE:
            power = 18;
            break;
        case PowerGrade.GRADE_TWO:
            power = 20;
            break;
        case PowerGrade.GRADE_THREE:
            power = 23;
            break;
        default:
            break;
        }
        return power;
    }

    @Override
    public void goOn() {
        isPause = false;
        if (workThread != null) {
            workThread.interrupt();
        }
        onStartScan();
    }

    @Override
    public void pause() {
        isPause = true;
    }

    @Override
    public void startOrStop() {
        // TODO Auto-generated method stub
        if (isPause) {
            isPause = false;
            onStartScan();
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
            if (myRadio.IsConnented()) {
                myRadio.DisconnectRadio();
            }
            myRadio.clearInventoryEventListener();
            if (context != null) {
                context.unregisterReceiver(screenReceiver);
            }
        } catch (Exception e) {
            // TODO: handle exception
            if (e != null && e.getMessage() != null) {
                e.printStackTrace();
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
        return false;
    }

    public class ScreenStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (PDAManaufacturers.DAHUA.equals(android.os.Build.MANUFACTURER)||PDAManaufacturers.DAHUA1.equals(android.os.Build.MANUFACTURER)||PDAManaufacturers.DAHUA2.equals(android.os.Build.MANUFACTURER)) {
                // 屏亮
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    Log.i("ScreenStateReceiver", "screen on");

                }// 屏灭
                else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    pause();
                    Log.i("ScreenStateReceiver", "screen off");
                }
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
   
                    Log.d("UHFWorkThread---R", "continue");
                    if (!isPause && isHandled) {

                        if (!myRadio.IsConnented()) {
                            ctrlOperateResult i = myRadio.ConnectRadio(context, 400, power, 4);
                            if (i != ctrlOperateResult.OK) {
                                Log.d("UHFWorkThread", "continue");
                                continue;
                            }
                        }
                        
                        onStartScan();
                        List<ReadResult> tagInfos = readCard();

                        if (handler != null && tagInfos != null && tagInfos.size() > 0 && uhfL != null) {
                            final List<byte[]> uhfList = new ArrayList<byte[]>();
                            final List<String> epcStrList = new ArrayList<String>();
                            final List<UHFCardBean> beanList = new ArrayList<UHFCardBean>();

                            for (int i = 0; i < tagInfos.size(); i++) {
                                ReadResult data = tagInfos.get(i);
                                String epc = data.getFlagID();

                                if (TextUtils.isEmpty(epc) && data.epc != null && data.epc.length > 0) {
                                    epc = shortArrToString(data.epc);
                                }

                                if (!TextUtils.isEmpty(epc)) {
                                    String epcUpper = epc.toUpperCase();
                                    if (epcStrList.contains(epcUpper)) {
                                        continue;
                                    }

                                    epcStrList.add(epcUpper);
                                    Log.d("epc", "" + epcUpper);
                                    byte[] epcArr = Tools.HexString2Bytes(epcUpper);
                                    uhfList.add(epcArr);
                                    if (!isEpcOnly) {
                                        String tipStr = null;
                                        if (data.readData != null && data.readData.length > 0) {
                                            tipStr = shortArrToString(data.readData);
                                        } else {
                                            // ReadResult tipResult =
                                            // readTIP(epc);
                                            // tipStr =
                                            // shortArrToString(tipResult.readData);
                                        }

                                        if (!TextUtils.isEmpty(tipStr)) {
                                            UHFCardBean bean = new UHFCardBean();
                                            bean.setTidArr(Tools.HexString2Bytes(tipStr));
                                            bean.setEpcArr(epcArr);
                                            beanList.add(bean);
                                            Log.d("tip", "" + tipStr);
                                        }

                                    }

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
                                    uhfL.onUHFReader(uhfList, epcStrList, beanList);
                                    isHandled = true;
                                }
                            }).sendToTarget();
                        }
                    } else {
                        onStopScan();
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
                        onStopScan();
                    }
                }
            }
            onStopScan();
        }
    }

    private void onStartScan() {
        if (handler != null && uhfL != null) {
            Message.obtain(handler, new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    uhfL.onStartScan();
                }
            }).sendToTarget();
        }
    }

    private void onStopScan() {
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

    private synchronized List<ReadResult> readCard() {
        // 进行标作签读取操
        try {
            parms = new ReadParms();
            parms.memBank = MemoryBank.TID;
            parms.offset = 0;
            parms.length = BlockConstant.SIZE_TID;
            parms.accesspassword = 0;
            return myRadio.TagInfoRead(parms);
        } catch (radioBusyException e) {
            // TODO Auto-generated catch block
            cancelBusy();
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private synchronized ReadResult readTIP(String maskValue) {
        ReadResult readResult = null;
        // 进行标作签读取操
        try {
            SingulationCriteria singulationCriteria = new SingulationCriteria();
            singulationCriteria.status = SingulationCriteriaStatus.Enabled;
            singulationCriteria.offset = 0;
            singulationCriteria.count = maskValue.length() * 4;
            singulationCriteria.match = matchType.Regular;

            for (int i = 0; i < (maskValue.length() / 2); i++) {
                singulationCriteria.mask[i] = (byte) (Short.parseShort(maskValue.substring(i * 2, i * 2 + 2), 16) & 0x00FF);
            }

            myRadio.SetMatchCriteria(singulationCriteria);

            List<ReadResult> tagInfos = myRadio.TagInfoRead(parms);
            if (tagInfos.size() > 0) {
                // 取一个进行显示
                readResult = tagInfos.get(tagInfos.size() - 1);
            }
            SingulationCriteria dissingulationCriteria = new SingulationCriteria();
            dissingulationCriteria.status = SingulationCriteriaStatus.Disabled;
            dissingulationCriteria.offset = 0;
            dissingulationCriteria.count = 0;
            dissingulationCriteria.match = matchType.Regular;

            ctrlOperateResult result = myRadio.SetMatchCriteria(dissingulationCriteria);
        } catch (radioBusyException e) {
            // TODO Auto-generated catch block
            cancelBusy();
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return readResult;
    }

    private void cancelBusy() {
        Class classType = (Class) myRadio.getClass();
        try {
            Field field = classType.getDeclaredField("isBusy");
            field.setAccessible(true);
            field.set(myRadio, false);
            myRadio.RadioReset();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String shortArrToString(short[] arr) {
        if (arr == null || arr.length <= 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            sb.append(Integer.toHexString(((arr[i] >> 8) & 0x000000FF) | 0xFFFFFF00).substring(6)
                    + Integer.toHexString((arr[i] & 0x000000FF) | 0xFFFFFF00).substring(6));
        }
        return sb.toString().toUpperCase();
    }

}
