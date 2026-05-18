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

import com.entity.Material;
import com.repository.MaterialRepository;

@RestController
@RequestMapping("/api/materiais")
@CrossOrigin(origins = "*") // Permite que o HTML acesse essa rota
public class MaterialController {

    @Autowired
    private MaterialRepository materialRepository;

    // Rota para salvar material (que você usou no Insomnia)
    @PostMapping
    public ResponseEntity<Material> cadastrar(@RequestBody Material material) {
        return ResponseEntity.status(201).body(materialRepository.save(material));
    }

    // Rota NOVA para listar todos os materiais nas caixinhas do Ecoponto
    @GetMapping
    public ResponseEntity<List<Material>> listarTodos() {
        return ResponseEntity.ok(materialRepository.findAll());
    }
}