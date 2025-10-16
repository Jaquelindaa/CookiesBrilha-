package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Cliente;
import model.ItemVenda;
import model.Venda;
import util.ConectionFactory;

public class VendaDAO {

    private final ClienteDAO clienteDAO;

    public VendaDAO() {
        this.clienteDAO = new ClienteDAO();
    }

    public void save(Venda venda) throws SQLException {
        String sqlVenda = "INSERT INTO venda (cliente_id, data_venda, valor_total, pontos_gerados, tipo_transacao) VALUES (?, ?, ?, ?, ?)";
        String sqlItemVenda = "INSERT INTO item_venda (venda_id, produto_id, quantidade, preco_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtVenda = null;
        PreparedStatement stmtItem = null;

        try {
            conn = ConectionFactory.getConnection();
            conn.setAutoCommit(false);

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

            try (ResultSet rs = stmtVenda.getGeneratedKeys()) {
                if (rs.next()) {
                    venda.setId(rs.getInt(1));
                }
            }

            if (venda.getItens() != null && !venda.getItens().isEmpty()) {
                stmtItem = conn.prepareStatement(sqlItemVenda);
                for (ItemVenda item : venda.getItens()) {
                    stmtItem.setInt(1, venda.getId());
                    stmtItem.setInt(2, item.getProduto().getId());
                    stmtItem.setInt(3, item.getQuantidade());
                    stmtItem.setDouble(4, item.getPrecoUnitario());
                    stmtItem.setDouble(5, item.getSubtotal());
                    stmtItem.addBatch();
                }
                stmtItem.executeBatch();
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new SQLException("Erro ao salvar a venda: " + e.getMessage(), e);

        } finally {
            if (stmtVenda != null) {
                stmtVenda.close();
            }
            if (stmtItem != null) {
                stmtItem.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<Venda> findAll() throws SQLException {
        List<Venda> vendas = new ArrayList<>();
        String sql = "SELECT * FROM venda ORDER BY data_venda DESC";
        try (Connection conn = ConectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Venda v = mapResultSetToVenda(rs);
                vendas.add(v);
            }
        }
        return vendas;
    }

    private Venda mapResultSetToVenda(ResultSet rs) throws SQLException {
        int clienteId = rs.getInt("cliente_id");
        Cliente cliente = null;
        
        try {
            if(clienteId > 0){
                cliente = clienteDAO.findById(clienteId);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar cliente para Venda ID " + rs.getInt("id") + ": " + e.getMessage());
        }

        List<ItemVenda> itensVenda = new ArrayList<>();

        return new Venda(
                rs.getTimestamp("data_venda").toLocalDateTime(),
                cliente,
                rs.getDouble("valor_total"),
                rs.getInt("pontos_gerados"),
                rs.getString("tipo_transacao"),
                itensVenda
        ) {
            {
                setId(rs.getInt("id"));
            }
        };
    }
}
