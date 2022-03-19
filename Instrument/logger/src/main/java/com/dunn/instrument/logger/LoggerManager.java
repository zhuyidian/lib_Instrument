package com.dunn.instrument.logger;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        if (LogConfig.DEBUG) Log.v("logger[", "LoggerManager：init mFilePath="+mFilePath+", mFileName="+mFileName);
        createThread();
    }

    public void release(){
        if (LogConfig.DEBUG) Log.v("logger[", "LoggerManager：release");
        releaseThread();
    }

    /**
     * 写log的入口
     * @param content
     */
    public void writeTo(String content) {
        if(mFilePath!=null && !mFilePath.isEmpty() && mFileName!=null && !mFileName.isEmpty()){
            if (dealExecutorService != null && !dealExecutorService.isShutdown()){
                dealExecutorService.execute( new WriteAble(content) );
            }
        }
    }

    /**
     * 强制删除文件
     */
    public void deleteLogFile(){
        try {
            if (mFilePath != null && !mFilePath.isEmpty() && mFileName != null && !mFileName.isEmpty()) {
                if (dealExecutorService != null && !dealExecutorService.isShutdown()) {
                    dealExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            String path = mFilePath + "/" + mFileName;
                            File logfile = new File(path);
                            if (logfile.exists()) {
                                if (LogConfig.DEBUG)
                                    Log.v("logger[", "LoggerManager：delete log file");
                                logfile.delete();
                            }
                        }
                    });
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 拷贝和压缩文件
     * @param rewrite
     * @return　拷贝的文件　　或者　　压缩文件
     */
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
            //生成文件夹之后，再生成文件，不然会出错
            makeFilePath(mFilePath, mFileName);

            //检查文件大小
            String path = mFilePath + "/"+ mFileName;
            File logfile = new File(path);
            if (logfile.exists() && logfile.length() > 1024 * 1024 * 50) {
                if (LogConfig.DEBUG) Log.v("logger[", "LoggerManager：log file size is > 50M !!!!!!!!!!!!!!!! & delete log file");
                logfile.delete();
            }

            // 每次写入时，都换行写
            String strContent = msg + "\r\n";
            try {
                File file = new File(path);
                if (!file.exists()) {
                    //if(!file.getParentFile().exists()) file.getParentFile().mkdir();
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                raf.seek(file.length());
                if (LogConfig.DEBUG) Log.v("logger[", "LoggerManager：write msg="+strContent);
                raf.write(strContent.getBytes("UTF-8"));
                raf.close();
            } catch (Exception e) {
                e.printStackTrace();
                if (LogConfig.DEBUG) Log.v("logger[", "LoggerManager：write e="+e);
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
            if (LogConfig.DEBUG) Log.v("logger[", "LoggerManager：makeFilePath e="+e);
        }
        return file;
    }

    private void makeRootDirectory(String filePath) {
        String pathLogger = filePath;
        File file = null;
        try {
            file = new File(pathLogger);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            if (LogConfig.DEBUG) Log.v("logger[", "LoggerManager：makeRootDirectory e="+e);
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
