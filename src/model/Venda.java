package model;

import java.util.List;
import java.time.LocalDateTime;

public class Venda {

    private int id;
    private LocalDateTime dataVenda;
    private Cliente cliente;
    private double valorTotal;
    private int pontosGerados;
    private String tipoTransacao;
    private List<ItemVenda> itens;

    public Venda(LocalDateTime dataVenda, Cliente cliente, double valorTotal, int pontosGerados, String tipoTransacao, List<ItemVenda> itens) {
        this.dataVenda = dataVenda;
        this.cliente = cliente;
        this.valorTotal = valorTotal;
        this.pontosGerados = pontosGerados;
        this.tipoTransacao = tipoTransacao;
        this.itens = itens;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public int getPontosGerados() {
        return pontosGerados;
    }

    public void setPontosGerados(int pontosGerados) {
        this.pontosGerados = pontosGerados;
    }

    public String getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(String tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }

    public void calcularValorTotalEPontos() {
        double total = 0.0;

        if (itens != null) {
            for (ItemVenda item : itens) {
                total += item.getPrecoUnitario() * item.getQuantidade();
            }
        }

        this.valorTotal = total;
        this.pontosGerados = (int) Math.floor(total / 10.0);
    }
}
