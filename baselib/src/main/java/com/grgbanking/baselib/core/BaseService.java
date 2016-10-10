package com.grgbanking.baselib.core;

import com.grgbanking.baselib.core.callback.BaseCallback;
import com.grgbanking.baselib.web.okhttp.ProgressListener;

public class BaseService {
    private static final int SLEEP_TIME = 5000;
    private static final int TEST_COUNT = 100;
    private static final int SLEEP_TIME_1 = 200;

    public void test(final BaseCallback callback) {
        test(callback, null);
    }

    public void test(final BaseCallback callback, final ProgressListener progressListener) {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (progressListener == null) {
                        Thread.sleep(SLEEP_TIME);
                    } else {
                        int i = 1;
                        while (i <= TEST_COUNT) {
                            progressListener.onProgress(i, TEST_COUNT);
                            i++;
                            Thread.sleep(SLEEP_TIME_1);
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                callback.onSuccess(null, null, null);
            }
        }.start();
    }
}
