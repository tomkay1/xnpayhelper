package com.arya1021.alipay.utlis;

import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StringUtils {
    public static void main(String[] args) {
    }

    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            Log.d("arik",e.getMessage());
        } catch (IOException e) {
            Log.d("arik",e.getMessage());
        }
        return buffer;
    }


    public static String getTextCenter(String text, String begin, String end) {
        try {
            int b = text.indexOf(begin) + begin.length();
            int e = text.indexOf(end, b);
            return text.substring(b, e);
        } catch (Exception e) {
            Log.d("arik",e.getMessage());
            return "error";
        }
    }

    /**
     * 截取两字符之间的字符串，str 和 start不能为null或""
     */
    public static String getCutOutString(String str, String start, String endwith) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        String result = "";
        if (str.contains(start)) {
            String s1 = str.split(start)[1];
            if (endwith == null || "".equals(endwith)) {
                result = s1;
            } else {
                String s2[] = s1.split(endwith);
                if (s2 != null) {
                    result = s2[0];
                }
            }
        }
        return result;

    }

    public static String getCutOutString(String str, String endwith) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        String result = "";
        if (str.contains(endwith)) {
            result = str.substring(0, str.indexOf(endwith));
        }
        return result;
    }

}
