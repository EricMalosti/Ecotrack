package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.entity.TransacaoReciclagem;

@Repository
public interface TransacaoReciclagemRepository extends JpaRepository<TransacaoReciclagem, Long> {

    // Retorna todo o histórico de um usuário específico
    List<TransacaoReciclagem> findByUsuarioId(Long usuarioId);

    // Consulta customizada para somar todos os pontos que um usuário já gerou
    @Query("SELECT SUM(t.pontosGerados) FROM TransacaoReciclagem t WHERE t.usuario.id = :usuarioId")
    Integer calcularPontosTotaisDoUsuario(@Param("usuarioId") Long usuarioId);
}