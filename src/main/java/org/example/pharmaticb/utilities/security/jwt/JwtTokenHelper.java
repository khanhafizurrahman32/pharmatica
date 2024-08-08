package org.example.pharmaticb.utilities.security.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import org.example.pharmaticb.utilities.security.rsa.RsaUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class JwtTokenHelper {

    private final String rsaPublicKey;
    private final String rsaPrivateKey;


    public JwtTokenHelper(String rsaPublicKey, String rsaPrivateKey) {
        this.rsaPublicKey = rsaPublicKey;
        this.rsaPrivateKey = rsaPrivateKey;
    }

    public Algorithm getTokenAlgorithm() {
        try {
            RsaUtil rsaUtil = new RsaUtil();
            RSAPublicKey publicKey = null;
            if (StringUtils.hasText(this.rsaPublicKey)) {
                publicKey = (RSAPublicKey) rsaUtil.getPublicKey(this.rsaPublicKey);
            }
            RSAPrivateKey privateKey = null;
            if (StringUtils.hasText(this.rsaPrivateKey)) {
                privateKey = (RSAPrivateKey) rsaUtil.getPrivateKey(this.rsaPrivateKey);
            }
            return Algorithm.RSA512(publicKey, privateKey);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
