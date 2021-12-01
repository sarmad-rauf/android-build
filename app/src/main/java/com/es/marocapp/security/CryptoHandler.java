package com.es.marocapp.security;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoHandler {

    private static CryptoHandler instance = null;
  //  private static byte[] KEY;
    // Your IV Initialization Vector
 //   private final static byte[] ivx = Globals.AES_IVX;

   /* protected CryptoHandler() {

        try {
            //Your Secret Key
            KEY = Globals.AES_SECRET_KEY.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
    }*/

    public static CryptoHandler getInstance() {

        if (instance == null) {
            instance = new CryptoHandler();
        }
        return instance;
    }

    public String encrypt(String message, byte[] key, byte[] iv) throws NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException,
            UnsupportedEncodingException, InvalidAlgorithmParameterException {

        byte[] srcBuff = message.getBytes(StandardCharsets.UTF_8);

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher ecipher = Cipher.getInstance("AES/CBC/ISO10126Padding");
        ecipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);

        byte[] dstBuff = ecipher.doFinal(srcBuff);

        return Base64.encodeToString(dstBuff, Base64.DEFAULT);

    }

    public String decrypt(String encrypted, byte[] key, byte[] iv) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher ecipher = Cipher.getInstance("AES/CBC/ISO10126Padding");
        ecipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);

        byte[] raw = Base64.decode(encrypted, Base64.DEFAULT);

        byte[] originalBytes = ecipher.doFinal(raw);

        String original = new String(originalBytes, "UTF-8");

        return original;

    }
}