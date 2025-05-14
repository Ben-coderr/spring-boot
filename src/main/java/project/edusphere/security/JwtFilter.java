package project.edusphere.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwt;

    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                              FilterChain chain) throws IOException, ServletException {
        String hdr = req.getHeader("Authorization");
        if (hdr != null && hdr.startsWith("Bearer ")) {
            var auth = jwt.parse(hdr.substring(7));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(req, res);
    }
}
