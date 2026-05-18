package com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Útil para o sistema de login verificar se o usuário existe
    Optional<Usuario> findByEmail(String email);
    
    // Útil para evitar cadastros duplicados
    Optional<Usuario> findByCpf(String cpf);
}
