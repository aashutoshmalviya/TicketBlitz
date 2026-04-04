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
        // Load the key from the resources folder
        InputStream is = getClass().getClassLoader().getResourceAsStream("private_key_pkcs8.pem");
        if (is == null) throw new RuntimeException("Private key not found!");

        String key = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        // Strip the header, footer, and newlines
        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        // Decode and generate the RSA Key object
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
    }

    public String generateToken(String username, String role) {
        long expirationTime = 1000 * 60 * 60; // 1 Hour

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(privateKey) // SIGNED WITH ASYMMETRIC PRIVATE KEY
                .compact();
    }
}