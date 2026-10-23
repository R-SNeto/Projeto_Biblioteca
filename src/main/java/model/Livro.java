package model;

public record Livro(int id, String titulo, String isbn, int ano, boolean disponivel,
                    int autorId, String autorNome) {
}
