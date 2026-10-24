package dao;

import db.Conexao;
import model.Livro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    private static final String SELECT_BASE =
            "SELECT l.id, l.titulo, l.isbn, l.ano_publicacao, l.disponivel, "
                    + "       a.id AS autor_id, a.nome AS autor_nome "
                    + "  FROM livros l "
                    + "  JOIN autores a ON a.id = l.autor_id ";

    public void inserir(String titulo, String isbn, int ano, boolean disponivel, int autorId) throws SQLException {
        String sql = "INSERT INTO livros (titulo, isbn, ano_publicacao, disponivel, autor_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setString(2, (isbn == null || isbn.isBlank()) ? null : isbn);
            ps.setInt(3, ano);
            ps.setBoolean(4, disponivel);
            ps.setInt(5, autorId);
            ps.executeUpdate();
        }
    }

    public List<Livro> listar() throws SQLException {
        return filtrar(null, null, null, null, null);
    }

    public Livro buscarPorId(int id) throws SQLException {
        String sql = SELECT_BASE + "WHERE l.id = ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public List<Livro> filtrar(String titulo, String autor, Integer anoMin, Integer anoMax,
                               Boolean disponivel) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_BASE).append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();

        if (titulo != null && !titulo.isBlank()) {
            sql.append("AND l.titulo ILIKE ? ");
            params.add("%" + titulo + "%");
        }
        if (autor != null && !autor.isBlank()) {
            sql.append("AND a.nome ILIKE ? ");
            params.add("%" + autor + "%");
        }
        if (anoMin != null) {
            sql.append("AND l.ano_publicacao >= ? ");
            params.add(anoMin);
        }
        if (anoMax != null) {
            sql.append("AND l.ano_publicacao <= ? ");
            params.add(anoMax);
        }
        if (disponivel != null) {
            sql.append("AND l.disponivel = ? ");
            params.add(disponivel);
        }
        sql.append("ORDER BY l.titulo");

        List<Livro> lista = new ArrayList<>();
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public boolean atualizar(int id, String titulo, String isbn, int ano, boolean disponivel,
                             int autorId) throws SQLException {
        String sql = "UPDATE livros SET titulo = ?, isbn = ?, ano_publicacao = ?, disponivel = ?, autor_id = ? "
                + "WHERE id = ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setString(2, (isbn == null || isbn.isBlank()) ? null : isbn);
            ps.setInt(3, ano);
            ps.setBoolean(4, disponivel);
            ps.setInt(5, autorId);
            ps.setInt(6, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean alterarDisponibilidade(int id, boolean disponivel) throws SQLException {
        String sql = "UPDATE livros SET disponivel = ? WHERE id = ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, disponivel);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM livros WHERE id = ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Livro mapear(ResultSet rs) throws SQLException {
        return new Livro(rs.getInt("id"), rs.getString("titulo"), rs.getString("isbn"),
                rs.getInt("ano_publicacao"), rs.getBoolean("disponivel"),
                rs.getInt("autor_id"), rs.getString("autor_nome"));
    }

}
