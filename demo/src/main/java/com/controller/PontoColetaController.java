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

import com.entity.PontoColeta;
import com.repository.PontoColetaRepository;

@RestController
@RequestMapping("/api/pontos-coleta")
@CrossOrigin(origins = "*")
public class PontoColetaController {

    @Autowired
    private PontoColetaRepository pontoColetaRepository;

    // Rota para salvar o ponto (que você usou no Insomnia)
    @PostMapping
    public ResponseEntity<PontoColeta> cadastrar(@RequestBody PontoColeta ponto) {
        return ResponseEntity.status(201).body(pontoColetaRepository.save(ponto));
    }

    // Rota NOVA para listar todos os ecopontos
    @GetMapping
    public ResponseEntity<List<PontoColeta>> listarTodos() {
        return ResponseEntity.ok(pontoColetaRepository.findAll());
    }
}