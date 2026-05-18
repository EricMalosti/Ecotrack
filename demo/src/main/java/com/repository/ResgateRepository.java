package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.entity.Resgate;

@Repository
public interface ResgateRepository extends JpaRepository<Resgate, Long> {
    
    List<Resgate> findByUsuarioId(Long usuarioId);

    // Consulta customizada para somar os pontos que o usuário já gastou
    @Query("SELECT SUM(r.premio.custoEmPontos) FROM Resgate r WHERE r.usuario.id = :usuarioId")
    Integer calcularPontosGastosDoUsuario(@Param("usuarioId") Long usuarioId);
}