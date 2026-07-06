package br.edu.cafeteria.app;

import javax.swing.SwingUtilities;

public class Main {
    
    // Listas simulando o Banco de Dados (CRUD)
    private static List<Produto> repositorioProdutos = new ArrayList<>();
    private static List<Cliente> repositorioClientes = new ArrayList<>();
    private static List<Atendente> repositorioAtendentes = new ArrayList<>();
    private static List<Pedido> repositorioPedidos = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CafeteriaApp());
    }
}

