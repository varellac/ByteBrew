package br.edu.cafeteria.modelo;

import br.edu.cafeteria.excecao.EstoqueInsuficienteException;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private static int contadorPedidos = 1;
    
    private int numeroSequencial;
    private Atendente atendente;
    private Cliente cliente; // Opcional
    private List<ItemPedido> itens;

    public Pedido(Atendente atendente) {
        this.numeroSequencial = contadorPedidos++;
        this.atendente = atendente;
        this.itens = new ArrayList<>();
    }

    public Pedido(Atendente atendente, Cliente cliente) {
        this(atendente);
        this.cliente = cliente;
    }

    public int getNumeroSequencial() { return numeroSequencial; }
    public Atendente getAtendente() { return atendente; }
    public Cliente getCliente() { return cliente; }
    public List<ItemPedido> getItens() { return itens; }

    // Polimorfismo por Sobrecarga: adicionarItem(Produto)
    public void adicionarItem(Produto p) {
        adicionarItem(p, 1);
    }

    // Polimorfismo por Sobrecarga: adicionarItem(Produto, quantidade)
    public void adicionarItem(Produto p, int quantidade) {
        if (quantidade > p.getQuantidadeEstoque()) {
            throw new EstoqueInsuficienteException("Estoque insuficiente para o produto: " + p.getNome() + ". Solicitado: " + quantidade + ", Disponível: " + p.getQuantidadeEstoque());
        }
        itens.add(new ItemPedido(p, quantidade));
    }

    // Polimorfismo por Inclusão (trata todos os produtos genericamente na lista de itens)
    public double calcularValorTotal() {
        double total = 0.0;
        for (ItemPedido item : itens) {
            total += item.getValorTotal();
        }
        return total;
    }

    // Sobrecarga para permitir finalizar sem desconto
    public void finalizarPedido() {
        finalizarPedido(0.0);
    }

    public void finalizarPedido(double valorDesconto) {
        // Atualiza estoque
        for (ItemPedido item : itens) {
            item.getProduto().diminuirEstoque(item.getQuantidade());
        }
        
        // Se houver cliente, adicionar XP sobre o valor líquido pago (Total - Desconto)
        if (this.cliente != null) {
            double valorVenda = calcularValorTotal() - valorDesconto;
            this.cliente.adicionarXP(valorVenda);
        }
    }
}
