package org.example.pharmaticb.service.security.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.service.security.rsa.RsaUtil;
import org.springframework.util.StringUtils;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Slf4j
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
            Algorithm algorithm = Algorithm.RSA512(publicKey, privateKey);
            return algorithm;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
