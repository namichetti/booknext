package com.amichettinestor.booknext.booknext.filter;

import com.amichettinestor.booknext.booknext.service.JwtService;
import com.amichettinestor.booknext.booknext.service.impl.CustomUserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtService jwtService;
    private final CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        //Obtenemos el header del request
        String requestHeader = request.getHeader("Authorization");

        //Obteniendo su header y verificamos si el mismo es nulo o empiece con "Bearer"
        if (requestHeader == null || !requestHeader.startsWith("Bearer ")) {

            //Si cumple ambas condiciones indeseadas, entonces:

            //Pasamos al siguiente filtro
            filterChain.doFilter(request, response);

            //Salimos del método
            return;
        }

        // Si el token es no nulo o empieza con "Bearer"
        // Extraemos el token del header.
        String token = requestHeader.substring(7);

        String username;

        try {
            //Extraemos el username del token llamando al JwtService
            username = jwtService.extractUsername(token);
        } catch (ExpiredJwtException e) {
            logger.info("Token expirado: "+ e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        } catch (JwtException e) {
            logger.info("JWT inválido: "+ e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (username == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            logger.info("No se puede extraer el username del token. El username es nulo.");
            return;
        }


        // Verifica si el usuario no está autenticado
        if(SecurityContextHolder.getContext().getAuthentication() == null) {

            //Si cumple las condiciones llama al UserDetailsService
            UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

            // Verificamos si el token es válido
            var istokenValid = this.jwtService.istokenValid(token, userDetails);

            if(istokenValid){
                //Creamos un objeto de autenticación ya autenticado
                //Por ser "ya autenticado", credentials es nulo.
                UsernamePasswordAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities());

                //Actualizamos el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        }
        //Pasamos al siguiente filtro luego de que todo esté ok.
        filterChain.doFilter(request, response);
    }

}
