package ims.services;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ims.models.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	@Value("${jwt.secret}")
	String secret;

	@Value("${jwt.expiration-ms}")
	long expirationMs;

	public String generateToken(Users user) {
		Date now = new Date();
		return Jwts.builder()
				.subject(user.getEmail())
				.claim("uid", user.getId())
				.claim("role", user.getRole())
				.claim("fullname", user.getFullname())
				.issuedAt(now)
				.expiration(new Date(now.getTime() + expirationMs))
				.signWith(signingKey())
				.compact();
	}

	public Claims validateToken(String token) {
		return Jwts.parser()
				.verifyWith(signingKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public String getEmail(String token) {
		return validateToken(token).getSubject();
	}

	public int getRole(String token) {
		Object role = validateToken(token).get("role");
		return Integer.parseInt(role.toString());
	}

	private SecretKey signingKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
}
