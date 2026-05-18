package com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entity.Usuario;
import com.repository.UsuarioRepository;

@RestController // Diz ao Spring que esta classe vai receber requisições da internet (URLs)
@RequestMapping("/api/usuarios") // Define o endereço base. Todas as URLs começarão com /api/usuarios
@CrossOrigin(origins = "*") // Permite que o seu frontend (como um site em HTML/JS ou React) acesse esta API
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; //CRIPTOGRAFIA 

    @Autowired
    private com.config.TokenService tokenService;

    // 1. CADASTRAR USUÁRIO (AGORA COM SENHA CRIPTOGRAFADA)
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("E-mail já cadastrado");
        }
        
        // Pega a senha que a pessoa digitou, embaralha, e salva no usuário de novo
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);
        
        return ResponseEntity.status(201).body(usuarioRepository.save(usuario));
    }

    // 2. LOGIN (AGORA DEVOLVENDO O CRACHÁ JWT)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> dados) {
        String email = dados.get("email");
        String senhaDigitada = dados.get("senha");

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Verifica se a senha bate
            if (passwordEncoder.matches(senhaDigitada, usuario.getSenha())) {
                
                // MÁGICA AQUI: Gera o token!
                String token = tokenService.gerarToken(usuario);

                // Prepara um "pacote" para enviar ao frontend com os dados e o token
                Map<String, Object> resposta = new HashMap<>();
                resposta.put("id", usuario.getId());
                resposta.put("nome", usuario.getNome());
                resposta.put("perfil", usuario.getPerfil());
                resposta.put("token", token); // <-- O Crachá!

                return ResponseEntity.ok(resposta);
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos");
    }
   
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        
        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
    }

   // listar todos os usuários cadastrados
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }
    //  REDEFINIR SENHA
    @PutMapping("/redefinir-senha")
    public ResponseEntity<String> redefinirSenha(@RequestBody Map<String, String> dados) {
        String email = dados.get("email");
        String cpf = dados.get("cpf");
        String novaSenha = dados.get("novaSenha");

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            if (usuario.getCpf().equals(cpf)) {
                
                //  CRIPTOGRAFIA AQUI 
                String senhaCriptografada = passwordEncoder.encode(novaSenha);
                usuario.setSenha(senhaCriptografada); 
                // -------------------------------------
                
                usuarioRepository.save(usuario); 
                return ResponseEntity.ok("Senha alterada com sucesso!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: O CPF não corresponde a este e-mail.");
            }
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: E-mail não encontrado.");
    }
    
   // Cadastro exclusivo de Administradores
    @PostMapping("/cadastrar-admin")
    public ResponseEntity<?> cadastrarAdmin(@RequestBody Usuario usuario, @RequestParam String chaveSecreta) {
        String CHAVE_MESTRE = "ECO-MASTER-2026"; 

        if (CHAVE_MESTRE.equals(chaveSecreta)) {
            // Define o perfil
            usuario.setPerfil("ADMIN"); 
            
            // CRIPTOGRAFIA AQUI 
            String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
            usuario.setSenha(senhaCriptografada);
            // -------------------------------------

            return ResponseEntity.status(201).body(usuarioRepository.save(usuario));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chave de segurança inválida!");
        }
    }

}
