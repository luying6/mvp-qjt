package com.luying.mvp.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 创建人:luying
 * 创建时间:2018/5/11.
 */

public class MD5Util {
    public static String getMd5(String time){
        String info = "76cde413e16846d282a4cc0a7030c074" + time + "RNWONRdT94iV2iKm";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        }
        catch (NoSuchAlgorithmException e) {
            return "";
        }
        catch (UnsupportedEncodingException e) {
            return "";
        }
    }

}
