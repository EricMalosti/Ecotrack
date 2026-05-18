package com.config;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.entity.Usuario;

@Service
public class TokenService {

    private final String secret = "minha-chave-secreta-ecotrack";

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("EcoTrack_API")
                    .withSubject(usuario.getEmail())
                    .withClaim("perfil", usuario.getPerfil())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("EcoTrack_API")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            // 🚨 IMPRIME O MOTIVO EXATO DA FALHA DO CRACHÁ
            System.out.println("❌ ERRO NO TOKEN: " + exception.getMessage());
            return "";
        }
    }

    private Instant gerarDataExpiracao() {
        // Correção definitiva de horário (2 horas em segundos a partir de agora)
        return Instant.now().plusSeconds(7200); 
    }
}