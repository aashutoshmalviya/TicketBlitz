package com.illusion.ticketblitz.authservice.service;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final PrivateKey privateKey;

    public JwtService() throws Exception {
        this.privateKey = loadPrivateKey();
    }

    private PrivateKey loadPrivateKey() throws Exception {
        // Load RSA private key from classpath - must be PKCS#8 format
        InputStream is = getClass().getClassLoader().getResourceAsStream("private_key_pkcs8.pem");
        if (is == null) throw new RuntimeException("Private key not found!");

        String key = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        // Strip PEM headers/footers and decode base64
        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
    }

    public String generateToken(String username, String role) {
        long expirationTime = 1000 * 60 * 60; // Token expiration: 1 hour

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(privateKey) // RSA signature - public key validation at gateway
                .compact();
    }
}