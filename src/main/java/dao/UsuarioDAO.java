package dao;

import db.Conexao;
import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public int contar() throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios";
        try (Connection c = Conexao.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public void inserir(String nome, String login, String senha) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, login, senha) VALUES (?, ?, ?)";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, login);
            ps.setString(3, senha);
            ps.executeUpdate();
        }
    }

    public Usuario autenticar(String login, String senha) throws SQLException {
        String sql = "SELECT id, nome, login FROM usuarios WHERE login = ? AND senha = ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt("id"), rs.getString("nome"), rs.getString("login"));
                }
            }
        }
        return null;
    }

    public List<Usuario> listar() throws SQLException {
        String sql = "SELECT id, nome, login FROM usuarios ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = Conexao.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Usuario(rs.getInt("id"), rs.getString("nome"), rs.getString("login")));
            }
        }
        return lista;
    }

    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

}
