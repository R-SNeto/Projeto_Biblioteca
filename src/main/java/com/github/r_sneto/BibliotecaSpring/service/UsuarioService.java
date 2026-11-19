package com.github.r_sneto.BibliotecaSpring.service;

import com.github.r_sneto.BibliotecaSpring.exception.ConflitoException;
import com.github.r_sneto.BibliotecaSpring.exception.CredenciaisInvalidasException;
import com.github.r_sneto.BibliotecaSpring.exception.RecursoNaoEncontradoException;
import com.github.r_sneto.BibliotecaSpring.exception.RequisicaoInvalidaException;
import com.github.r_sneto.BibliotecaSpring.model.Usuario;
import com.github.r_sneto.BibliotecaSpring.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private static final int TAMANHO_MINIMO_SENHA = 6;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado. Id: " + id));
    }

    public Usuario findByLogin(String login) {
        return usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado. Login: " + login));
    }

    @Transactional
    public Usuario insert(String nome, String login, String senha) {
        validarNomeELogin(nome, login);
        validarSenha(senha);
        if (usuarioRepository.existsByLogin(login.trim())) {
            throw new ConflitoException("O login '" + login.trim() + "' já está em uso.");
        }
        Usuario usuario = new Usuario(nome.trim(), login.trim());
        usuario.setSenha(passwordEncoder.encode(senha));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario update(Long id, String nome, String login) {
        validarNomeELogin(nome, login);
        Usuario entity = findById(id);
        if (usuarioRepository.existsByLoginAndIdNot(login.trim(), id)) {
            throw new ConflitoException("O login '" + login.trim() + "' já está em uso.");
        }
        entity.setNome(nome.trim());
        entity.setLogin(login.trim());
        return usuarioRepository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        usuarioRepository.deleteById(id);
    }

    // ---------- Autenticação ----------

    public Usuario autenticar(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isEmpty()) {
            throw new CredenciaisInvalidasException("Login ou senha inválidos.");
        }
        Usuario usuario = usuarioRepository.findByLogin(login.trim())
                .orElseThrow(() -> new CredenciaisInvalidasException("Login ou senha inválidos."));
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Login ou senha inválidos.");
        }
        return usuario;
    }

    @Transactional
    public void alterarSenha(Long id, String senhaAtual, String novaSenha) {
        Usuario usuario = findById(id);
        if (senhaAtual == null || !passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Senha atual incorreta.");
        }
        validarSenha(novaSenha);
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    private void validarNomeELogin(String nome, String login) {
        if (nome == null || nome.isBlank()) {
            throw new RequisicaoInvalidaException("O nome do usuário é obrigatório.");
        }
        if (login == null || login.isBlank()) {
            throw new RequisicaoInvalidaException("O login é obrigatório.");
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new RequisicaoInvalidaException(
                    "A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
    }

}
