package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.Entities.AdminEntity;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${proliferate.app.jwtsecret}")
    private String SECRET;

    public String genToken(UserDetails userDetails, Object o){
        return Jwts.builder().setSubject(userDetails.getUsername())
		        .claim("role", userDetails.getAuthorities())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 18))
                .signWith(getLoginKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    private  <T> T extractClaim(String token, Function<Claims, T> claimResolvers){
        final Claims claims = extraAllClaims(token);
        return claimResolvers.apply(claims);
    }

    public Key getLoginKey(){
        byte[] key = Decoders.BASE64URL.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }

    private Claims extraAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getLoginKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

   @Override
  public Long getUserId() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (principal instanceof StudentEntity) {
        StudentEntity student = (StudentEntity) principal;
        return student.getStudentId();
    } else if (principal instanceof TutorEntity) {
        TutorEntity tutor = (TutorEntity) principal;
        return tutor.getTutorId();
    }  else if (principal instanceof AdminEntity) {
        AdminEntity admin = (AdminEntity) principal;
        return admin.getAdminId();
    } else {
        throw new IllegalArgumentException("Unknown principal type");
    }
}
    @Override
    public Long geTutorId() {
        TutorEntity user = (TutorEntity) SecurityContextHolder
                .getContext().
                getAuthentication()
                .getPrincipal();

        return user.getTutorId();
    }
}
