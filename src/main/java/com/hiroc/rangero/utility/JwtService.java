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
import java.util.*;
import java.util.function.Function;


//Bean dependencies
@Service
@Slf4j
public class JwtService {
    @Value("${security.secret}")
    private String SECRET_KEY;
    private static final long JWT_EXPIRATION = 1000 * 60 * 60 * 24 * 7; //one week


    //################### EXTRACTION #############################################

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        // Function interface -> takes in "Claims", produce T
        // claimsResolver is a funct oin
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
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
        //Useless
        extractClaims.put("jit", UUID.randomUUID().toString());
        String token = Jwts.builder()
                .claims(extractClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+JWT_EXPIRATION))
                .signWith(getSignInKey())
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
        log.debug("Expiration of jwt: {}",extractExpiration(token));
        log.debug("new Date: {}",new Date());
        return extractExpiration(token).before(new Date());
    }

    public SecretKey getSignInKey(){
        byte[] bytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }

}

















