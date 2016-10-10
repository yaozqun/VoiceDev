package com.grgbanking.baselib.core;

import com.google.gson.reflect.TypeToken;
import com.grgbanking.baselib.config.AppConfig;
import com.grgbanking.baselib.core.callback.BaseCallback;
import com.grgbanking.baselib.core.callback.FileCallback;
import com.grgbanking.baselib.core.callback.JsonCallback;
import com.grgbanking.baselib.core.callback.ResultCallback;
import com.grgbanking.baselib.util.FileUtil;
import com.grgbanking.baselib.util.log.LogUtil;
import com.grgbanking.baselib.web.WebService;
import com.grgbanking.baselib.web.entity.ErrorMsg;
import com.grgbanking.baselib.web.okhttp.ProgressListener;
import com.grgbanking.baselib.web.request.VersionInfoReq;
import com.grgbanking.baselib.web.response.ResponseRoot;
import com.grgbanking.baselib.web.response.VersionInfoResponse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class SystemService extends BaseService {
    private static SystemService service;

    private SystemService() {
    }

    public static SystemService getInstance() {
        if (service == null) {
            synchronized (SystemService.class) {
                if (service == null) {
                    service = new SystemService();
                }
            }
        }
        return service;
    }

    /**
     * 下载文件
     *
     * @param resultCallback
     * @param progressListener
     */
    public void downLoadFile(String url, ResultCallback<File> resultCallback,
                             ProgressListener progressListener) {
        // http://hukai.me/images/android_perf_patterns_season_4.png
        // http://bos.nj.bpc.baidu.com/v1/nuomi-b-apk/BusinessApp.apk
        WebService.getInstance().asyncGet(
                url,
                new FileCallback(AppConfig.FILE_ROOT, resultCallback,
                        progressListener));
    }

    /**
     * 检查更新
     *
     * @param callback
     */
    public void checkUpgrade(String url, VersionInfoReq req, ResultCallback<VersionInfoResponse> callback) {
//    test(new JsonCallback<Void>(new TypeToken<ResponseRoot<Void>>() {
//    }.getType(), callback));

//        VersionInfoReq req = new VersionInfoReq();
//        req.phoneCode = SystemUtil.getDeviceId();
//        req.version = SystemUtil.getCurrentVersion();
        WebService.getInstance().asyncPost(url, req, new JsonCallback<VersionInfoResponse>(new TypeToken<ResponseRoot<VersionInfoResponse>>() {
        }.getType(), callback));
    }

    /**
     * 上传日志
     *
     * @param files            日志文件
     * @param callback         回调
     * @param progressListener 进度监听
     * @param delete           上传成功后删除文件
     */
    public void uploadLog(String url, final Map<String, File> files,
                          ResultCallback<Void> callback, ProgressListener progressListener,
                          final boolean delete) {
        WebService.getInstance().upLoadFile(url, "IISAPP",
                files, new BaseCallback<Void>(callback) {

                    @Override
                    public void onResponse(Call call, Response response)
                            throws IOException {
                        if (!response.isSuccessful()) {
                            LogUtil.i(TAG, "onResponse Failure:" + response.code());
                            onFailure(call, new ErrorMsg(response.code()));
                            return;
                        }
                        LogUtil.i(TAG, "上传日志成功");
                        // 上传成功后删除文件
                        if (delete) {
                            for (String key : files.keySet()) {
                                File file = files.get(key);
                                file.delete();
                            }
                        }
                        onSuccess(call, response, null);
                    }
                }, progressListener);

        // test(new BaseCallback<Void>(callback) {
        //
        // @Override
        // public void onResponse(Call call, Response response) throws IOException {
        // if (!response.isSuccessful()) {
        // LogUtil.i(TAG, "onResponse Failure:" + response.code());
        // onFailure(call, new ErrorMsg(response.code()));
        // return;
        // }
        // onSuccess(call, response, null);
        // }
        // }, progressListener);
    }

    /**
     * 把之前存在没上传成功的缓存文件再次上传
     *
     * @param path 日志缓存文件夹目录
     */
    public void uploadLog(String path) {
        File file = new File(path);
        if (!file.exists() || file.isFile()) {
            return;
        }
        File[] childFile = file.listFiles();
        if (childFile == null || childFile.length == 0) {
            return;
        }
        Map<String, File> files = new HashMap<String, File>();
        for (File f : childFile) {
            String name = f.getName();
            if ("zip".equals(FileUtil.getExtensionName(name))) {
                files.put(name, f);
            }
        }
        if (!files.isEmpty()) {
            uploadLog(path, files, null, null, true);
        }

    }

}