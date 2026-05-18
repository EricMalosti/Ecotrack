package com.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.Administrador;
import com.repository.AdministradorRepository;

@RestController
@RequestMapping("/api/administradores")
@CrossOrigin(origins = "*")
public class AdministradorController {

    @Autowired
    private AdministradorRepository adminRepository;

    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrarAdmin(@RequestBody Administrador admin) {
        if (adminRepository.findByMatricula(admin.getMatricula()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Matrícula já cadastrada.");
        }
        adminRepository.save(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body("Administrador cadastrado!");
    }

    @PostMapping("/login")
    public ResponseEntity<Administrador> loginAdmin(@RequestBody Administrador dadosLogin) {
        Optional<Administrador> admin = adminRepository.findByEmail(dadosLogin.getEmail());
        if (admin.isPresent() && admin.get().getSenha().equals(dadosLogin.getSenha())) {
            return ResponseEntity.ok(admin.get());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}