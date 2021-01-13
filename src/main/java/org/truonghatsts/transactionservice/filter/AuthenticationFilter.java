package org.truonghatsts.transactionservice.filter;

import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.truonghatsts.transactionservice.config.ApplicationProperties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private ApplicationProperties props;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().matches(props.getSecurity().getAllowed());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String apikey = httpServletRequest.getHeader("X-API-KEY");
        if (StringUtils.isNotBlank(apikey)) {
            if (apikey.equals(props.getSecurity().getApiKey())) {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            } else {
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
        }
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
