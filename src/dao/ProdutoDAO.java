package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Produto;
import util.ConectionFactory;

public class ProdutoDAO {

    public void save(Produto produto) throws SQLException {
        String sql = "INSERT INTO produto (nome, preco, tipo, estoque, custo_em_pontos) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setString(3, produto.getTipo());
            stmt.setInt(4, produto.getEstoque());
            stmt.setInt(5, produto.getCustoEmPontos());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    produto.setId(rs.getInt(1));
                }
            }
        }
    }

    public void update(Produto produto) throws SQLException {
        String sql = "UPDATE produto SET nome = ?, preco = ?, tipo = ?, estoque = ?, custo_em_pontos = ? WHERE id = ?";
        try (Connection conn = ConectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setString(3, produto.getTipo());
            stmt.setInt(4, produto.getEstoque());
            stmt.setInt(5, produto.getCustoEmPontos());
            stmt.setInt(6, produto.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM produto WHERE id = ?";
        try (Connection conn = ConectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Produto findById(int id) throws SQLException {
        String sql = "SELECT * FROM produto WHERE id = ?";
        try (Connection conn = ConectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return resultSetToProduto(rs);
                }
            }
        }
        return null;
    }

    public List<Produto> findAll() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produto ORDER BY nome";
        try (Connection conn = ConectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                produtos.add(resultSetToProduto(rs));
            }
        }
        return produtos;
    }
    
    public void decrementarEstoque(int idProduto, int quantidade) throws SQLException {
        String sql = "UPDATE produto SET estoque = estoque - ? WHERE id = ?";
        try (Connection conn = ConectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantidade);
            stmt.setInt(2, idProduto);
            stmt.executeUpdate();
        }
    }

    private Produto resultSetToProduto(ResultSet rs) throws SQLException {
        return new Produto(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getDouble("preco"),
                rs.getString("tipo"),
                rs.getInt("estoque"),
                rs.getInt("custo_em_pontos")
        );
    }
}
