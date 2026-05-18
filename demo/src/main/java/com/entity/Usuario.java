package com.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity; // Se estiver usando Lombok
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data // Cria getters e setters automaticamente
@Entity
@Table(name = "usuarios") // O nome exato da tabela no seu MySQL
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String sobrenome;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String cpf;
    
    // ... adicione os outros campos como cep, cidade, etc.

    @Column(nullable = false)
    private String senha;

   @Column(nullable = false)
    private String perfil = "USER";

    public String getPerfil() { 
        return perfil; 
    }
    
    public void setPerfil(String perfil) { 
        this.perfil = perfil; 
    }
    
}