package io.mannsgoggel.tournamentserver.config;

import org.apache.catalina.connector.RequestFacade;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class CorsSettings implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        RequestFacade request = (RequestFacade) req;

        var allowedOrigins = List.of("http://localhost:3000", "https://www.mannsgoggel.io", "https://mannsgoggel.io");

        var origin = request.getHeader("Origin");

        if (allowedOrigins.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

}