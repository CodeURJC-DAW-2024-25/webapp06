package es.codeurjc.global_mart.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserLoginService {

	private static final Logger log = LoggerFactory.getLogger(UserLoginService.class);

	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtTokenProvider jwtTokenProvider;

	public UserLoginService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
			JwtTokenProvider jwtTokenProvider) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	public boolean login(HttpServletResponse response, LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);

			UserDetails user = userDetailsService.loadUserByUsername(loginRequest.getUsername());

			var newAccessToken = jwtTokenProvider.generateAccessToken(user);
			var newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

			response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));
			response.addCookie(buildTokenCookie(TokenType.REFRESH, newRefreshToken));

			return true;
		} catch (Exception e) {
			log.error("Login failed for user: {}", loginRequest.getUsername(), e);
			return false;
		}
	}

	public boolean refresh(HttpServletResponse response, String refreshToken) {
		try {
			var claims = jwtTokenProvider.validateToken(refreshToken);
			UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());

			var newAccessToken = jwtTokenProvider.generateAccessToken(user);
			response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));

			return true;

		} catch (Exception e) {
			log.error("Error while processing refresh token", e);
			return false;
		}
	}

	public boolean logout(HttpServletResponse response) {
		try {
			SecurityContextHolder.clearContext();
			response.addCookie(removeTokenCookie(TokenType.ACCESS));
			response.addCookie(removeTokenCookie(TokenType.REFRESH));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Cookie buildTokenCookie(TokenType type, String token) {
		Cookie cookie = new Cookie(type.cookieName, token);
		cookie.setMaxAge((int) type.duration.getSeconds());
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		return cookie;
	}

	private Cookie removeTokenCookie(TokenType type) {
		Cookie cookie = new Cookie(type.cookieName, "");
		cookie.setMaxAge(0);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		return cookie;
	}
}
