package com.jsp.osa.securityfilters;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.jsp.osa.exception.UserAlreadyLoggedInException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		boolean loggedIn = false;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("at") || cookie.getName().equals("rt")) {
					loggedIn = true;
				}

			}
		}
		if (loggedIn) {
			throw new UserAlreadyLoggedInException("User already logged in");
		}

		else {
			filterChain.doFilter(request, response);
		}

	}
}
