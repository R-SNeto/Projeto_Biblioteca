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
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private LivroRepository livroRepository;

    public List<Autor> findAll() {
        return autorRepository.findAll();
    }

    public List<Autor> findByNome(String nome) {
        return autorRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Autor findById(Long id) {
        return autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Autor não encontrado. Id: " + id));
    }

    @Transactional
    public Autor insert(Autor autor) {
        validar(autor);
        return autorRepository.save(new Autor(autor.getNome().trim(), autor.getNacionalidade()));
    }

    @Transactional
    public Autor update(Long id, Autor obj) {
        validar(obj);
        Autor entity = findById(id);
        entity.setNome(obj.getNome().trim());
        entity.setNacionalidade(obj.getNacionalidade());
        Autor salvo = autorRepository.save(entity);

        List<Livro> livros = livroRepository.findByAutorId(id.intValue());
        livros.forEach(l -> l.setAutorNome(salvo.getNome()));
        livroRepository.saveAll(livros);

        return salvo;
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        if (livroRepository.existsByAutorId(id.intValue())) {
            throw new ConflitoException("Não é possível excluir o autor: existem livros vinculados a ele.");
        }
        autorRepository.deleteById(id);
    }

    private void validar(Autor autor) {
        if (autor.getNome() == null || autor.getNome().isBlank()) {
            throw new RequisicaoInvalidaException("O nome do autor é obrigatório.");
        }
    }

}
