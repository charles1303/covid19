package com.projects.covid19.serviceitem.interceptors;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.projects.covid19.serviceitem.components.CustomUserDetails;
import com.projects.covid19.serviceitem.components.JwtManager;
import com.projects.covid19.serviceitem.models.User;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SecurityInterceptor implements HandlerInterceptor {

	@Autowired
	private JwtManager jwtManager;

	public void setJwtManager(JwtManager jwtManager) {
		this.jwtManager = jwtManager;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		try {
			String jwt = getJwtFromRequest(request);

			if (StringUtils.hasText(jwt) && jwtManager.validateToken(jwt)) {
				String username = jwtManager.getUsernameFromJWT(jwt);
				List<String> roles = jwtManager.getRolesFromJwt(jwt);
				List<GrantedAuthority> authorities = CustomUserDetails.getAuthorities(roles);
				
				User user = new User();
				user.setUsername(username);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,
						jwt, authorities);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				SecurityContextHolder.getContext().setAuthentication(null);
				response = setResponseContent(response);
				 return false;				
			}
		} catch (Exception ex) {
			log.error("Could not set user authentication in security context", ex);
		}

		log.info("pre Handler execution is complete");

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView model)
			throws Exception {
		log.info("Post Handler execution is complete");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception arg3)
			throws Exception {
		try {
			String jwt = getJwtFromRequest(request);

			if (StringUtils.hasText(jwt) && jwtManager.validateToken(jwt)) {
				String username = jwtManager.getUsernameFromJWT(jwt);
				List<String> roles = jwtManager.getRolesFromJwt(jwt);
				List<GrantedAuthority> authorities = CustomUserDetails.getAuthorities(roles);
				
				
				User user = new User();
				user.setUsername(username);
				

				CustomUserDetails userdetails = CustomUserDetails.create(user);

				
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userdetails, "",
						authorities);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
				
				response.setHeader("token", jwtManager.generateToken(authentication));
			} else {
				SecurityContextHolder.getContext().setAuthentication(null);
				SecurityContextHolder.getContext().setAuthentication(null);
				response = setResponseContent(response);
			}
		} catch (Exception ex) {
			log.error("Could not set user authentication in security context", ex);
		}

		log.info("Request is complete");
	}

	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}
	
	private HttpServletResponse setResponseContent(HttpServletResponse httpResponse) {
		httpResponse.setContentType("application/json;charset=UTF-8");
		httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		try {
			httpResponse.getWriter().write(new JSONObject()
			        .put("timestamp", LocalDateTime.now())
			        .put("message", "Unauthorized user or invalid token")
			        .put("status", HttpServletResponse.SC_UNAUTHORIZED)
			        .toString());
		} catch (JSONException | IOException e) {
			log.error("Error generating Unauthorized response message - {}", e.getMessage());
		}
		
		return httpResponse;

	}

}
