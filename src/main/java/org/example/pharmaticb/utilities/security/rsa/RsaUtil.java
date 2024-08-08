package org.example.pharmaticb.utilities.security.rsa;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaUtil {
    public static final String INVALID_PRIVATE_KEY = "Invalid private key!!!";
    private final Cipher cipher;
    private static final String RSA = "RSA";
    private static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
    private static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";
    private static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
    private static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";

    public RsaUtil() throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.cipher = Cipher.getInstance(RSA);
    }

    public PublicKey getPublicKey(final String rsaPublicKey) {
        try {
            var publicKeyPem = rsaPublicKey.replace(BEGIN_PUBLIC_KEY, "").replace(END_PUBLIC_KEY, "").replaceAll("\\s", "");
            byte[] decode = Base64.getDecoder().decode(publicKeyPem);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(decode);
            KeyFactory instance = KeyFactory.getInstance(RSA);
            return instance.generatePublic(x509EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public PrivateKey getPrivateKey(String rsaPrivateKey) throws GeneralSecurityException {
        if (rsaPrivateKey.contains(BEGIN_PRIVATE_KEY)) {
            var privateKeyPem = rsaPrivateKey.replace(BEGIN_PRIVATE_KEY, "").replace(END_PRIVATE_KEY, "").replaceAll("\\s", "");
            byte[] pkcs8EncodedKey = Base64.getDecoder().decode(privateKeyPem);
            try {
                var factory = KeyFactory.getInstance(RSA);
                return factory.generatePrivate(new PKCS8EncodedKeySpec(pkcs8EncodedKey));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new GeneralSecurityException(INVALID_PRIVATE_KEY);
        }
    }
}
