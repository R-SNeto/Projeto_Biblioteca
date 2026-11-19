package com.github.r_sneto.BibliotecaSpring.repository;

import com.github.r_sneto.BibliotecaSpring.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findByTituloContainingIgnoreCase(String titulo);

    List<Livro> findByAutorId(int autorId);

    List<Livro> findByDisponivel(boolean disponivel);

    boolean existsByAutorId(int autorId);

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

}
