package com.aluracursos.forohub.infra.security;

import com.aluracursos.forohub.domain.usuarios.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var autenticacionHeader = request.getHeader("authorization");
        System.out.println("header " + autenticacionHeader);

        if (autenticacionHeader != null){
            String token = autenticacionHeader.replace("Bearer ","");
            var nombreUsuario = tokenService.obtenerUsuarioToken(token);
            var idUsuario = tokenService.obtenerIdToken(token);

            if (nombreUsuario != null && idUsuario != null ){
                // Token valido
                var usuario = usuarioRepository.findByEmail(nombreUsuario);
                // Forzamos el inicio de sesion
                var autenticacion = new UsernamePasswordAuthenticationToken(idUsuario,null,usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            }
        }
        filterChain.doFilter(request, response);
    }
}
