package com.grgbanking.pdamodel.fingerprint.dahua;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.grgbanking.pdamodel.fingerprint.FingerPrintListener;
import com.grgbanking.pdamodel.fingerprint.FingerPrintModel;
import com.grgbanking.pdamodel.tools.Tools;

public class DahuaFingerPrintModel implements FingerPrintModel {

    public static final int MATCH_CHARA = 50;//指纹比对分值大于50算匹配

    private final static int METHOD1 = 1;// 扫描指纹

    private final static int METHOD2 = 2;// 指纹比对

    private static DahuaFingerPrintModel model;

    private boolean isContinuous;

    private boolean isPause = true;

    private Handler handler;

    FingerPrintManager fpManager;

    private FingerPrintListener listener;

    private WorkThread workThread;

    private int method;

    private boolean hasImage;// 是否需要扫描出指纹图片，比较耗时，非必要勿使用

    private List<FingerData> fingerDatas;

    private DahuaFingerPrintModel(FingerPrintManager fpManager) {
        this.fpManager = fpManager;
    }

    public static DahuaFingerPrintModel getInstance() {
        if (model == null) {
            synchronized (DahuaFingerPrintModel.class) {
                if (model == null) {
                    model = new DahuaFingerPrintModel(FingerPrintManager.getInstance());
                }
            }
        }
        return model;
    }

    @Override
    public boolean open(FingerPrintListener listener) {
        // TODO Auto-generated method stub
        boolean isSuccess = false;
        try {
            this.listener = listener;
            this.handler = new Handler();
            if (!fpManager.isOpen()) {
                isSuccess = fpManager.open();
            } else {
                isSuccess = true;
            }
            workThread = new WorkThread();
            workThread.run = true;
            workThread.start();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    @Override
    public void startOrStop() {
        // TODO Auto-generated method stub
        if (isPause) {
            goOn();
        } else {
            pause();
        }

    }

    @Override
    public void pause() {
        onStopScan();
        isPause = true;
    }

    @Override
    public void goOn() {
        method = METHOD1;
        onStartScan();
        isPause = false;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        try {
            pause();
            if (fpManager.isOpen()) {
                fpManager.close();
            }
            if (workThread != null) {
                workThread.run = false;
                isPause = true;
                workThread.interrupt();
                workThread = null;
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    private void onReader(final byte[] rawData, final String hexStr, final Bitmap bp) {
        if (handler != null && listener != null) {
            Message.obtain(handler, new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    listener.onReader(rawData, hexStr, bp);
                }
            }).sendToTarget();
        }
    }

    private void onStartScan() {
        if (handler != null && listener != null) {
            Message.obtain(handler, new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    listener.onStartScan();
                }
            }).sendToTarget();
        }
    }

    private void onStopScan() {
        if (handler != null && listener != null) {
            Message.obtain(handler, new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    listener.onStopScan();
                }
            }).sendToTarget();
        }
    }

    private void onError(final String msg) {
        if (handler != null && listener != null) {
            Message.obtain(handler, new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    listener.onError(msg);
                }
            }).sendToTarget();
        }
    }

    private void onCompareSuccess(final int score, final FingerData fingerData, final byte[] fingerByte) {
        if (handler != null && listener != null) {
            Message.obtain(handler, new Runnable() {

                @Override
                public void run() {

                    listener.onCompareSuccess(score, fingerData, fingerByte);
                }
            }).sendToTarget();
        }
    }

    private class WorkThread extends Thread {
        public boolean run;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (run) {
                Log.e("isPause", isPause + "");
                if (!isPause) {
                    try {
                        boolean success = false;
                        switch (method) {
                        case METHOD1:
                            success = scanFinger();
                            if (!success) {
                                sleep(2000);
                                continue;
                            }
                            if (!isContinuous) {
                                pause();
                            }
                            break;
                        case METHOD2:
                            success = compareFinger();
                            if (!success) {
                                sleep(1000);
                                continue;
                            }
                            if (!isContinuous) {
                                pause();
                            }
                            break;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    sleep(2000);
                }
            }

            pause();

        }

        private void sleep(int time) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private boolean compareFinger() {
        if (fingerDatas == null || fingerDatas.size() <= 0) {
            return true;
        }

        // 将当前指纹特征存入buffer_a
        if (fpManager.genChara(FingerPrintManager.BUFFER_A)) {
            int score = 0;
            for (int i = 0; i < fingerDatas.size(); i++) {
                FingerData fingerData = fingerDatas.get(i);
                List<byte[]> fingerBytes = fingerData.getFingerData();
                if (fingerBytes == null || fingerBytes.size() <= 0) {
                    continue;
                }
                for (int j = 0; j < fingerBytes.size(); j++) {
                    byte[] bytes = fingerBytes.get(j);
                    if (bytes == null || bytes.length <= 0) {
                        continue;
                    }
                    boolean putFlag = fpManager.putChara(FingerPrintManager.BUFFER_B, bytes);
                    if (putFlag) {
                        // 比对A B缓存区的指纹模板，得出分值，当分值大于50可认为匹配成功
                        score = fpManager.matchChara();
                        if (score > 50) {
                            onCompareSuccess(score, fingerData, bytes);
                            return true;
                        }
                    }
                }
            }
            onCompareSuccess(score, null, null);
        }

        return false;
    }

    private boolean scanFinger() {
        Bitmap bp = null;
        byte[] templet = null;
        boolean isSuccess = false;
        if (hasImage) {
            Object[] bojs = fpManager.getFPImageAndChara(FingerPrintManager.BUFFER_A);
            if (bojs != null) {
                isSuccess = true;
                templet = (byte[]) bojs[0];
                bp = (Bitmap) bojs[1];
            }
        } else {
            if (fpManager.genChara(FingerPrintManager.BUFFER_A)) {
                isSuccess = true;
                templet = fpManager.getChara(FingerPrintManager.BUFFER_A);
            }
        }
        if (isSuccess) {
            onReader(templet, Tools.Bytes2HexString(templet, templet.length), bp);
        } else {
            onError("请将手指放入感应区域");
        }
        return isSuccess;
    }

    @Override
    public String getModelInfo() {
        // TODO Auto-generated method stub
        return null;
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
    public void setHasImage(boolean ishas) {
        this.hasImage = ishas;
    }

    @Override
    public void compareFinger(List<FingerData> fingerDatas) {
        if (fingerDatas == null || fingerDatas.size() <= 0) {
            return;
        }
        this.fingerDatas = fingerDatas;
        method = METHOD2;
        isPause = false;
    }

    public interface FingerData {
        public List<byte[]> getFingerData();
    }

}
