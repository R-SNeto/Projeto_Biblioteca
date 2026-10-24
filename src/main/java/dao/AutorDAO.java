package dao;

import db.Conexao;
import model.Autor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AutorDAO {
    public void inserir(String nome, String nacionalidade) throws SQLException {
        String sql = "INSERT INTO autores (nome, nacionalidade) VALUES (?, ?)";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, nacionalidade);
            ps.executeUpdate();
        }
    }

    public List<Autor> listar() throws SQLException {
        return consultar("SELECT id, nome, nacionalidade FROM autores ORDER BY nome", null);
    }

    public List<Autor> filtrarPorNome(String nome) throws SQLException {
        return consultar("SELECT id, nome, nacionalidade FROM autores WHERE nome ILIKE ? ORDER BY nome",
                "%" + nome + "%");
    }

    public Autor buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, nacionalidade FROM autores WHERE id = ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public boolean atualizar(int id, String nome, String nacionalidade) throws SQLException {
        String sql = "UPDATE autores SET nome = ?, nacionalidade = ? WHERE id = ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, nacionalidade);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM autores WHERE id = ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<String> relatorioLivrosPorAutor() throws SQLException {
        String sql = "SELECT a.nome AS autor, COUNT(l.id) AS total_livros "
                + "FROM autores a LEFT JOIN livros l ON l.autor_id = a.id "
                + "GROUP BY a.nome ORDER BY total_livros DESC, a.nome";
        List<String> linhas = new ArrayList<>();
        try (Connection c = Conexao.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                linhas.add(String.format("%-35s %d livro(s)", rs.getString("autor"), rs.getInt("total_livros")));
            }
        }
        return linhas;
    }

    private List<Autor> consultar(String sql, String param) throws SQLException {
        List<Autor> lista = new ArrayList<>();
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (param != null) ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Autor mapear(ResultSet rs) throws SQLException {
        return new Autor(rs.getInt("id"), rs.getString("nome"), rs.getString("nacionalidade"));
    }
}
