package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import model.ItemVenda;
import model.Venda;
import util.ConectionFactory;

public class VendaDAO {

    public void save(Venda venda) throws SQLException {
        String sqlVenda = "INSERT INTO venda (id_cliente, data_venda, valor_total, pontos_gerados, tipo_transacao) VALUES (?, ?, ?, ?, ?)";
        String sqlItemVenda = "INSERT INTO item_venda (id_venda, id_produto, quantidade, preco_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtVenda = null;
        PreparedStatement stmtItem = null;

        try {
            conn = ConectionFactory.getConnection();
            // Desabilita o auto-commit para controlar a transação manualmente
            conn.setAutoCommit(false);

            // 1. Inserir a Venda
            stmtVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS);
            if (venda.getCliente() != null) {
                stmtVenda.setInt(1, venda.getCliente().getId());
            } else {
                stmtVenda.setNull(1, java.sql.Types.INTEGER);
            }
            stmtVenda.setTimestamp(2, Timestamp.valueOf(venda.getDataVenda()));
            stmtVenda.setDouble(3, venda.getValorTotal());
            stmtVenda.setInt(4, venda.getPontosGerados());
            stmtVenda.setString(5, venda.getTipoTransacao());
            stmtVenda.executeUpdate();

            // 2. Obter o ID da Venda gerado
            try (ResultSet rs = stmtVenda.getGeneratedKeys()) {
                if (rs.next()) {
                    venda.setId(rs.getInt(1));
                }
            }

            // 3. Inserir os Itens da Venda
            if (venda.getItens() != null && !venda.getItens().isEmpty()) {
                stmtItem = conn.prepareStatement(sqlItemVenda);
                for (ItemVenda item : venda.getItens()) {
                    stmtItem.setInt(1, venda.getId());
                    stmtItem.setInt(2, item.getProduto().getId());
                    stmtItem.setInt(3, item.getQuantidade());
                    stmtItem.setDouble(4, item.getPrecoUnitario());
                    stmtItem.setDouble(5, item.getSubtotal());
                    stmtItem.addBatch(); // Adiciona a instrução em um lote
                }
                stmtItem.executeBatch(); // Executa todas as instruções do lote
            }
            
            // Se tudo correu bem, confirma a transação
            conn.commit();

        } catch (SQLException e) {
            // Se ocorrer um erro, desfaz a transação
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Logar o erro do rollback
                }
            }
            throw new SQLException("Erro ao salvar a venda: " + e.getMessage(), e);

        } finally {
            // Fecha os recursos e restaura o auto-commit
            if (stmtVenda != null) stmtVenda.close();
            if (stmtItem != null) stmtItem.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}
