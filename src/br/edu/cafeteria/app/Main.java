package br.edu.cafeteria.app;

import javax.swing.SwingUtilities;
import br.edu.cafeteria.modelo.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // ===== DEMONSTRAÇÃO DO CRUD DE PRODUTOS =====
        System.out.println("\n========== CRUD DE PRODUTOS ==========\n");
        
        GerenciadorCafeteria gerenciador = new GerenciadorCafeteria();

        // CREATE - Adicionando produtos
        System.out.println("1. CREATE - Adicionando produtos ao cardápio:");
        Comida lembas = new Comida("C001", "Lembas Bread", 15.0, 10, 5, true);
        Comida portal = new Comida("C002", "Portal Cake", 25.0, 5, 10, false);
        Bebida pocao = new Bebida("B001", "Poção de Mana", 12.0, 20, "M", 0);
        Bebida cafe = new Bebida("B002", "Café do Programador", 18.0, 15, "G", 200);

        gerenciador.adicionarProduto(lembas);
        gerenciador.adicionarProduto(portal);
        gerenciador.adicionarProduto(pocao);
        gerenciador.adicionarProduto(cafe);
        System.out.println("   ✓ 4 produtos adicionados com sucesso.\n");

        // READ - Buscando produtos
        System.out.println("2. READ - Buscando produtos:");
        System.out.println("   Buscando produto com código 'B001':");
        Produto encontrado = gerenciador.buscarProdutoPorCodigo("B001");
        if (encontrado != null) {
            System.out.println("   ✓ Encontrado: " + encontrado.getNome() + " - R$ " + encontrado.getPrecoBase());
        }

        System.out.println("\n   Buscando produtos com nome contendo 'Café':");
        List<Produto> encontrados = gerenciador.buscarProdutosPorNome("Café");
        for (Produto p : encontrados) {
            System.out.println("   ✓ " + p.getNome() + " (" + p.getCodigo() + ")");
        }

        System.out.println("\n   Listando todos os produtos:");
        List<Produto> todosProdutos = gerenciador.listarTodosProdutos();
        for (Produto p : todosProdutos) {
            System.out.println("   ✓ " + p.getCodigo() + " - " + p.getNome() + " - R$ " + p.getPrecoBase() + " - Estoque: " + p.getQuantidadeEstoque());
        }

        // UPDATE - Atualizando um produto
        System.out.println("\n3. UPDATE - Atualizando preço de um produto:");
        System.out.println("   Preço anterior de 'Lembas Bread': R$ " + lembas.getPrecoBase());
        gerenciador.atualizarProduto("C001", null, 16.50, -1);
        System.out.println("   ✓ Novo preço: R$ " + gerenciador.buscarProdutoPorCodigo("C001").getPrecoBase());

        // DELETE - Removendo um produto
        System.out.println("\n4. DELETE - Removendo um produto:");
        System.out.println("   Removendo 'Portal Cake' (C002)...");
        boolean removido = gerenciador.removerProduto("C002");
        if (removido) {
            System.out.println("   ✓ Produto removido com sucesso.");
        }
        System.out.println("   Tentando buscar produto removido:");
        if (gerenciador.buscarProdutoPorCodigo("C002") == null) {
            System.out.println("   ✓ Confirmado: Produto não encontrado.\n");
        }

        // ===== DEMONSTRAÇÃO DO CRUD DE CLIENTES =====
        System.out.println("\n========== CRUD DE CLIENTES ==========\n");

        // CREATE - Adicionando clientes
        System.out.println("1. CREATE - Adicionando clientes ao programa de fidelidade:");
        Cliente frodo = new ClienteStandard("Frodo Baggins", "111.222.333-44");
        Cliente gandalf = new ClienteVIP("Gandalf", "999.888.777-66");

        gerenciador.adicionarCliente(frodo);
        gerenciador.adicionarCliente(gandalf);
        System.out.println("   ✓ 2 clientes cadastrados com sucesso.\n");

        // READ - Buscando clientes
        System.out.println("2. READ - Buscando clientes:");
        System.out.println("   Buscando cliente com CPF '111.222.333-44':");
        Cliente clienteEncontrado = gerenciador.buscarClientePorCPF("111.222.333-44");
        if (clienteEncontrado != null) {
            System.out.println("   ✓ Encontrado: " + clienteEncontrado.getNome() + " (Tipo: " + 
                (clienteEncontrado instanceof ClienteVIP ? "VIP" : "Standard") + ")");
        }

        System.out.println("\n   Buscando clientes com nome contendo 'alf':");
        List<Cliente> clientesEncontrados = gerenciador.buscarClientesPorNome("alf");
        for (Cliente c : clientesEncontrados) {
            System.out.println("   ✓ " + c.getNome() + " - CPF: " + c.getCpf());
        }

        System.out.println("\n   Listando todos os clientes:");
        List<Cliente> todosClientes = gerenciador.listarTodosClientes();
        for (Cliente c : todosClientes) {
            System.out.println("   ✓ " + c.getNome() + " - CPF: " + c.getCpf() + " - XP: " + c.getSaldoXP());
        }

        // UPDATE - Atualizando um cliente
        System.out.println("\n3. UPDATE - Atualizando nome de um cliente:");
        System.out.println("   Nome anterior: " + frodo.getNome());
        gerenciador.atualizarNomeCliente("111.222.333-44", "Frodo Baggins (Ringbearer)");
        System.out.println("   ✓ Novo nome: " + gerenciador.buscarClientePorCPF("111.222.333-44").getNome());

        // DELETE - Removendo um cliente
        System.out.println("\n4. DELETE - Removendo um cliente:");
        System.out.println("   Removendo cliente com CPF '999.888.777-66'...");
        boolean removidoCliente = gerenciador.removerCliente("999.888.777-66");
        if (removidoCliente) {
            System.out.println("   ✓ Cliente removido com sucesso.");
        }
        System.out.println("   Tentando buscar cliente removido:");
        if (gerenciador.buscarClientePorCPF("999.888.777-66") == null) {
            System.out.println("   ✓ Confirmado: Cliente não encontrado.\n");
        }

        // ===== ABRINDO A GUI DA CAFETERIA =====
        System.out.println("\n========== INICIANDO INTERFACE GRÁFICA ==========\n");
        System.out.println("Abrindo GUI da Byte & Brew...\n");
        
        SwingUtilities.invokeLater(() -> new CafeteriaApp());
    }
}

