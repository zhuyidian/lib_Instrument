package com.dunn.logger;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class LoggerManager {
    private static volatile LoggerManager instance;
    private String mFilePath = "";
    private String mFileName = "";
    private ExecutorService dealExecutorService;  //用于处理任务  数量多，耗时少的线程任务  无限制

    private LoggerManager() {

    }

    public static LoggerManager L() {
        if (instance == null) {
            synchronized (LoggerManager.class) {
                if (instance == null) {
                    instance = new LoggerManager();
                }
            }
        }
        return instance;
    }

    public void init(String filePath,String fileName){
        mFilePath = filePath;
        mFileName = fileName;
        Log.v("logger[", "LoggerManager：init mFilePath="+mFilePath+", mFileName="+mFileName);
        createThread();
    }

    public void release(){
        Log.v("logger[", "LoggerManager：release");
        releaseThread();
    }

    public void writeTo(String content) {
        if(mFilePath!=null && !mFilePath.isEmpty() && mFileName!=null && !mFileName.isEmpty()){
            if (dealExecutorService != null && !dealExecutorService.isShutdown()){
                dealExecutorService.execute( new WriteAble(content) );
            }
        }
    }

    public File copyfile(Boolean rewrite ) {
        try {
            String path = mFilePath + "/" + mFileName;
            File fromFile = new File(path);

            String pathZip = mFilePath + "/" + mFileName + "Z";
            File toFile = new File(pathZip);

            if (!fromFile.exists()) {
                return null;
            }
            if (!fromFile.isFile()) {
                return null;
            }
            if (!fromFile.canRead()) {
                return null;
            }
            if (!toFile.getParentFile().exists()) {
                toFile.getParentFile().mkdirs();
            }
            if (toFile.exists() && rewrite) {
                toFile.delete();
            }

            //当文件不存时，canWrite一直返回的都是false
            //  if (!toFile.canWrite()) {
            //  MessageDialog.openError(new Shell(),"错误信息","不能够写将要复制的目标文件" + toFile.getPath());
            // Toast.makeText(this,"不能够写将要复制的目标文件", Toast.LENGTH_SHORT);
            //   return ;
            //   }
            try {
                FileInputStream fosfrom = new FileInputStream(fromFile);
                FileOutputStream fosto = new FileOutputStream(toFile);
                byte bt[] = new byte[1024];
                int c;
                while ((c = fosfrom.read(bt)) > 0) {
                    fosto.write(bt, 0, c); //将内容写到新文件当中
                }
                fosfrom.close();
                fosto.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //zip
            if (toFile != null && toFile.exists()) {
                File zipFile = ZipUtils.compressFile(toFile.getPath());
                if(zipFile!=null && zipFile.exists()){
                    return zipFile;
                }
            }
            return toFile;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private class WriteAble implements Runnable {
        private String msg;

        public WriteAble(String m_msg) {
            msg = m_msg;
        }

        @Override
        public void run() {
            //检查文件大小
            String path = mFilePath + "/"+ mFileName;
            File logfile = new File(path);
            if (logfile.exists() && logfile.length() > 1024 * 1024 * 50) {
                Log.v("logger[", "LoggerManager：log file size is > 50M !!!!!!!!!!!!!!!! & delete log file");
                logfile.delete();
            }

            //生成文件夹之后，再生成文件，不然会出错
            makeFilePath(mFilePath, mFileName);

            // 每次写入时，都换行写
            String strContent = msg + "\r\n";
            try {
                File file = new File(path);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                raf.seek(file.length());
                //Log.v("logger[", "LoggerManager：write msg="+strContent);
                raf.write(strContent.getBytes("UTF-8"));
                raf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }

    /**
     * 压缩文件和文件夹
     *
     */
    private void ZipFolder() throws Exception {
        //创建文件
        String path = mFilePath + "/"+ mFileName+"zzz";
        File file = new File(path);
        //zip
        String pathZip = mFilePath + "/"+ mFileName+".zip";
        File fileZip = new File(pathZip);
        //创建ZIP
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(fileZip));
        //压缩
        ZipFiles(file, file.getName(), outZip);
        //完成和关闭
        outZip.finish();
        outZip.close();
    }

    /**
     * 压缩文件
     */
    private void ZipFiles(File srcFile, String fileName, ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null)
            return;

        if (srcFile.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            FileInputStream inputStream = new FileInputStream(srcFile);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        }
    }

    private void createThread(){
        try {
            if (dealExecutorService == null || dealExecutorService.isShutdown()) {
                dealExecutorService = Executors.newCachedThreadPool();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void releaseThread(){
        try {
            if (dealExecutorService != null && !dealExecutorService.isShutdown()) {
                dealExecutorService.shutdown();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //**********************************************************time********************************
    private Timer timer1;
    private CheckTimerTask mcheckTimerTask;
    private class CheckTimerTask extends TimerTask {
        @Override
        public void run() {

        }
    }
    private void startTimer(){
        if (mcheckTimerTask != null) {
            mcheckTimerTask.cancel();
            mcheckTimerTask = null;
        }
        if( timer1!=null ){
            timer1.cancel();
            timer1=null;
        }
        timer1 = new Timer();
        mcheckTimerTask =new CheckTimerTask();
        timer1.schedule(mcheckTimerTask,60000);
    }
    private void stopTimer(){
        if (mcheckTimerTask != null) {
            mcheckTimerTask.cancel();
            mcheckTimerTask = null;
        }
        if( timer1!=null ){
            timer1.cancel();
            timer1=null;
        }
    }
}
