package com.github.r_sneto.BibliotecaSpring.service;

import com.github.r_sneto.BibliotecaSpring.exception.ConflitoException;
import com.github.r_sneto.BibliotecaSpring.exception.RecursoNaoEncontradoException;
import com.github.r_sneto.BibliotecaSpring.exception.RequisicaoInvalidaException;
import com.github.r_sneto.BibliotecaSpring.model.Autor;
import com.github.r_sneto.BibliotecaSpring.model.Livro;
import com.github.r_sneto.BibliotecaSpring.repository.AutorRepository;
import com.github.r_sneto.BibliotecaSpring.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    public List<Livro> findAll() {
        return livroRepository.findAll();
    }

    public List<Livro> findByTitulo(String titulo) {
        return livroRepository.findByTituloContainingIgnoreCase(titulo);
    }

    public List<Livro> findByAutor(int autorId) {
        return livroRepository.findByAutorId(autorId);
    }

    public List<Livro> findByDisponivel(boolean disponivel) {
        return livroRepository.findByDisponivel(disponivel);
    }

    public Livro findById(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado. Id: " + id));
    }

    @Transactional
    public Livro insert(Livro livro) {
        validar(livro);
        if (temIsbn(livro) && livroRepository.existsByIsbn(livro.getIsbn())) {
            throw new ConflitoException("Já existe um livro com o ISBN " + livro.getIsbn() + ".");
        }
        Livro novo = new Livro();
        copiarDados(novo, livro);
        return livroRepository.save(novo);
    }

    @Transactional
    public Livro update(Long id, Livro obj) {
        validar(obj);
        Livro entity = findById(id);
        if (temIsbn(obj) && livroRepository.existsByIsbnAndIdNot(obj.getIsbn(), id)) {
            throw new ConflitoException("Já existe outro livro com o ISBN " + obj.getIsbn() + ".");
        }
        copiarDados(entity, obj);
        return livroRepository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        livroRepository.deleteById(id);
    }

    @Transactional
    public Livro emprestar(Long id) {
        Livro livro = findById(id);
        if (!livro.isDisponivel()) {
            throw new ConflitoException("O livro já está emprestado.");
        }
        livro.setDisponivel(false);
        return livroRepository.save(livro);
    }

    @Transactional
    public Livro devolver(Long id) {
        Livro livro = findById(id);
        if (livro.isDisponivel()) {
            throw new ConflitoException("O livro já está disponível.");
        }
        livro.setDisponivel(true);
        return livroRepository.save(livro);
    }

    private void copiarDados(Livro destino, Livro origem) {
        Autor autor = autorRepository.findById((long) origem.getAutorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Autor não encontrado. Id: " + origem.getAutorId()));

        destino.setTitulo(origem.getTitulo().trim());
        destino.setIsbn(temIsbn(origem) ? origem.getIsbn().trim() : null);
        destino.setAno(origem.getAno());
        destino.setDisponivel(origem.isDisponivel());
        destino.setAutorId(autor.getId().intValue());
        destino.setAutorNome(autor.getNome());
    }

    private boolean temIsbn(Livro livro) {
        return livro.getIsbn() != null && !livro.getIsbn().isBlank();
    }

    private void validar(Livro livro) {
        if (livro.getTitulo() == null || livro.getTitulo().isBlank()) {
            throw new RequisicaoInvalidaException("O título do livro é obrigatório.");
        }
        if (livro.getAno() <= 0) {
            throw new RequisicaoInvalidaException("O ano de publicação deve ser maior que zero.");
        }
    }

}
