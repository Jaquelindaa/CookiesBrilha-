package model;

public class Cliente {

    private int id;
    private String nome;
    private String cpf;
    private String telefone;
    private int pontosAcumulados;

    public Cliente() {
    }

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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getPontosAcumulados() {
        return pontosAcumulados;
    }

    public void setPontosAcumulados(int pontosAcumulados) {
        this.pontosAcumulados = pontosAcumulados;
    }
}
