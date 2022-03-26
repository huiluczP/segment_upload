package com.huiluczp.util;
import java.util.UUID;

// 工具类
// 文件名生成
public class FileUtil {
    public static String getFileNameWithoutSuffix(String fileName){
        int suffixIndex = fileName.lastIndexOf('.');
        if(suffixIndex<0)
            return fileName;
        return fileName.substring(0, suffixIndex);
    }

    public static String getFileSuffix(String fileName){
        int suffixIndex = fileName.lastIndexOf('.');
        if(suffixIndex<0)
            return "";
        return fileName.substring(suffixIndex+1);
    }

    public static String getSegmentName(String fileName, int segmentIndex){
        return fileName + "#" + segmentIndex;
    }

    public static String createSaveFileName(String key, String fileName){
        String suffix = getFileSuffix(fileName);
        return key + "." + suffix;
    }

    public static String createUUIDFileName(String fileName){
        String suffix = getFileSuffix(fileName);
        String name = UUID.randomUUID().toString();
        return name + "." + suffix;
    }
}
