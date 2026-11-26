package com.curso.eventos.application;

import com.curso.eventos.domain.ContaAcesso;
import com.curso.eventos.domain.Pessoa;
import com.curso.eventos.repository.ContaAcessoRepository;
import com.curso.eventos.repository.PessoaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class ContaAcessoService {

    private final ContaAcessoRepository contas;
    private final PessoaRepository pessoas;
    private final PasswordEncoder passwordEncoder;

    public ContaAcessoService(
            ContaAcessoRepository contas,
            PessoaRepository pessoas,
            PasswordEncoder passwordEncoder) {
        this.contas = contas;
        this.pessoas = pessoas;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ContaAcesso cadastrar(Long pessoaId, String login, String senha) {
        if (pessoaId == null) {
            throw new IllegalArgumentException("Pessoa obrigatoria");
        }
        if (login == null || !login.trim().matches("[A-Za-z0-9._-]{3,80}")) {
            throw new IllegalArgumentException("Login invalido");
        }
        if (senha == null || senha.length() < 12 || senha.length() > 72) {
            throw new IllegalArgumentException("Senha deve ter entre 12 e 72 caracteres");
        }

        String loginNormalizado = login.trim().toLowerCase(Locale.ROOT);

        if (contas.existsByLoginIgnoreCase(loginNormalizado)) {
            throw new IllegalArgumentException("Login ja cadastrado");
        }

        if (contas.existsByPessoaId(pessoaId)) {
            throw new IllegalArgumentException("Pessoa ja possui conta");
        }

        Pessoa pessoa = pessoas.findById(pessoaId)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa nao encontrada"));

        String hash = passwordEncoder.encode(senha);

        return contas.save(new ContaAcesso(pessoa, loginNormalizado, hash));
    }


    @Transactional
    public void alterarSenha(Long contaId, String senhaAtual, String novaSenha) {
        if (contaId == null) {
            throw new IllegalArgumentException("Conta obrigatoria");
        }
        if (novaSenha == null || novaSenha.length() < 12 || novaSenha.length() > 72) {
            throw new IllegalArgumentException("Nova senha deve ter entre 12 e 72 caracteres");
        }

        ContaAcesso conta = contas.findById(contaId)
                .orElseThrow(() -> new IllegalArgumentException("Conta nao encontrada"));

        if (senhaAtual == null ||
                !passwordEncoder.matches(senhaAtual, conta.getSenhaHash())) {
            throw new IllegalArgumentException("Senha atual invalida");
        }

        if (passwordEncoder.matches(novaSenha, conta.getSenhaHash())) {
            throw new IllegalArgumentException("Nova senha deve ser diferente");
        }

        conta.alterarSenha(passwordEncoder.encode(novaSenha));
    }


    @Transactional(readOnly = true)
    public ContaAcesso buscarPorId(Long id) {
        return contas.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta nao encontrada"));
    }

    @Transactional(readOnly = true)
    public ContaAcesso buscarPorLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login obrigatorio");
        }

        String normalizado = login.trim().toLowerCase(Locale.ROOT);

        return contas.findByLoginIgnoreCase(normalizado)
                .orElseThrow(() -> new IllegalArgumentException("Conta nao encontrada"));
    }

    @Transactional
    public ContaAcesso bloquear(Long id) {
        ContaAcesso conta = buscarPorId(id);
        conta.bloquear();
        return conta;
    }

    @Transactional
    public ContaAcesso ativar(Long id) {
        ContaAcesso conta = buscarPorId(id);
        conta.ativar();
        return conta;
    }

}
