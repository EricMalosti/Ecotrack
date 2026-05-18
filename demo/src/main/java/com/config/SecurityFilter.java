package com.config;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.entity.Usuario;
import com.repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        // Ignora a rota de login no log para não poluir
        if(request.getRequestURI().contains("/login") || request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("➡️ Requisição chegando para: " + request.getRequestURI());
        String token = recuperarToken(request);
        
        if (token != null) {
            System.out.println("🔑 Token detectado na mão do usuário!");
            String emailDono = tokenService.validarToken(token);

            if (!emailDono.isEmpty()) {
                System.out.println("✅ Token autêntico, pertence a: " + emailDono);
                Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(emailDono);
                
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("🔓 Porta destrancada com sucesso!");
                } else {
                    System.out.println("❌ Usuário não existe mais no banco.");
                }
            }
        } else {
            System.out.println("⚠️ ALERTA: Nenhum Token enviado pelo site (Header vazio).");
        }
        
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}