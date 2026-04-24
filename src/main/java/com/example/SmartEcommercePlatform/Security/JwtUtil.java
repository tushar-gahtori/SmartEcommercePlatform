package com.example.SmartEcommercePlatform.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component //This tells Spring to manage this class so AuthService can find it!
public class JwtUtil {

    private final String SECRET="5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    //token expiry to 1 hour
    private final long EXPIRATION_TIME=1000*60*60;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Extract the Email (Subject) from the token
    public String extractEmail(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody().
                getSubject();
    }

    //Extract the expiration date
    public Date extractExpiration(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    //Check if the token is valid and hasn't expired
    public boolean validateToken(String token, String userEmail){
        final String email=extractEmail(token);
        return (email.equals(userEmail) && !extractExpiration(token).before(new Date()));
    }
}
