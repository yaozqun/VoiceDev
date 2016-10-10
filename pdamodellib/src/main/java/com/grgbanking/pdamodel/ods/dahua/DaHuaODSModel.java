package com.grgbanking.pdamodel.ods.dahua;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.pda.serialport.SerialPort;

import com.grgbanking.pdamodel.ods.IODSModel;
import com.grgbanking.pdamodel.ods.ODSListener;
import com.grgbanking.pdamodel.ods.dahua.KeyStateReceiver.ScanKeyListener;
import com.grgbanking.pdamodel.tools.Tools;

public class DaHuaODSModel implements IODSModel {
    private static DaHuaODSModel model;

    private static Object lock = new Object();

    private boolean isContinuous;

    private boolean isPause = true;

    private SerialPort mSerialPort;

    private int port = 0;

    private int baudrate = 9600;

    private int flags = 0;

    private Handler handler;

    private InputStream is;

    private OutputStream os;

    private ODSListener odsL;

    private ODSWorkThread workThread;

    private KeyStateReceiver keyReceiver;

    private Context context;

    private DaHuaODSModel() {
    }

    public static DaHuaODSModel getInstance() {
        if (model == null) {
            synchronized (lock) {
                if (model == null) {
                    model = new DaHuaODSModel();
                }
            }
        }
        return model;
    }

    @Override
    public String getModelInfo() {
        // TODO Auto-generated method stub
        return "方法暂时未实现";
    }

    @Override
    public boolean open(final Context context, Handler handler, ODSListener odsL, boolean isContinuous) {
        // TODO Auto-generated method stub
        boolean isSuccess = false;
        try {
            close();
            this.context = context;
            this.odsL = odsL;
            this.isContinuous = isContinuous;
            this.handler = handler;
            mSerialPort = new SerialPort(port, baudrate, flags);
            mSerialPort.scaner_poweron();
            is = mSerialPort.getInputStream();
            os = mSerialPort.getOutputStream();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /** clear useless data **/
            byte[] temp = new byte[128];
            is.read(temp);

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

            IntentFilter filter = new IntentFilter();
            filter = new IntentFilter();
            filter.addAction("android.intent.action.FUN_KEY");
            context.registerReceiver(keyReceiver, filter);

            workThread = new ODSWorkThread();
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
        if (mSerialPort != null) {
            if (mSerialPort.scaner_trig_stat()) {
                isPause = true;
                mSerialPort.scaner_trigoff();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                isPause = false;
                mSerialPort.scaner_trigon();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        if (mSerialPort != null) {
            if (mSerialPort.scaner_trig_stat()) {
                isPause = true;
                mSerialPort.scaner_trigoff();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void goOn() {
        // TODO Auto-generated method stub
        if (mSerialPort != null) {
            if (!mSerialPort.scaner_trig_stat()) {
                isPause = false;
                mSerialPort.scaner_trigon();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        try {
            pause();
            if (mSerialPort != null) {
                mSerialPort.scaner_poweroff();
                if (os != null) {
                    os.close();
                    os.close();
                    os = null;
                }
                if (is != null) {
                    is.close();
                    is.close();
                    is = null;
                }
                mSerialPort.close(port);
                mSerialPort = null;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (workThread != null) {
                workThread.run = false;
                isPause = true;
                workThread.interrupt();
                workThread = null;
            }
            if (context != null) {
                context.unregisterReceiver(keyReceiver);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    private class ODSWorkThread extends Thread {
        public boolean run;

        private boolean isHandled = true;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (run) {
                Log.e("isPause", isPause + "");
                Log.e("isHandled", isHandled + "");
                if (!isPause && isHandled) {
                    if (handler != null && odsL != null) {
                        Message.obtain(handler, new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                odsL.onStartScan();
                            }
                        }).sendToTarget();
                    }
                    try {
                        int len = is.available();
                        if (len > 0) {
                            try {
                                sleep(50);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            while (is.available() != len) {
                                len = is.available();
                                try {
                                    sleep(50);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                            final byte[] buffer = new byte[len];
                            is.read(buffer);
                            if (handler != null && odsL != null) {
                                isHandled = false;
                                Message.obtain(handler, new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        odsL.onODSReader(buffer, Tools.Bytes2HexString(buffer, buffer.length));
                                        isHandled = true;
                                    }
                                }).sendToTarget();
                            }
                            if (isContinuous) {
                                pause();
                                goOn();
                            } else {
                                pause();
                            }
                        } else {
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    if (handler != null && odsL != null) {
                        Message.obtain(handler, new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                odsL.onStopScan();
                            }
                        }).sendToTarget();
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO: handle exception
                    }
                }
            }
            if (handler != null && odsL != null) {
                Message.obtain(handler, new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        odsL.onStopScan();
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

}
