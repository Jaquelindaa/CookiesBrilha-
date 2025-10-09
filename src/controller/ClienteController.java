package controller;

import dao.ClienteDAO;
import model.Cliente;

import java.sql.SQLException;
import java.util.List;

public class ClienteController {

    private final ClienteDAO dao;

    public ClienteController() {
        this.dao = new ClienteDAO();
    }

    public void salvarCliente(String nome, String cpf, String telefone) throws Exception {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }

        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos.");
        }
        if (!cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF deve conter apenas números.");
        }

        if (telefone != null && !telefone.trim().isEmpty()) {
            String telNumeros = telefone.replaceAll("\\D", "");
            if (telNumeros.length() < 10 || telNumeros.length() > 11) {
                throw new IllegalArgumentException("Telefone inválido. Deve ter 10 ou 11 dígitos.");
            }
        }

        Cliente cliente = new Cliente(nome, cpf, telefone, 0);

        try {
            dao.save(cliente);
        } catch (SQLException e) {
            throw new Exception("Erro ao salvar cliente: " + e.getMessage(), e);
        }
    }

    public void atualizarCliente(int id, String nome, String telefone, int pontosAcumulados) throws Exception {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }

        Cliente cliente = new Cliente(id, nome, null, telefone, pontosAcumulados);
        try {
            dao.update(cliente);
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    public void deletarCliente(int id) throws Exception {
        try {
            dao.delete(id);
        } catch (SQLException e) {
            throw new Exception("Erro ao deletar cliente: " + e.getMessage(), e);
        }
    }

    public List<Cliente> buscarTodos() throws SQLException {
        return dao.findAll();
    }

    public Cliente buscarPorId(int id) throws Exception {
        try {
            return dao.findById(id);
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar cliente: " + e.getMessage(), e);
        }
    }

    public void adicionarPontos(int clienteId, int pontos) throws Exception {
        try {
            Cliente cliente = dao.findById(clienteId);
            if (cliente == null) {
                throw new Exception("Cliente não encontrado.");
            }
            int novosPontos = cliente.getPontosAcumulados() + pontos;
            cliente.setPontosAcumulados(novosPontos);
            dao.update(cliente);
        } catch (SQLException e) {
            throw new Exception("Erro ao adicionar pontos: " + e.getMessage(), e);
        }
    }
}
