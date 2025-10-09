package model;

public class Produto {

    private int id;
    private String nome;
    private double preco;
    private String tipo;
    private int estoque;
    private int custoEmPontos;

    public Produto() {
    }

    // Construtor para criar um novo produto (sem ID ainda)
    public Produto(String nome, double preco, String tipo, int estoque, int custoEmPontos) {
        this.nome = nome;
        this.preco = preco;
        this.tipo = tipo;
        this.estoque = estoque;
        this.custoEmPontos = custoEmPontos;
    }

    // Construtor para criar um produto a partir do banco de dados (com ID)
    public Produto(int id, String nome, double preco, String tipo, int estoque, int custoEmPontos) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.tipo = tipo;
        this.estoque = estoque;
        this.custoEmPontos = custoEmPontos;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public int getCustoEmPontos() {
        return custoEmPontos;
    }

    public void setCustoEmPontos(int custoEmPontos) {
        this.custoEmPontos = custoEmPontos;
    }

    @Override
    public String toString() {
        return this.nome + " (R$ " + String.format("%.2f", this.preco) + ")";
    }

}
