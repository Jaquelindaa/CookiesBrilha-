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

    // Registrar uma venda
    public void registrarVenda(Cliente cliente, List<ItemVenda> itens) throws Exception {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("A venda deve conter pelo menos 1 item.");
        }

        //Calcular Valor Total da Venda
        double total = itens.stream().mapToDouble(ItemVenda::getSubtotal).sum();

        // Calcular Pontos Gerados
        int pontosGerados = (int) (total / PONTOS_POR_REAL);

        //Criar objeto Venda
        Venda venda = new Venda(LocalDateTime.now(), cliente, total, pontosGerados, "VENDA", itens);

        try {
            // Persistir a Venda e os Itens
            vendaDAO.save(venda);

            // Atualizar Estoque
            for (ItemVenda item : itens) {
                produtoDAO.decrementarEstoque(item.getProduto().getId(), item.getQuantidade());
            }

            // Atualizar Pontos do Cliente (se não for venda anônima)
            if (cliente != null) {
                cliente.setPontosAcumulados(cliente.getPontosAcumulados() + pontosGerados);
                clienteDAO.update(cliente);
            }

        } catch (SQLException e) {
            throw new Exception("Erro ao registrar venda: " + e.getMessage(), e);
        }
    }

    // Resgatar produto com pontos
    public void resgatarProduto(Cliente cliente, Produto produtoResgatavel) throws Exception {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente é obrigatório para resgate.");
        }

        // 1. Validar Estoque
        if (produtoResgatavel.getEstoque() <= 0) {
            throw new Exception("Produto fora de estoque para resgate.");
        }

        // 2. Validar Pontos
        if (cliente.getPontosAcumulados() < produtoResgatavel.getCustoEmPontos()) {
            throw new Exception("Pontos insuficientes para o resgate.");
        }

        try {
            // 3. Atualizar Estoque
            produtoDAO.decrementarEstoque(produtoResgatavel.getId(), 1);

            // 4. Atualizar Pontos do Cliente
            cliente.setPontosAcumulados(cliente.getPontosAcumulados() - produtoResgatavel.getCustoEmPontos());
            clienteDAO.update(cliente);

            // 5. Registrar Transação de Resgate (Venda com valor total 0 e tipo 'RESGATE')
            Venda resgate = new Venda(LocalDateTime.now(), cliente, 0.0, 0, "RESGATE", List.of());
            vendaDAO.save(resgate);

        } catch (SQLException e) {
            throw new Exception("Erro ao realizar resgate: " + e.getMessage(), e);
        }
    }
}
