package com.phemex.dataFactory.common.utils;

import com.phemex.dataFactory.common.exception.CommonMessageCode;
import com.phemex.dataFactory.common.exception.PhemexException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.utils.RawAesMessageEncryptor
 * @Date: 2022年10月28日 11:50
 * @Description:
 */

@Slf4j
public class RawAesMessageEncryptor {

    private static final String algo = "AES/CFB8/NoPADDING";

    private byte[] keyUsed;

    final public static RawAesMessageEncryptor INSTANCE = createDefaultInstance();

    static RawAesMessageEncryptor createDefaultInstance() {
        try {
            return new RawAesMessageEncryptor();
        } catch (Exception e) {
            // ignore
            log.error("Failed to create sym encryptor");
            return null;
        }
    }

    public RawAesMessageEncryptor(String input) {
        this.init(input);
    }

    public RawAesMessageEncryptor() {
        this("9Aq@0#cdd#{}!@Y5260&43");
    }

    private static byte[] toKey(byte[] keyStr) {
        byte[] keyBytes = new byte[32];
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Failed to find sha256 algo", ex);
        }
        md.update(keyStr);
        System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.length);
        return keyBytes;
    }

    private static byte[] getVector() {
        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        ThreadLocalRandom.current().nextBytes(iv);
        return iv;
    }

    public String encrypt(String text) {
        byte[] encyptedBytes = doEncrypt(this.keyUsed, getVector(), text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encyptedBytes);
    }

    public String decrypt(String ciperText) {
        byte[] ciperBytes = Base64.getDecoder().decode(ciperText);
        return new String(doDecrypt(this.keyUsed, ciperBytes), StandardCharsets.UTF_8);
    }

    public byte[] decrypt2Bytes(String ciperText) {
        byte[] ciperBytes = Base64.getDecoder().decode(ciperText);

        return doDecrypt(this.keyUsed, ciperBytes);
    }

    private static byte[] doEncrypt(byte[] keyB, byte[] ivB, byte[] bytes) {
        try {
            IvParameterSpec iv = new IvParameterSpec(ivB);
            SecretKeySpec skeySpec = new SecretKeySpec(keyB, "AES");

            Cipher cipher = Cipher.getInstance(algo);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encryptedBytes = cipher.doFinal(bytes);

            byte[] encryptedIVAndText = new byte[ivB.length + encryptedBytes.length];

            System.arraycopy(ivB, 0, encryptedIVAndText, 0, ivB.length);
            System.arraycopy(encryptedBytes, 0, encryptedIVAndText, ivB.length, encryptedBytes.length);
            return encryptedIVAndText;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to encrypt", ex);
        }
    }

    private static byte[] doDecrypt(byte[] keyB, byte[] encryptedIvTextBytes) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(keyB, "AES");
            Cipher cipher = Cipher.getInstance(algo);

            int blockSize = cipher.getBlockSize();
            byte[] ivB = Arrays.copyOfRange(encryptedIvTextBytes, 0, blockSize);
            IvParameterSpec iv = new IvParameterSpec(ivB);
            byte[] dataToDecrypt = Arrays.copyOfRange(encryptedIvTextBytes, blockSize, encryptedIvTextBytes.length);

            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            return cipher.doFinal(dataToDecrypt);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to decrypt", ex);
        }
    }

    public String encode(String text, int expire) {
        long now = System.currentTimeMillis();
        long toExpire = now + expire;
        byte[] b = BytesUtils.long2LittleEndian(toExpire);

        byte[] tb = text.getBytes(StandardCharsets.UTF_8);
        byte[] all = new byte[b.length + tb.length];
        System.arraycopy(b, 0, all, 0, b.length);
        System.arraycopy(tb, 0, all, b.length, tb.length);
        return Base64.getEncoder().encodeToString(doEncrypt(this.keyUsed, getVector(), all));
    }

    public String decode(String text) {
        long now = System.currentTimeMillis();
        byte[] out = doDecrypt(this.keyUsed, Base64.getDecoder().decode(text));
        long expire = BytesUtils.littleEndian2Long(out);

        if (now > expire) {
            throw new PhemexException(CommonMessageCode.INVALID_ARGS, "text expired at " + expire);
        }

        return new String(out, 8, out.length - 8, StandardCharsets.UTF_8);
    }

    private void init(String k1) {
        this.keyUsed = toKey(k1.getBytes(StandardCharsets.UTF_8));
    }

}
