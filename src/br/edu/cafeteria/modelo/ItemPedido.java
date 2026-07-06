package br.edu.cafeteria.modelo;

public class ItemPedido {
    private Produto produto;
    private int quantidade;

    public ItemPedido(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getValorTotal() {
        // Polimorfismo por Coerção: conversão implícita/explícita de int (quantidade) para double
        return produto.getPrecoBase() * (double) quantidade;
    }
}
