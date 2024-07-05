package com.jsp.osa.securityfilters;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jsp.osa.entity.AccessToken;
import com.jsp.osa.entity.RefreshToken;
import com.jsp.osa.exception.InavlidJwtException;
import com.jsp.osa.repository.AccessTokenRepo;
import com.jsp.osa.repository.RefreshTokenRepo;
import com.jsp.osa.security.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor

public class JwtAuthFilters extends OncePerRequestFilter {

	private JwtService jwtService;
	
	private RefreshTokenRepo refreshTokenRepo;
	
	private AccessTokenRepo accessTokenRepo;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		TokenExceptionHandler handler = new TokenExceptionHandler();
		Cookie[] cookie = request.getCookies();

		String at = null,rt=null;

		if (cookie != null) {

			for (Cookie cookies : cookie) {

				if (cookies.getName().equals("at")) 

					at = cookies.getValue();
				
				else if(cookies.getName().equals("rt"))

				   if (at != null && rt != null) {
			            Optional<RefreshToken> optionalRT = refreshTokenRepo.findByRefreshToken(rt);
			            Optional<AccessToken> optionalAT = accessTokenRepo.findByToken(at);

			            if (optionalRT.isPresent() && optionalAT.isPresent()) {
			                RefreshToken refreshToken = optionalRT.get();
			                AccessToken accessToken = optionalAT.get();
			                if (!refreshToken.isBlocked() & !accessToken.isBlocked()) {
						try {

							String userName = jwtService.extractUserName(at);
							String userRole = jwtService.extractUserRole(at);

							if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

								UsernamePasswordAuthenticationToken token1 = new UsernamePasswordAuthenticationToken(
										userName, null,
										List.of(new SimpleGrantedAuthority(userRole)));

								token1.setDetails(new WebAuthenticationDetails(request));

								SecurityContextHolder.getContext().setAuthentication(token1);

								

							}
						} catch (ExpiredJwtException ex) {

							handler.tokenException(HttpStatus.GATEWAY_TIMEOUT.value(), response, "token expired");

						} catch (JwtException ex) {
							throw new InavlidJwtException("jwt expired");
						}

					}


				}
			}

		filterChain.doFilter(request, response);
		}

		}
	}
}


