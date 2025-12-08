package com.safepass.crypto;

import java.security.MessageDigest;
import javax.crypto.spec.SecretKeySpec;

public abstract class CriptografiaBase {

    protected SecretKeySpec generarClave(String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        return new SecretKeySpec(key, "AES");
    }
}
