package br.edu.cafeteria.modelo;

public abstract class Produto {
    private String codigo;
    private String nome;
    private double precoBase;
    private int quantidadeEstoque;

    public Produto(String codigo, String nome, double precoBase, int quantidadeEstoque) {
        this.codigo = codigo;
        this.nome = nome;
        this.precoBase = precoBase;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPrecoBase() { return precoBase; }
    public void setPrecoBase(double precoBase) { this.precoBase = precoBase; }

    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int quantidadeEstoque) { 
        this.quantidadeEstoque = quantidadeEstoque < 0 ? 0 : quantidadeEstoque;
    }

    public void diminuirEstoque(int quantidade) {
        this.quantidadeEstoque -= quantidade;
        if (this.quantidadeEstoque < 0) {
            this.quantidadeEstoque = 0;
        }
    }
}
