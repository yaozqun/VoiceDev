package com.grgbanking.baselib.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.grgbanking.baselib.config.AppConfig;
import com.grgbanking.baselib.util.log.LogUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/6/2.
 */
public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    public static final double BYTES_PER_M = 1024.0 * 1024.0;

    private static final int SIZE = 1024;
    private static final int SIZE_1 = 1000;

    private FileUtil() {
    }

    //文件前缀、后缀
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    /**
     * 创建图片文件-按时间命名
     */
    public static File createImageFile() throws IOException {
        // Create an image file name
        return createImageFile(AppConfig.IMAGE_ROOT);
    }

    /**
     * 创建指定目录图片文件-按时间命名
     */
    public static File createImageFile(String filePath) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + JPEG_FILE_SUFFIX;
        return createFile(imageFileName, filePath);
    }

    /**
     * 创建文件
     */
    public static File createFile(String fileName, String filePath) throws IOException {
        File directory = new File(filePath);
        mkdirs(directory);
        File imageF = new File(filePath + fileName);
        return imageF;
    }

    /**
     * 递归删除文件和文件夹,不删除根目录
     *
     * @param file 要删除的根目录
     */
    public static void deleteAllFileWithoutRoot(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                return;
            }
            for (File f : childFile) {
                deleteAllFile(f);
            }
        }
    }

    /**
     * 递归删除文件和文件夹,并删除根目录
     *
     * @param file 要删除的根目录
     */
    public static void deleteAllFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteAllFile(f);
            }
            file.delete();
        }
    }

    /**
     * 递归删除文件,除开指定文件名的文件,只会删除文件不会删除文件夹
     *
     * @param file
     * @param except
     */
    public static void deleteOnlyFile(File file, String except) {
        if (file.isFile() && !file.getName().equals(except)) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                return;
            }
            for (File f : childFile) {
                deleteOnlyFile(f, except);
            }
        }
    }

    /**
     * 创建文件目录
     */
    public static void mkdirs(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 获取文件扩展名
     */
    public static String getExtensionName(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return null;
    }

    /**
     * 取得文件的大小
     *
     * @param file 本地文件
     * @return 文件的大小, 如果找不到文件或读取失败则返回-1
     */
    public static long getFileSize(File file) {
        return (file.exists() && file.isFile() ? file.length() : -1L);
    }

    /**
     * 根据uri获取文件路径
     *
     * @param uri     使用URI表示的文件路径
     * @param context 上下文
     * @return 实际的文件路径
     */
    public static String getFilePath(Uri uri, Context context) {
        String filePath = null;
        String scheme = uri.getScheme();

        if (StringUtil.isEmpty(scheme)) {
            return filePath;
        }

        if ("file".equals(scheme)) { // 直接从Uri获取
            filePath = uri.getPath();
        } else if ("content".equals(scheme)) { // 从contentprovider获取
            String[] proj = {MediaStore.Images.Media.DATA};
            android.database.Cursor cursor = context.getContentResolver()
                    .query(uri, proj, null, null, null);
            if (cursor != null) {
                int columnIndex = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.getCount() > 0 && cursor.moveToFirst()
                        && columnIndex != -1) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        }

        return filePath;
    }

    /**
     * 拷贝文件到某文件夹
     *
     * @param source      源文件
     * @param destination 目标文件夹
     * @return 拷贝成功则返回true, 否则返回false
     */
    public static boolean copyFile(File source, File destination) {
        if (!source.exists() || !source.isFile())
            return false;
        if (!destination.exists())
            destination.mkdirs();
        if (!destination.exists() || !destination.isDirectory())
            return false;

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(new File(destination, source.getName()));
            byte[] buf = new byte[SIZE];
            int len;
            while ((len = fis.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            return true;
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, e.toString());
        } catch (Exception e) {
            LogUtil.e(TAG, e.toString());
        } finally {
            closeQuietly(fis);
            closeQuietly(fos);
        }
        return false;
    }

    /**
     * 拷贝Assets下的文件到程序私有空间
     *
     * @param context 上下文
     * @param source  原文件
     * @return 保存到程序私有空间后的文件路径
     * @throws IOException 原文件在assets目录中不存在的时候
     */
    public static String copyAssetFileToInternal(Context context, String source)
            throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = context.getAssets().open(source);
            out = context.openFileOutput(source, Context.MODE_WORLD_READABLE);
            byte[] buf = new byte[SIZE];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            return (context.getFileStreamPath(source)).getAbsolutePath();
        } finally {
            closeQuietly(in);
            closeQuietly(out);
        }
    }

    /**
     * 调用系统的工具，浏览某个文件
     *
     * @param path 文件路径
     */
    public static Intent openFile(String path) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, getMIMEType(file));
        return intent;
    }

    /**
     * 判断文件MimeType audio:.amr,.m4a,.mp3,.mid,.xmf,.ogg,.wav,.3gpp,.3ga,.wma
     * video:.3gp,.mp4,.m4v image:.jpg,.gif,.png,.jpeg,.bmp word:.doc apk:.apk
     *
     * @return 文件MimeType
     */
    public static String getMIMEType(File file) {
        String fName = file.getName();
        /* 取得扩展名 */
        String[] names = fName.split("\\.");
        String end = names[names.length - 1];

		/* 依扩展名的类型决定MimeType */
        // 根据需要扩展解析文件类型
        String type = "";
        if (end.equals("amr") || end.equals("m4a") || end.equals("mp3")
                || end.equals("mid") || end.equals("xmf") || end.equals("ogg")
                || end.equals("wav") || end.equals("3gpp") || end.equals("3ga")
                || end.equals("wma")) {
            type = "audio/*";
        } else if (end.equals("3gp") || end.equals("mp4") || end.equals("m4v")) {
            type = "video/*";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image/*";
        } else if (end.endsWith("apk")) {
            type = "application/vnd.android.package-archive";
        } else if (end.endsWith("doc")) {
            type = "application/msword";
        } else {
            type = "*/*";
        }
        return type;
    }

    /**
     * 判断文件类型 audio:.amr,.m4a,.mp3,.mid,.xmf,.ogg,.wav,.3gpp,.3ga,.wma
     * video:.3gp,.mp4,.m4v image:.jpg,.gif,.png,.jpeg,.bmp
     * word:.xls,doc,docx,xlsx
     *
     * @param filename 文件名
     * @return 文件类型
     */
    public static String getMIMEType(String filename) {

        if (StringUtil.isEmpty(filename)) {
            return "";
        }

		/* 取得扩展名 */
        String[] texts = filename.split("\\.");
        String end = texts[texts.length - 1].toLowerCase();

		/* 依扩展名的类型决定MimeType */
        // 根据需要扩展解析文件类型
        String type = "";
        if (end.equals("amr") || end.equals("m4a") || end.equals("mp3")
                || end.equals("mid") || end.equals("xmf") || end.equals("ogg")
                || end.equals("wav") || end.equals("3gpp") || end.equals("3ga")
                || end.equals("wma")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4") || end.equals("m4v")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("xls") || end.equals("doc") || end.equals("docx")
                || end.equals("xlsx")) {
            type = "word";
        } else {
            type = "other";
        }
        return type;
    }

    /***
     * 计算文件夹大小
     *
     * @param mFile 目录或文件
     * @return 文件或目录的大小
     */
    public static long calculateFolderSize(File mFile) {
        // 判断文件是否存在
        if (!mFile.exists()) {
            return 0;
        }

        // 如果是目录则递归计算其内容的总大小，如果是文件则直接返回其大小
        if (mFile.isDirectory()) {
            File[] files = mFile.listFiles();
            long size = 0;
            for (File f : files) {
                size += calculateFolderSize(f);
            }
            return size;
        } else {
            return mFile.length();
        }

    }

    /***
     * 清空指定文件夹/文件
     *
     * @return 清空成功的话返回true, 否则返回false
     */

    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return true;
        }
        if (file.isDirectory()) {
            File[] childs = file.listFiles();
            if (childs == null || childs.length <= 0) {
                // 空文件夹删掉
                return file.delete();
            } else {
                // 非空，遍历删除子文件
                for (int i = 0; i < childs.length; i++) {
                    deleteFile(childs[i]);
                }
                return deleteFile(file);
            }
        } else {
            return file.delete();
        }

    }

    /**
     * 获取asset目录中文件的流
     *
     * @param fileName asserts目录中的文件名
     * @return 文件流, 如果读取失败则返回null
     */
    public static InputStream getAssetsInputStream(Context context,
                                                   String fileName) {
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
        } catch (IOException e) {
            LogUtil.e(TAG, e.toString());
        }
        return is;
    }

    /**
     * 判断路径(文件或目录)是否存在
     *
     * @param path 文件或目录路径
     * @return 如果存在返回true, 否则返回false
     */
    public static boolean isFileExist(String path) {
        return new File(path).exists();
    }

    /**
     * 关闭输入输出流
     *
     * @param c 输入输出流
     */
    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                LogUtil.i(TAG, e.toString());
            }
        }
    }

    /**
     * 流转为string
     *
     * @param is
     * @return String [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String stream2String(InputStream is) {
        if (is == null) {
            return "";
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer strBuffer = new StringBuffer("");

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                strBuffer.append(line);
            }
        } catch (IOException e) {
            LogUtil.e(TAG, e.toString());
        } finally {
            closeQuietly(reader);
        }
        return strBuffer.toString();
    }

    /**
     * 把输入流拷贝到输出流
     *
     * @param input  输入流
     * @param output 输出流
     * @return 拷贝的字节数，如果失败返回-1
     */
    public static int copyStream(InputStream input, OutputStream output) {
        byte[] buffer = new byte[SIZE];
        int count = 0;
        int n = 0;
        try {
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
        } catch (Exception ex) {
            return -1;
        }
        return count;
    }

    /**
     * <查询指定目录下所有指定一种后缀名的文件放到一个文件队列中> <功能详细描述>
     *
     * @param dirName  指定的目录
     * @param endName  指定的后缀名
     * @param fileList [文件List]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void getFiles(File dirName, String endName,
                                List<File> fileList) {

        File[] files = dirName.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    getFiles(f, endName, fileList);
                } else if (f.isFile()) {
                    try {
                        if (f.getName().endsWith(endName.toLowerCase())
                                || f.getName().endsWith(endName.toUpperCase())) {
                            fileList.add(f);
                        }
                    } catch (Exception e) {
                        e.toString();
                    }
                }
            }
        }
    }

    /**
     * <查询指定目录下所有指定多种后缀名的文件放到一个文件队列中>
     *
     * @param dirName
     * @param endNames [后缀名数组]
     * @param fileList [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void getFiles(File dirName, String[] endNames,
                                List<File> fileList) {
        for (String endName : endNames) {
            getFiles(dirName, endName, fileList);
        }
    }

    public static String getDirectoryName(String path) {
        if (!StringUtil.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                String parentPath = file.getParent();
                return parentPath.substring(parentPath
                        .lastIndexOf(File.separator) + 1);
            }
        }
        return "";

    }

    /**
     * SD卡是否可用
     *
     * @return SD卡是否可用
     */
    public static boolean isSDCardReady() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * sd卡的根目录
     */
    public static String read(String fileName) {
        String result = "";
        File file = new File(AppConfig.FILE_ROOT + fileName);
        if (!file.exists()) {
            return result;
        }
        FileReader fileReader;
        BufferedReader reader;
        try {
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);

            String line = "";
            while ((line = reader.readLine()) != null) {
                result += line;
            }

            fileReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getBytesFromFile(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(SIZE_1);
            byte[] b = new byte[SIZE_1];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 根据byte数组，生成文件
     *
     * @param bfile    byte数组
     * @param fileName 文件名称
     */
    public static String getFile(byte[] bfile, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        String fileFolder = AppConfig.FILE_ROOT;
        try {
            file = new File(fileFolder);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(fileFolder + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return fileFolder + fileName;
    }

    /**
     * 判断文件是否存在
     *
     * @return
     */
    public static boolean isFileExists(String filePath) {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    /**
     * 删除文件夹所有内容
     *
     */
    public static void deleteDir(String filePath) {
        File file = new File(filePath);
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File[] files = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteDir(files[i].getPath()); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            //
        }
    }

    // 以当前时间作为文件名
    public static String getCurrentDateForFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str + ".spx";
    }

}
