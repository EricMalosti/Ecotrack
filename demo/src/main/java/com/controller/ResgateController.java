package com.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.Premio;
import com.entity.Resgate;
import com.repository.PremioRepository;
import com.repository.ResgateRepository;
import com.repository.TransacaoReciclagemRepository;

@RestController
@RequestMapping("/api/resgates")
@CrossOrigin(origins = "*")
public class ResgateController {

    @Autowired
    private ResgateRepository resgateRepository;

    @Autowired
    private TransacaoReciclagemRepository transacaoRepository;

    @Autowired
    private PremioRepository premioRepository;

    @PostMapping("/solicitar")
    public ResponseEntity<?> realizarResgate(@RequestBody Resgate resgate) {
        Long usuarioId = resgate.getUsuario().getId();

        // 1. Calcula os pontos ganhos e gastos
        Integer ganhos = transacaoRepository.calcularPontosTotaisDoUsuario(usuarioId);
        if (ganhos == null) ganhos = 0;

        Integer gastos = resgateRepository.calcularPontosGastosDoUsuario(usuarioId);
        if (gastos == null) gastos = 0;

        // 2. Saldo Real
        int saldoAtual = ganhos - gastos;

        // 3. Busca o prêmio para saber o preço e o estoque
        Optional<Premio> premioOpt = premioRepository.findById(resgate.getPremio().getId());
        if (!premioOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Prêmio não encontrado.");
        }
        
        Premio premio = premioOpt.get();

        // 4. A GRANDE VALIDAÇÃO
        if (saldoAtual >= premio.getCustoEmPontos() && premio.getEstoque() > 0) {
            resgate.setDataResgate(LocalDateTime.now());
            Resgate salvo = resgateRepository.save(resgate);

            // Diminui o estoque e salva a atualização do prêmio
            premio.setEstoque(premio.getEstoque() - 1);
            premioRepository.save(premio);

            return ResponseEntity.ok(salvo);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: Saldo insuficiente ou prêmio esgotado. Seu saldo atual é: " + saldoAtual);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Resgate>> listarResgatesDoUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(resgateRepository.findByUsuarioId(usuarioId));
    }
}