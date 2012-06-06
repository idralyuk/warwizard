package com.yammer.dropwizard.servlets;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.sun.jersey.core.util.Base64;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;

/**
 * Allows access to filtered resources if the request provides basic authentication.
 */
public class BasicAuthFilter implements Filter {
    private final String username;
    private final String password;
    private final Splitter splitter =  Splitter.on(':').limit(2);
    private final int credOffset = "Basic ".length();

    public BasicAuthFilter(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        String auth = ((HttpServletRequest)request).getHeader("Authorization");
        if (auth != null && auth.startsWith("Basic ")) {
            String combined = new String(Base64.decode(auth.substring(credOffset)), Charsets.UTF_8);
            Iterator<String> credentials = splitter.split(combined).iterator();
            if (credentials.hasNext() && username.equals(credentials.next()) &&
                    credentials.hasNext() && password.equals(credentials.next())) {
                chain.doFilter(request, response);
                return;
            }
        }

        HttpServletResponse resp = (HttpServletResponse)response;
        resp.setHeader("WWW-Authenticate", "Basic realm=\"Admin\"" );
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {}

    @Override
    public void destroy() {}
}
