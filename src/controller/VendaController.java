package controller;

import dao.ClienteDAO;
import dao.ProdutoDAO;
import dao.VendaDAO;
import model.Cliente;
import model.ItemVenda;
import model.Produto;
import model.Venda;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class VendaController {

    private static final int PONTOS_POR_REAL = 10;

    private final VendaDAO vendaDAO;
    private final ProdutoDAO produtoDAO;
    private final ClienteDAO clienteDAO;

    public VendaController() {
        this.vendaDAO = new VendaDAO();
        this.produtoDAO = new ProdutoDAO();
        this.clienteDAO = new ClienteDAO();
    }

    public void registrarVenda(Cliente cliente, List<ItemVenda> itens) throws Exception {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("A venda deve conter pelo menos 1 item.");
        }

        double total = itens.stream().mapToDouble(ItemVenda::getSubtotal).sum();
        int pontosGerados = (int) (total / PONTOS_POR_REAL);

        Venda venda = new Venda(LocalDateTime.now(), cliente, total, pontosGerados, "VENDA", itens);

        try {
            vendaDAO.save(venda);

            for (ItemVenda item : itens) {
                produtoDAO.decrementarEstoque(item.getProduto().getId(), item.getQuantidade());
            }

            if (cliente != null) {
                cliente.setPontosAcumulados(cliente.getPontosAcumulados() + pontosGerados);
                clienteDAO.update(cliente);
            }

        } catch (SQLException e) {
            throw new Exception("Erro ao registrar venda: " + e.getMessage(), e);
        }
    }

    public void resgatarProduto(Cliente cliente, Produto produtoResgatavel) throws Exception {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente é obrigatório para resgate.");
        }

        if (produtoResgatavel.getEstoque() <= 0) {
            throw new Exception("Produto fora de estoque para resgate.");
        }

        if (cliente.getPontosAcumulados() < produtoResgatavel.getCustoEmPontos()) {
            throw new Exception("Pontos insuficientes para o resgate.");
        }
        if (produtoResgatavel.getEstoque() <= 0) {
            throw new Exception("Produto fora de estoque para resgate.");
        }

        if (cliente.getPontosAcumulados() < produtoResgatavel.getCustoEmPontos()) {
            throw new Exception("Pontos insuficientes para resgate. Necessário: "
                    + produtoResgatavel.getCustoEmPontos() + ", Atual: "
                    + cliente.getPontosAcumulados());
        }

        try {
            produtoDAO.decrementarEstoque(produtoResgatavel.getId(), 1);

            int pontosSubtraidos = produtoResgatavel.getCustoEmPontos();
            cliente.setPontosAcumulados(cliente.getPontosAcumulados() - pontosSubtraidos);
            clienteDAO.update(cliente);

            ItemVenda itemResgate = new ItemVenda(produtoResgatavel, 1, 0.0, 0.0);
            Venda resgate = new Venda(LocalDateTime.now(), cliente, 0.0, pontosSubtraidos, "RESGATE", List.of(itemResgate));
            vendaDAO.save(resgate);

        } catch (SQLException e) {
            throw new Exception("Erro ao realizar resgate: " + e.getMessage(), e);
        }
    }

    public List<Venda> buscarTodasTransacoes() throws SQLException {
        return vendaDAO.findAll();
    }

    public int calcularPontos(List<ItemVenda> itens) {
        double total = itens.stream().mapToDouble(ItemVenda::getSubtotal).sum();
        return (int) (total / PONTOS_POR_REAL);
    }
}
