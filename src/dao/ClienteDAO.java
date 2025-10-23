package dao;

import model.Cliente;
import util.ConectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteDAO {

    public void save(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nome, cpf, telefone, pontos_acumulados) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getTelefone());
            stmt.setInt(4, cliente.getPontosAcumulados());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
            }
        }
    }

    public Cliente findByCpf(String cpf) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE cpf = ?";
        try (Connection conn = ConectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCliente(rs);
                }
                return null;
            }
        }
    }

    public Cliente findById(int id) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        try (Connection conn = ConectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente();
                    c.setId(rs.getInt("id"));
                    c.setNome(rs.getString("nome"));
                    c.setCpf(rs.getString("cpf"));
                    c.setTelefone(rs.getString("telefone"));
                    c.setPontosAcumulados(rs.getInt("pontos_acumulados"));
                    return c;
                }
            }
        }
        return null; // não encontrou
    }

    public void update(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, pontos_acumulados = ? WHERE id = ?";
        try (Connection conn = ConectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.setInt(3, cliente.getPontosAcumulados());
            stmt.setInt(4, cliente.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException, Exception {
        String sqlDeleteCliente = "DELETE FROM cliente WHERE id = ?";
        try (Connection conn = ConectionFactory.getConnection(); PreparedStatement stmtCliente = conn.prepareStatement(sqlDeleteCliente)) {

            stmtCliente.setInt(1, id);
            int rowsAffected = stmtCliente.executeUpdate();

            if (rowsAffected == 0) {
                throw new Exception("Exclusão falhou, nenhum cliente encontrado com o ID: " + id);
            }

        } catch (SQLException e) {

            // O código '23503' é padrão para "foreign_key_violation" no PostgreSQL
            if ("23503".equals(e.getSQLState()) || e.getMessage().contains("venda_cliente_id_fkey")) {
                throw new Exception("Este cliente não pode ser excluído pois possui vendas em seu histórico.");
            }
            
            throw new Exception("Ocorreu um erro no banco de dados ao tentar excluir o cliente.");
        } catch (Exception ex) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Cliente> findAll() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente ORDER BY nome";
        try (Connection conn = ConectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                c.setCpf(rs.getString("cpf"));
                c.setTelefone(rs.getString("telefone"));
                c.setPontosAcumulados(rs.getInt("pontos_acumulados"));
                clientes.add(c);
            }
        }
        return clientes;
    }

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));
        c.setTelefone(rs.getString("telefone"));
        c.setPontosAcumulados(rs.getInt("pontos_acumulados"));
        return c;
    }
}
