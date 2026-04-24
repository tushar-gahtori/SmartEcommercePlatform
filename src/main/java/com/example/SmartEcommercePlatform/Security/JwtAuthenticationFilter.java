package com.example.SmartEcommercePlatform.Security;

import com.example.SmartEcommercePlatform.Service.CustomerUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomerUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException{

        //Look for the "Authorization" header
        final String authHeader = request.getHeader("Authorization");
        String email=null;
        String jwt=null;

        //Check if it starts with "Bearer " (The standard for JWTs)
        if(authHeader!=null && authHeader.startsWith("Bearer ")){
            jwt=authHeader.substring(7);//Remove "Bearer " to grab just the token
            try{
                email=jwtUtil.extractEmail(jwt);
            }catch(Exception e){
                System.out.println("Invalid Token");
            }
        }

        //If we found an email, and the user isn't already logged in...
        if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            //validate the token
            if(jwtUtil.validateToken(jwt,email)){

                //user is authenticated
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
                        email,null,userDetails.getAuthorities() // Empty list means no specific roles (yet)
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        //Pass the request to the next filter/controller
        filterChain.doFilter(request,response);

    }
}
