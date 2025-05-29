package com.dunn.instrument.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AssetFileUtil {

    public static List<String> listAssetFiles(Context context, String path) {
        List<String> fileList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list(path);
            if (files != null) {
                for (String file : files) {
                    String filePath = path.isEmpty() ? file : path + "/" + file;
                    fileList.add(filePath);

                    // 检查是否是目录
                    if (assetManager.list(filePath).length > 0) {
                        // 如果是目录，递归调用
                        fileList.addAll(listAssetFiles(context, filePath));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }

    public static String readAssetFile(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        try (InputStream inputStream = assetManager.open(fileName)) {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File copyAssetToInternalStorage(Context context, String assetFileName, String destinationFileName) {
        File destinationFile = new File(context.getFilesDir(), destinationFileName);
        try (InputStream inputStream = context.getAssets().open(assetFileName);
             FileOutputStream outputStream = new FileOutputStream(destinationFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destinationFile;
    }
}