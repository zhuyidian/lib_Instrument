package com.dunn.logger;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpUtils {
    private OkHttpClient okHttpClient;

    private HttpUtils(){
        createOkhttp();
    }

    /**
     * 使用静态内部类的方式实现单例模式
     */
    private static class UploadUtilInstance{
        private static final HttpUtils INSTANCE = new HttpUtils();
    }

    public static HttpUtils getInstance(){
        return UploadUtilInstance.INSTANCE;
    }

    /**
     * @param url   服务器地址
     * @param file  所要上传的文件
     * @return      响应结果
     * @throws IOException
     */
    public ResponseBody uploadLog(String url, File file, String token){
        createOkhttp();

        try {
            if (LogConfig.DEBUG) Log.v("logger[", "HttpUtils url=" + url + ", file path=" + file.getPath()+", token="+token);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
//                .addFormDataPart("encryp",getRequestEncryp(cTime,num))
//                .addFormDataPart("date",cTime + "")
//                .addFormDataPart("ran", num)
                    .addFormDataPart("accesstoken",token!=null?token:"")
                    .setType(MediaType.parse("multipart/form-data"))
                    .addFormDataPart("file", file.getName(),
                            RequestBody.create(MediaType.parse("multipart/form-data"), file))
                    .build();
            Request request = new Request.Builder()
                    //.header("Authorization", "ClientID" + UUID.randomUUID())
                    .url(url)
                    .post(requestBody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if (LogConfig.DEBUG) Log.v("logger[", "HttpUtils response.toString=" + response.toString());
            if (response.isSuccessful()) {
                if (LogConfig.DEBUG) Log.v("logger[", "HttpUtils suc exit");
                //file.delete();
            } else {
                if (LogConfig.DEBUG) Log.v("logger[", "HttpUtils suc not exit!!!!!!!!!");
            }
            if (LogConfig.DEBUG) Log.v("logger[", "HttpUtils suc status=" + response.code());
//            if (!response.isSuccessful())
//                throw new IOException("Unexpected code " + response);
            return response.body();
        }catch (Exception e){
            e.printStackTrace();
            if (LogConfig.DEBUG) Log.v("logger[", "HttpUtils e=" + e);
            return null;
        }
    }

    public ResponseBody uploadLogOld(String url, File file, String userId){
        createOkhttp();

        try {
            if (LogConfig.DEBUG) Log.v("logger[", "HttpUtils url=" + url + ", file path=" + file.getPath()+", userId="+userId);
            long cTime = System.currentTimeMillis();
            String num = Tools.getRandom(Tools.NUMBERS, 4);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("encryp", Tools.getRequestEncryp(cTime,num))
                    .addFormDataPart("date",cTime + "")
                    .addFormDataPart("ran", num)
                    .addFormDataPart("uid",userId)
                    .setType(MediaType.parse("multipart/form-data"))
                    .addFormDataPart("file", file.getName(),
                            RequestBody.create(MediaType.parse("multipart/form-data"), file))
                    .build();

            Request request = new Request.Builder()
                    //.header("Authorization", "ClientID" + UUID.randomUUID())
                    .url(url)
                    .post(requestBody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if (LogConfig.DEBUG) Log.v("logger[", "HttpUtils response.toString=" + response.toString());
            if(response.isSuccessful()) {
                if (LogConfig.DEBUG) Log.v("logger[", "HttpUtils suc exit");
                file.delete();
            }else {
                if (LogConfig.DEBUG) Log.v("logger[", "HttpUtils suc not exit!!!!!!!!!");
            }
            if (LogConfig.DEBUG) Log.v("logger[", "HttpUtils suc status=" + response.code());
//            if (!response.isSuccessful())
//                throw new IOException("Unexpected code " + response);
            return response.body();
        }catch (Exception e){
            e.printStackTrace();
            if (LogConfig.DEBUG) Log.v("logger[", "HttpUtils e=" + e);
            return null;
        }
    }

    private void createOkhttp(){
        if(okHttpClient!=null) return;

//        okHttpClient = new OkHttpClient();

        okHttpClient = new OkHttpClient
                .Builder()
                .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(60, TimeUnit.SECONDS)//设置读取超时时间
                .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        if (LogConfig.DEBUG) Log.v("logger[", "HttpUtils okhttp log message="+message);
                    }
                }).setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }
}

