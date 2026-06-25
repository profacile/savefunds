package be.profacile.savefunds.security.jwt;

import be.profacile.savefunds.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String userEmail;

        // Vérifier si le header Authorization existe et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraire le token (enlever "Bearer ")
        token = authHeader.substring(7);

        try {
            // Extraire l'email du token
            userEmail = jwtUtil.extractEmail(token);

            // Si l'email existe et qu'il n'y a pas encore d'authentification
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Charger les détails de l'utilisateur
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // Valider le token
                if (jwtUtil.validateToken(token, userDetails.getUsername())) {

                    // Créer l'authentification
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Mettre l'authentification dans le contexte Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // En cas d'erreur (token invalide, expiré, etc.), ne rien faire
            // Le filtre suivant gérera l'accès non autorisé
        }

        // Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}