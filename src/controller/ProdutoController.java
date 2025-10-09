package controller;

import dao.ProdutoDAO;
import java.sql.SQLException;
import java.util.List;
import model.Produto;

public class ProdutoController {

    private final ProdutoDAO dao;

    public ProdutoController() {
        this.dao = new ProdutoDAO();
    }

    public void salvarProduto(String nome, double preco, String tipo, int estoque, int custoEmPontos) throws Exception {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório.");
        }
        if (preco <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero.");
        }
        if (estoque < 0) {
            throw new IllegalArgumentException("Estoque não pode ser negativo.");
        }

        Produto produto = new Produto(nome, preco, tipo, estoque, custoEmPontos);
        try {
            dao.save(produto);
        } catch (SQLException e) {
            throw new Exception("Erro ao salvar produto: " + e.getMessage(), e);
        }
    }

    public void atualizarProduto(int id, String nome, double preco, String tipo, int estoque, int custoEmPontos) throws Exception {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório.");
        }
        // Validações adicionais aqui...

        Produto produto = new Produto(id, nome, preco, tipo, estoque, custoEmPontos);
        try {
            dao.update(produto);
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    public void deletarProduto(int id) throws Exception {
        try {
            dao.delete(id);
        } catch (SQLException e) {
            throw new Exception("Erro ao deletar produto: " + e.getMessage(), e);
        }
    }

    public List<Produto> buscarTodos() throws SQLException {
        return dao.findAll();
    }
}
