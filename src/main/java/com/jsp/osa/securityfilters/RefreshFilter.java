package com.jsp.osa.securityfilters;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jsp.osa.repository.RefreshTokenRepo;
import com.jsp.osa.security.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RefreshFilter extends OncePerRequestFilter {

	private JwtService jwtService;
	private RefreshTokenRepo refreshTokenRepo;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		TokenExceptionHandler handler = new TokenExceptionHandler();
		Cookie[] cookie = request.getCookies();
		String refreshToken = null;

		if (cookie != null) {

			for (Cookie cookies : cookie) {

				if (cookies.getName().equals("rt")) {

					refreshToken = cookies.getValue();
				}

				if (refreshToken == null || !jwtService.isTokenValid(refreshToken)) {
					handler.tokenException(HttpStatus.UNAUTHORIZED.value(), response, "Token is blocked");
					return;
				}

				try {
					String username = jwtService.extractUserName(refreshToken);
					String userRole = jwtService.extractUserRole(refreshToken);

					if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

						UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
								username, null, List.of(new SimpleGrantedAuthority(userRole.toString())));
						usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetails(request));
						SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
					}

				} catch (ExpiredJwtException ex) {

					handler.tokenException(HttpStatus.UNAUTHORIZED.value(), response, "token time out");
				}

			}
			filterChain.doFilter(request, response);
		}
	}
}
