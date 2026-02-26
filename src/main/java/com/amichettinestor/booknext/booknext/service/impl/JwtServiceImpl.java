package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {


    private static final Logger logger = LogManager.getLogger(JwtServiceImpl.class);

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    //Convierte el secret en una clase Key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String extractUsername(String token) {
        //Extraigo todos los claims
        var claims =this.extractAllClaims(token);
        //Llamo para extraer el claim
        //El segundo parámetro define el comportamiento de la interfaz funcional
        // y lo que hará su método apply
        return extractClaim(claims, Claims->claims.getSubject());
    }

    @Override
    public boolean istokenValid(String token, UserDetails userDetails) {
        var username = extractUsername(token);

        //El token es válido si no está expirado y el username del token es igual al del userDetails
        return username.equals(userDetails.getUsername()) && !this.isExpired(token);
    }


    //Creamos un método que genere el token
    //Este es por si no queremos pasar Map para los claims
    @Override
    public String generatetoken(UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(new HashMap<>())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Creamos un método para extraer todos los Claims del token
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(this.getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    //Creamos un método que extraiga una única Claim.
    //Es genético porque T es llamado por un método que pide un String y otro un Date
    private <T> T extractClaim(Claims claims,Function<Claims,T> functionClaims){
        //El llamado que envió el parámetro functionClaims define el comportamiento del método apply
        return functionClaims.apply(claims);
    }

    //Creamos un método para ver si el token está expirado
    private boolean isExpired(String token){

        //Extaigo todos los claims
        var claims = this.extractAllClaims(token);

        //Le indico el comportamiento a la interfaz funcional
        //Dado todos los claims, quiero que me devuelva la fecha de expiración
        var extractClaim = this.extractClaim(claims,Claims->claims.getExpiration());

        //Retorna true si la fecha de expiración es aterior a la fecha actual
        return extractClaim.before(new Date());
    }
}
