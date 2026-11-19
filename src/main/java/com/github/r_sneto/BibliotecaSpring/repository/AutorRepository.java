package com.github.r_sneto.BibliotecaSpring.repository;

import com.github.r_sneto.BibliotecaSpring.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    List<Autor> findByNomeContainingIgnoreCase(String nome);
}
