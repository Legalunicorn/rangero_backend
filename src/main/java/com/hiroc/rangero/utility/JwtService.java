package com.hiroc.rangero.utility;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


//Bean dependecies
@Service
@Slf4j
public class JwtService {
    @Value("${security.secret}")
    private String SECRET_KEY;
    private static final long JWT_EXPIRATION = 1000 * 60 * 60 * 24 * 7; //one week


    //################### EXTRACTION #############################################

    public Claims extractAllClaims(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims;
    }


    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        // Function interface -> takes in "Claims", produce T
        // claimsResolver is a funct oin
        return claimsResolver.apply(extractAllClaims(token));

    }

    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject); //subject -> username (not email)
    }

    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }


    //##################### Generation  #########################################

    public String generateToken(
            Map<String,Object> extractClaims,
            UserDetails userDetails){
        String token = Jwts.builder()
                .claims(extractClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+JWT_EXPIRATION))
                .compact();
        return token;

    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    //##################### Utility  #########################################

//seems redudaance to check if username matches token as they came from the same palce
// since my extract claims check the sign in key, i should just car eabout expiration
//    public boolean isTokenValid(String token, UserDetails userDetails){
//        // getusername
//        // just check if the token is expired
//    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public SecretKey getSignInKey(){
        byte[] bytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }

}

















