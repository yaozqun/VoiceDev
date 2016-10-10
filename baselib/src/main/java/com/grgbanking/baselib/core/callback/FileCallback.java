package com.grgbanking.baselib.core.callback;

import com.grgbanking.baselib.util.log.LogUtil;
import com.grgbanking.baselib.web.entity.ErrorMsg;
import com.grgbanking.baselib.web.okhttp.ProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class FileCallback extends BaseCallback<File> {
    public final static String TAG = "FileCallback";
    private String filePath;
    private long contentLength;
    private String url;
    private final ProgressListener progressListener;

    private static final int SIZE = 2048;

    /**
     * @param filePath       保存路径
     * @param resultCallback
     */
    public FileCallback(String filePath, ResultCallback<File> resultCallback, ProgressListener progressListener) {
        super(resultCallback);
        this.filePath = filePath;
        this.progressListener = progressListener;
    }

    public FileCallback(String filePath, ResultCallback<File> resultCallback, CallbackHandler handler, ProgressListener progressListener) {
        super(resultCallback, handler);
        this.filePath = filePath;
        this.progressListener = progressListener;
    }

    @Override
    public void onResponse(Call call, Response response) {
        try {
            if (!response.isSuccessful()) {
                LogUtil.i(TAG, "onResponse Failure:" + response.code());
                onFailure(call, new ErrorMsg(response.code()));
                return;
            }
            url = response.request().url().url().toString();
            ResponseBody body = response.body();
            this.contentLength = body.contentLength();
            onSuccess(call, response, parser(body.source()));
        } catch (ErrorMsg e) {
            onFailure(call, e);
        } catch (java.net.SocketException e){
            if("Socket closed".equals(e.getMessage())){
                LogUtil.e(TAG, "onResponse  用户取消操作");
                onFailure(call, new ErrorMsg(ErrorMsg.CODE_CANCEL));
            }else{
                onFailure(call, e);
            }
        }catch (Exception e) {
            LogUtil.e(TAG, "onResponse", e);
            onFailure(call, new ErrorMsg(ErrorMsg.CODE_WEB_ERROE));
        }
    }

    @Override
    public void onFailure(Call call, ErrorMsg error) {
        //下载中途出错，删除下载文件
        if(resp!=null){
            resp.delete();
        }
        super.onFailure(call, error);
    }

    /**
     * 保存文件到手机文件夹中
     *
     * @param source
     * @return
     * @throws Exception
     */
    public File parser(BufferedSource source) throws Exception {
        byte[] buf = new byte[SIZE];
        int len = 0;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = source.inputStream();
            long hasRead = 0;
            long totalCount = contentLength;
            if (totalCount == -1) {
                totalCount = is.available();
            }
            if (totalCount == 0) {
                LogUtil.e(TAG, ErrorMsg.MSG_DOWNLOAD_ERROE + "   parser(BufferedSource source) url:" + url);
                throw new ErrorMsg(ErrorMsg.CODE_DOWNLOAD_ERROE);
            }
            LogUtil.e(TAG, "   parser(BufferedSource source) filePath:" + filePath);
            //new 目标保存文件
            resp = getFile();
            fos = new FileOutputStream(resp);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                if (progressListener != null) {
                    hasRead += len;
                    progressListener.onProgress(hasRead, totalCount);
                }
            }
            fos.flush();
            return resp;
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }
    }

    //生产一个下载文件
    private File getFile() {
        File file = new File(filePath);
        if (file.exists()) {
            //文件存在
            if (file.isDirectory()) {
                //文件是否是路径
                String filePathStr = filePath + getFileName(url);
                file = new File(filePathStr);
                if (file.exists()) {
                    file.delete();
                }
            } else {
                file.delete();
            }

        } else {
            //文件不存在
            if (file.isDirectory()) {
                //文件是否是路径
                file.mkdirs();
            } else {
                File dir = file.getParentFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
        }
        return file;
    }

    public static String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}