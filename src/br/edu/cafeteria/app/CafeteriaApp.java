package br.edu.cafeteria.app;

import br.edu.cafeteria.excecao.EstoqueInsuficienteException;
import br.edu.cafeteria.excecao.PontosInsuficientesException;
import br.edu.cafeteria.modelo.Bebida;
import br.edu.cafeteria.modelo.Cliente;
import br.edu.cafeteria.modelo.ClienteStandard;
import br.edu.cafeteria.modelo.ClienteVIP;
import br.edu.cafeteria.modelo.Comida;
import br.edu.cafeteria.modelo.Pedido;
import br.edu.cafeteria.modelo.Produto;
import br.edu.cafeteria.servico.PromocaoEventoGeek;
import br.edu.cafeteria.servico.Promocional;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CafeteriaApp extends JFrame {
    private final List<Produto> cardapio = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private Pedido pedidoAtual;

    private JTable menuTable;
    private JTable pedidoTable;
    private JTextArea logArea;
    private JLabel totalLabel;
    private JComboBox<String> clienteCombo;
    private JSpinner quantidadeSpinner;
    private JButton pagarComXPButton;
    private JButton aplicarPromocaoButton;
    private JButton finalizarPedidoButton;
    private JButton novoPedidoButton;
    private JLabel pedidoLabel;

    public CafeteriaApp() {
        super("Byte & Brew - Cafeteria Responsiva");
        inicializarDados();
        inicializarComponentes();
        configurarJanela();
        criarNovoPedido();
    }

    private void inicializarDados() {
        cardapio.add(new Comida("C001", "Lembas Bread", 15.0, 10, 5, true));
        cardapio.add(new Comida("C002", "Portal Cake", 25.0, 5, 10, false));
        cardapio.add(new Bebida("B001", "Poção de Mana", 12.0, 20, "M", 0));
        cardapio.add(new Bebida("B002", "Café do Programador", 18.0, 15, "G", 200));

        clientes.add(new ClienteStandard("Frodo Baggins", "111.222.333-44"));
        clientes.add(new ClienteVIP("Gandalf", "999.888.777-66"));
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(8, 8));

        JPanel topo = criarPainelTopo();
        JPanel centro = criarPainelCentro();
        JPanel rodape = criarPainelRodape();

        add(topo, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private JPanel criarPainelTopo() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Configuração do Pedido"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        painel.add(new JLabel("Cliente:"), c);

        c.gridx = 1;
        clienteCombo = new JComboBox<>(new String[]{"Cliente Casual", "Frodo Baggins (Standard)", "Gandalf (VIP)"});
        painel.add(clienteCombo, c);

        c.gridx = 2;
        painel.add(new JLabel("Quantidade:"), c);

        c.gridx = 3;
        quantidadeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        painel.add(quantidadeSpinner, c);

        c.gridx = 4;
        novoPedidoButton = new JButton("Novo Pedido");
        painel.add(novoPedidoButton, c);

        c.gridx = 5;
        pedidoLabel = new JLabel("Pedido #0");
        painel.add(pedidoLabel, c);

        novoPedidoButton.addActionListener(e -> criarNovoPedido());
        clienteCombo.addActionListener(e -> atualizarBotoes());

        return painel;
    }

    private JPanel criarPainelCentro() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 8, 8));

        painel.add(criarPainelMenu());
        painel.add(criarPainelResumoPedido());

        return painel;
    }

    private JPanel criarPainelMenu() {
        JPanel painel = new JPanel(new BorderLayout(4, 4));
        painel.setBorder(BorderFactory.createTitledBorder("Cardápio"));

        DefaultTableModel cardapioModel = new DefaultTableModel(new Object[]{"Código", "Nome", "Preço", "Estoque"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        menuTable = new JTable(cardapioModel);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuTable.setFillsViewportHeight(true);

        atualizarTabelaCardapio();

        painel.add(new JScrollPane(menuTable), BorderLayout.CENTER);

        JButton adicionarButton = new JButton("Adicionar ao Pedido");
        adicionarButton.addActionListener(e -> adicionarItemAoPedido());

        JPanel botoes = new JPanel(new BorderLayout());
        botoes.add(adicionarButton, BorderLayout.CENTER);
        painel.add(botoes, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelResumoPedido() {
        JPanel painel = new JPanel(new BorderLayout(4, 4));
        painel.setBorder(BorderFactory.createTitledBorder("Resumo do Pedido"));

        DefaultTableModel pedidoModel = new DefaultTableModel(new Object[]{"Código", "Nome", "Quantidade", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pedidoTable = new JTable(pedidoModel);
        pedidoTable.setFillsViewportHeight(true);

        painel.add(new JScrollPane(pedidoTable), BorderLayout.CENTER);

        JPanel opcoes = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        aplicarPromocaoButton = new JButton("Aplicar Promoção Geek");
        c.gridx = 0;
        c.gridy = 0;
        opcoes.add(aplicarPromocaoButton, c);

        pagarComXPButton = new JButton("Pagar com XP (VIP)");
        c.gridx = 1;
        opcoes.add(pagarComXPButton, c);

        finalizarPedidoButton = new JButton("Finalizar Pedido");
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        opcoes.add(finalizarPedidoButton, c);

        totalLabel = new JLabel("Total: R$ 0.00");
        c.gridy = 2;
        opcoes.add(totalLabel, c);

        painel.add(opcoes, BorderLayout.SOUTH);

        aplicarPromocaoButton.addActionListener(e -> aplicarPromocao());
        pagarComXPButton.addActionListener(e -> pagarComXP());
        finalizarPedidoButton.addActionListener(e -> finalizarPedido());

        return painel;
    }

    private JPanel criarPainelRodape() {
        JPanel painel = new JPanel(new BorderLayout(4, 4));
        painel.setBorder(BorderFactory.createTitledBorder("Registro de Ações"));

        logArea = new JTextArea(8, 20);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        painel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        return painel;
    }

    private void configurarJanela() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 540));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void criarNovoPedido() {
        Cliente clienteSelecionado = selecionarCliente();
        if (clienteSelecionado == null) {
            pedidoAtual = new Pedido();
        } else {
            pedidoAtual = new Pedido(clienteSelecionado);
        }

        pedidoLabel.setText("Pedido #" + pedidoAtual.getNumeroSequencial());
        limparTabelaPedido();
        atualizarTotal();
        atualizarBotoes();
        registrarLog("Novo pedido iniciado." + (clienteSelecionado == null ? " Cliente casual." : " Cliente: " + clienteSelecionado.getNome()));
    }

    private Cliente selecionarCliente() {
        int indice = clienteCombo.getSelectedIndex();
        if (indice == 1) {
            return clientes.get(0);
        } else if (indice == 2) {
            return clientes.get(1);
        }
        return null;
    }

    private void atualizarBotoes() {
        boolean pedidoTemItens = pedidoAtual != null && !pedidoAtual.getItens().isEmpty();
        boolean clienteVIP = selecionarCliente() instanceof ClienteVIP;
        pagarComXPButton.setEnabled(pedidoTemItens && clienteVIP);
        aplicarPromocaoButton.setEnabled(pedidoTemItens);
        finalizarPedidoButton.setEnabled(pedidoTemItens);
    }

    private void atualizarTabelaCardapio() {
        DefaultTableModel modelo = (DefaultTableModel) menuTable.getModel();
        modelo.setRowCount(0);
        for (Produto produto : cardapio) {
            modelo.addRow(new Object[]{produto.getCodigo(), produto.getNome(), String.format("R$ %.2f", produto.getPrecoBase()), produto.getQuantidadeEstoque()});
        }
    }

    private void limparTabelaPedido() {
        DefaultTableModel modelo = (DefaultTableModel) pedidoTable.getModel();
        modelo.setRowCount(0);
    }

    private void adicionarItemAoPedido() {
        int linhaSelecionada = menuTable.getSelectedRow();
        if (linhaSelecionada < 0) {
            registrarLog("Selecione um item do cardápio antes de adicionar.");
            return;
        }

        Produto produto = cardapio.get(linhaSelecionada);
        int quantidade = (Integer) quantidadeSpinner.getValue();

        try {
            pedidoAtual.adicionarItem(produto, quantidade);
            registrarLog("Adicionado ao pedido: " + produto.getNome() + " x" + quantidade + ".");
            atualizarTabelaPedido();
            atualizarTotal();
            atualizarBotoes();
        } catch (EstoqueInsuficienteException ex) {
            registrarLog("Erro: " + ex.getMessage());
        }
    }

    private void atualizarTabelaPedido() {
        DefaultTableModel modelo = (DefaultTableModel) pedidoTable.getModel();
        modelo.setRowCount(0);
        for (var item : pedidoAtual.getItens()) {
            modelo.addRow(new Object[]{
                item.getProduto().getCodigo(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                String.format("R$ %.2f", item.getValorTotal())
            });
        }
    }

    private void atualizarTotal() {
        double total = pedidoAtual.calcularValorTotal();
        totalLabel.setText(String.format("Total: R$ %.2f", total));
    }

    private void aplicarPromocao() {
        if (pedidoAtual.getItens().isEmpty()) {
            registrarLog("Adicione itens antes de aplicar a promoção.");
            return;
        }

        Promocional promocao = new PromocaoEventoGeek();
        double desconto = promocao.aplicarDesconto(pedidoAtual);
        double bruto = pedidoAtual.calcularValorTotal();
        double liquido = bruto - desconto;

        registrarLog(String.format("Promoção aplicada. Desconto: R$ %.2f. Total final: R$ %.2f.", desconto, liquido));
    }

    private void pagarComXP() {
        if (!(selecionarCliente() instanceof ClienteVIP)) {
            registrarLog("Pagamento com XP só está disponível para clientes VIP.");
            return;
        }

        ClienteVIP clienteVIP = (ClienteVIP) selecionarCliente();
        double total = pedidoAtual.calcularValorTotal();

        try {
            clienteVIP.pagarComXP(total);
            registrarLog(String.format("Pagamento realizado com XP. Novo saldo: R$ %.2f XP.", clienteVIP.getSaldoXP()));
            finalizarPedido();
        } catch (PontosInsuficientesException ex) {
            registrarLog("Erro: " + ex.getMessage());
        }
    }

    private void finalizarPedido() {
        if (pedidoAtual.getItens().isEmpty()) {
            registrarLog("Não há itens para finalizar o pedido.");
            return;
        }

        pedidoAtual.finalizarPedido();
        atualizarTabelaCardapio();
        registrarLog("Pedido finalizado com sucesso.");
        if (pedidoAtual.getCliente() != null) {
            registrarLog(String.format("XP do cliente agora: R$ %.2f.", pedidoAtual.getCliente().getSaldoXP()));
        }
        criarNovoPedido();
    }

    private void registrarLog(String mensagem) {
        logArea.append(mensagem + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
