package com.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "transacoes_reciclagem")
public class TransacaoReciclagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Chave estrangeira para Usuario
    @ManyToOne 
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Chave estrangeira para Ponto de Coleta
    @ManyToOne 
    @JoinColumn(name = "ponto_coleta_id", nullable = false)
    private PontoColeta pontoColeta;

    // Chave estrangeira para Material
    @ManyToOne 
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(name = "peso_kg", nullable = false)
    private Double pesoKg;

    @Column(name = "pontos_gerados", nullable = false)
    private Integer pontosGerados;

    @Column(name = "data_entrega", nullable = false)
    private LocalDateTime dataEntrega;
}
