package com.jsp.osa.securityfilters;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jsp.osa.exception.InavlidJwtException;
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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		TokenExceptionHandler handler = new TokenExceptionHandler();
		Cookie[] cookie = request.getCookies();

		String token = null;

		if (cookie != null) {

			for (Cookie cookies : cookie) {

				if (cookies.getName().equals("at")) {

					token = cookies.getValue();
				}

					if (token != null) {
						try {

							String userName = jwtService.extractUserName(token);
							String userRole = jwtService.extractUserRole(token);

							if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

								UsernamePasswordAuthenticationToken token1 = new UsernamePasswordAuthenticationToken(
										userName, null,
										List.of(new SimpleGrantedAuthority(userRole)));

								token1.setDetails(new WebAuthenticationDetails(request));

								SecurityContextHolder.getContext().setAuthentication(token1);

								System.out.println(token);
								System.out.println(userName);
								System.out.println(userRole);

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


