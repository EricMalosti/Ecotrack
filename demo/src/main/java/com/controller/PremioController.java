package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.Premio;
import com.repository.PremioRepository;

@RestController
@RequestMapping("/api/premios")
@CrossOrigin(origins = "*")
public class PremioController {

    @Autowired
    private PremioRepository premioRepository;

    @GetMapping
    public ResponseEntity<List<Premio>> listarPremiosDisponiveis() {
        return ResponseEntity.ok(premioRepository.findAll());
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Premio> cadastrarPremio(@RequestBody Premio premio) {
        // Apenas o admin deveria acessar esta rota em um sistema com Spring Security
        return ResponseEntity.ok(premioRepository.save(premio));
    }
}