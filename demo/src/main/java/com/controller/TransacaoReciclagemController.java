package com.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.Material;
import com.entity.TransacaoReciclagem;
import com.repository.MaterialRepository;
import com.repository.TransacaoReciclagemRepository;

@RestController
@RequestMapping("/api/transacoes")
@CrossOrigin(origins = "*")
public class TransacaoReciclagemController {

    @Autowired
    private TransacaoReciclagemRepository transacaoRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private com.repository.ResgateRepository resgateRepository;

    // 1. Registrar uma nova entrega de material
    @PostMapping
    public ResponseEntity<TransacaoReciclagem> registrarEntrega(@RequestBody TransacaoReciclagem transacao) {
        // Busca qual foi o material entregue para saber o multiplicador de pontos
        Optional<Material> materialOpt = materialRepository.findById(transacao.getMaterial().getId());
        
        if (materialOpt.isPresent()) {
            Material material = materialOpt.get();
            // REGRA DE NEGÓCIO: Peso * Pontos por Kg
            int pontosCalculados = (int) (transacao.getPesoKg() * material.getPontosPorKg());
            
            transacao.setPontosGerados(pontosCalculados);
            transacao.setDataEntrega(LocalDateTime.now()); // Registra a hora exata
            
            TransacaoReciclagem salva = transacaoRepository.save(transacao);
            return ResponseEntity.ok(salva);
        }
        return ResponseEntity.badRequest().build();
    }

    // 2. DASHBOARD DO USUÁRIO: Histórico de entregas
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TransacaoReciclagem>> buscarHistoricoUsuario(@PathVariable Long usuarioId) {
        List<TransacaoReciclagem> historico = transacaoRepository.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(historico);
    }

    // 3. DASHBOARD DO USUÁRIO: Total de pontos que ele tem
    @GetMapping("/usuario/{usuarioId}/pontos")
    public ResponseEntity<Integer> buscarPontosTotais(@PathVariable Long usuarioId) {
        Integer ganhos = transacaoRepository.calcularPontosTotaisDoUsuario(usuarioId);
        if (ganhos == null) ganhos = 0;

        Integer gastos = resgateRepository.calcularPontosGastosDoUsuario(usuarioId);
        if (gastos == null) gastos = 0;

        int saldoReal = ganhos - gastos;
        
        return ResponseEntity.ok(saldoReal);
    }
    
    // O Dashboard do Admin (toneladas por bairro) pode ser feito puxando todos os dados 
    // com um findAll() e agrupando no Frontend, ou criando uma Query JPQL complexa no Repository.
    @GetMapping("/admin/todas")
    public ResponseEntity<List<TransacaoReciclagem>> listarTodasParaDashboardAdmin() {
        return ResponseEntity.ok(transacaoRepository.findAll());
    }
    // Rota EXCLUSIVA para o Admin: Lista TODAS as transações do sistema
    @GetMapping
    public ResponseEntity<List<TransacaoReciclagem>> listarTodasTransacoes() {
        return ResponseEntity.ok(transacaoRepository.findAll());
    }
}