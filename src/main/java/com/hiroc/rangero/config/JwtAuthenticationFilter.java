package com.hiroc.rangero.config;

import com.hiroc.rangero.utility.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //TODO create custom exception handle for jwt

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; //my own bead



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Check security context
        //Just a defensive measure, the context should be null by right
        log.debug("JWT AUTH FILTER - CALLED");
        if (SecurityContextHolder.getContext().getAuthentication()!=null){
            log.debug("JWT Skipped - Contexted filled");
            filterChain.doFilter(request,response);
        }

        //Extract header
        String authHeader = request.getHeader("Authorization");
        if (authHeader==null || !authHeader.startsWith("Bearer ") ){
            log.debug("JWT Token not found, SKIP` ");
            filterChain.doFilter(request,response);
            return;
        }
        //Extract token and username
        String token = authHeader.substring(7);
        String username = null;
        try{
            username = jwtService.extractUsername(token);
            log.debug("extracted username {}",username);
        } catch (Exception e){
            log.debug("JWT username extraction failed");
            //TODO test if this throws proper exception
        }
        //Check token validity
        if (!jwtService.isTokenExpired(token)){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails==null){
                throw new UsernameNotFoundException("Jwt auth failed. User not found");
            }
            //Put user in the security context;
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else{
            //Expired token
            throw new RuntimeException("JWT Token has expired");
        }

        filterChain.doFilter(request,response);
    }
}





























