package com.batch.Utilities;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class AESystem {

    private static String ALGO = "AES";
    private static byte[] KeyValue = "aaaaaaaaaaaaaaaa".getBytes();

    public AESystem(String KeyValue) {
        AESystem.KeyValue = KeyValue.getBytes();
    }

    public static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encval = cipher.doFinal(Data.getBytes());
        byte[] encryptedvalue = Base64.getEncoder().encode(encval);

        return new String(encryptedvalue);
    }

    public static String decrypt(String Data) throws Exception {
        
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedvalue = Base64.getDecoder().decode(Data);
        byte[] decryptedvalue = cipher.doFinal(decodedvalue);
        return new String(decryptedvalue);
    }

    private static Key generateKey() {
        Key key = new SecretKeySpec(KeyValue, ALGO);
        return key;
    }
}
