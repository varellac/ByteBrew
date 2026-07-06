package br.edu.cafeteria.app;

import br.edu.cafeteria.modelo.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por gerenciar as operações CRUD de Produtos e Clientes
 * Demonstra encapsulamento e centralização de regras de negócio
 */
public class GerenciadorCafeteria {
    private List<Produto> produtos;
    private List<Cliente> clientes;

    public GerenciadorCafeteria() {
        this.produtos = new ArrayList<>();
        this.clientes = new ArrayList<>();
    }

    // ===== OPERAÇÕES CRUD DE PRODUTOS =====

    // CREATE
    public void adicionarProduto(Produto produto) {
        // Validar se já existe produto com mesmo código
        if (buscarProdutoPorCodigo(produto.getCodigo()) != null) {
            throw new IllegalArgumentException("Produto com código " + produto.getCodigo() + " já existe!");
        }
        produtos.add(produto);
    }

    // READ - Buscar por código
    public Produto buscarProdutoPorCodigo(String codigo) {
        for (Produto p : produtos) {
            if (p.getCodigo().equals(codigo)) {
                return p;
            }
        }
        return null;
    }

    // READ - Buscar por nome (parcial)
    public List<Produto> buscarProdutosPorNome(String nome) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.getNome().toLowerCase().contains(nome.toLowerCase())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    // READ - Listar todos
    public List<Produto> listarTodosProdutos() {
        return new ArrayList<>(produtos);
    }

    // UPDATE
    public void atualizarProduto(String codigo, String novoNome, double novoPreco, int novaQuantidade) {
        Produto produto = buscarProdutoPorCodigo(codigo);
        if (produto == null) {
            throw new IllegalArgumentException("Produto com código " + codigo + " não encontrado!");
        }
        if (novoNome != null && !novoNome.isEmpty()) {
            produto.setNome(novoNome);
        }
        if (novoPreco > 0) {
            produto.setPrecoBase(novoPreco);
        }
        if (novaQuantidade >= 0) {
            produto.setQuantidadeEstoque(novaQuantidade);
        }
    }

    // DELETE
    public boolean removerProduto(String codigo) {
        Produto produto = buscarProdutoPorCodigo(codigo);
        if (produto != null) {
            return produtos.remove(produto);
        }
        return false;
    }

    // ===== OPERAÇÕES CRUD DE CLIENTES =====

    // CREATE
    public void adicionarCliente(Cliente cliente) {
        // Validar se já existe cliente com mesmo CPF
        if (buscarClientePorCPF(cliente.getCpf()) != null) {
            throw new IllegalArgumentException("Cliente com CPF " + cliente.getCpf() + " já está cadastrado!");
        }
        clientes.add(cliente);
    }

    // READ - Buscar por CPF
    public Cliente buscarClientePorCPF(String cpf) {
        for (Cliente c : clientes) {
            if (c.getCpf().equals(cpf)) {
                return c;
            }
        }
        return null;
    }

    // READ - Buscar por nome (parcial)
    public List<Cliente> buscarClientesPorNome(String nome) {
        List<Cliente> resultado = new ArrayList<>();
        for (Cliente c : clientes) {
            if (c.getNome().toLowerCase().contains(nome.toLowerCase())) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    // READ - Listar todos
    public List<Cliente> listarTodosClientes() {
        return new ArrayList<>(clientes);
    }

    // UPDATE - Nome
    public void atualizarNomeCliente(String cpf, String novoNome) {
        Cliente cliente = buscarClientePorCPF(cpf);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente com CPF " + cpf + " não encontrado!");
        }
        if (novoNome != null && !novoNome.isEmpty()) {
            cliente.setNome(novoNome);
        }
    }

    // DELETE
    public boolean removerCliente(String cpf) {
        Cliente cliente = buscarClientePorCPF(cpf);
        if (cliente != null) {
            return clientes.remove(cliente);
        }
        return false;
    }
}
