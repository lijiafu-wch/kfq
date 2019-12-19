package f.s.kfq.util;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 */
public class KfqUtil {

    public static String aesEncrypt(String aesKey,String iv, String data) throws Exception {
        return byte2HexString(encrypt(aesKey.getBytes("utf-8"), data.getBytes("utf-8"),iv.getBytes("utf-8")));
    }

    public static final String aesDecrypt(String key, String data) throws Exception {
        return new String(decrypt(key.getBytes("utf-8"), hexString2Byte(data)), "utf-8");
    }

    /**
     * md5加密
     **/
    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try {
                md.update(str.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] byteDigest = md.digest();
            StringBuffer buf = new StringBuffer("");

            for (int offset = 0; offset < byteDigest.length; ++offset) {
                int i = byteDigest[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }

                buf.append(Integer.toHexString(i));
            }

            return buf.toString();
        } catch (NoSuchAlgorithmException var6) {
            var6.printStackTrace();
            return null;
        }
    }

    private static byte[] decrypt(byte[] key, byte[] data) throws Exception {
        byte[] buff = get128Key(key);
        SecretKeySpec skeySpec = new SecretKeySpec(buff, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
        cipher.init(2, skeySpec, iv);
        return cipher.doFinal(data);
    }

    private static byte[] hexString2Byte(String str) {
        int len = str.length();
        String stmp = null;
        byte[] bt = new byte[len / 2];

        for(int n = 0; n < len / 2; ++n) {
            stmp = str.substring(n * 2, n * 2 + 2);
            bt[n] = (byte)Integer.parseInt(stmp, 16);
        }

        return bt;
    }

    private static String byte2HexString(byte[] b) {
        String hs = "";
        String stmp = "";

        for (int n = 0; n < b.length; ++n) {
            stmp = Integer.toHexString(b[n] & 255);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }

            if (n < b.length - 1) {
                hs = hs + "";
            }
        }

        return hs.toUpperCase();
    }

    private static byte[] encrypt(byte[] key, byte[] data,byte[] iv) throws Exception {
        byte[] buff = get128Key(key);
        SecretKeySpec skeySpec = new SecretKeySpec(buff, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(1, skeySpec, ivParameterSpec);
        return cipher.doFinal(data);
    }

    private static byte[] get128Key(byte[] key) {
        byte[] buff = new byte[16];
        for (int i = 0; i < 16; ++i) {
            buff[i] = 0;
        }
        if (key.length < 16) {
            System.arraycopy(key, 0, buff, 0, key.length);
        } else if (key.length > 16) {
            System.arraycopy(key, 0, buff, 0, buff.length);
        } else {
            System.arraycopy(key, 0, buff, 0, buff.length);
        }
        return buff;
    }

    /*****************************************/
    /**
     * 接收json参数
     * @param request
     * @return
     */
    public static String getBodyJson(HttpServletRequest request){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "utf-8"));
            StringBuffer sb = new StringBuffer("");
            String temp;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
            br.close();
            String params = sb.toString();
            return params;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}