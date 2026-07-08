package br.edu.cafeteria.app;

import br.edu.cafeteria.excecao.EstoqueInsuficienteException;
import br.edu.cafeteria.excecao.PontosInsuficientesException;
import br.edu.cafeteria.modelo.Atendente;
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
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CafeteriaApp extends JFrame {
    private final List<Produto> cardapio = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private Pedido pedidoAtual;
    private Atendente atendente;

    private JTable menuTable;
    private JTable pedidoTable;
    private JTable clienteTable;
    private JTextArea logArea;
    private JLabel totalLabel;
    private JComboBox<String> clienteCombo;
    private DefaultComboBoxModel<String> clienteComboModel;
    private JSpinner quantidadeSpinner;
    private JButton pagarComXPButton;
    private JButton aplicarPromocaoButton;
    private JButton finalizarPedidoButton;
    private JButton novoPedidoButton;
    private JLabel pedidoLabel;

    private JTextField produtoCodigoField;
    private JTextField produtoNomeField;
    private JTextField produtoPrecoField;
    private JTextField produtoEstoqueField;
    private JComboBox<String> produtoTipoCombo;
    private JButton adicionarProdutoButton;
    private JButton atualizarProdutoButton;
    private JButton removerProdutoButton;

    private JTextField clienteNomeField;
    private JTextField clienteCPFField;
    private JComboBox<String> clienteTipoCombo;
    private JButton adicionarClienteButton;
    private JButton atualizarClienteButton;
    private JButton removerClienteButton;

    public CafeteriaApp() {
        super("Byte & Brew - Cafeteria Responsiva");
        inicializarDados();
        inicializarComponentes();
        configurarJanela();
        criarNovoPedido();
    }

    private void inicializarDados() {
        atendente = new Atendente("Bilbo", "A001");
        
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
        clienteComboModel = new DefaultComboBoxModel<>();
        clienteCombo = new JComboBox<>(clienteComboModel);
        atualizarComboClientes();
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
        // Usar JSplitPane vertical para permitir que o usuário redimensione a área
        JPanel painel = new JPanel(new BorderLayout());
        JPanel topo = new JPanel(new GridLayout(1, 2, 8, 8));
        topo.add(criarPainelMenu());
        topo.add(criarPainelResumoPedido());

        JPanel crud = criarPainelCRUD();

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topo, crud);
        split.setResizeWeight(0.6); // dar prioridade ao topo inicialmente
        split.setOneTouchExpandable(true);
        painel.add(split, BorderLayout.CENTER);
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
        menuTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                popularCamposProdutoSelecionado();
            }
        });

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

    private JPanel criarPainelCRUD() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 8, 8));
        painel.setBorder(BorderFactory.createTitledBorder("CRUD de Produtos e Clientes"));
        painel.add(criarPainelCRUDProdutos());
        painel.add(criarPainelCRUDClientes());
        return painel;
    }

    private JPanel criarPainelCRUDProdutos() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Gerenciar Produtos"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        painel.add(new JLabel("Código:"), c);
        c.gridx = 1;
        produtoCodigoField = new JTextField();
        painel.add(produtoCodigoField, c);

        c.gridx = 0;
        c.gridy = 1;
        painel.add(new JLabel("Nome:"), c);
        c.gridx = 1;
        produtoNomeField = new JTextField();
        painel.add(produtoNomeField, c);

        c.gridx = 0;
        c.gridy = 2;
        painel.add(new JLabel("Preço:"), c);
        c.gridx = 1;
        produtoPrecoField = new JTextField();
        painel.add(produtoPrecoField, c);

        c.gridx = 0;
        c.gridy = 3;
        painel.add(new JLabel("Estoque:"), c);
        c.gridx = 1;
        produtoEstoqueField = new JTextField();
        painel.add(produtoEstoqueField, c);

        c.gridx = 0;
        c.gridy = 4;
        painel.add(new JLabel("Tipo:"), c);
        c.gridx = 1;
        produtoTipoCombo = new JComboBox<>(new String[]{"Comida", "Bebida"});
        painel.add(produtoTipoCombo, c);

        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        adicionarProdutoButton = new JButton("Incluir Produto");
        painel.add(adicionarProdutoButton, c);

        c.gridy = 6;
        atualizarProdutoButton = new JButton("Atualizar Produto");
        painel.add(atualizarProdutoButton, c);

        c.gridy = 7;
        removerProdutoButton = new JButton("Remover Produto");
        painel.add(removerProdutoButton, c);

        adicionarProdutoButton.addActionListener(e -> adicionarProduto());
        atualizarProdutoButton.addActionListener(e -> atualizarProduto());
        removerProdutoButton.addActionListener(e -> removerProduto());

        return painel;
    }

    private JPanel criarPainelCRUDClientes() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Gerenciar Clientes"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        painel.add(new JLabel("Nome:"), c);
        c.gridx = 1;
        clienteNomeField = new JTextField();
        painel.add(clienteNomeField, c);

        c.gridx = 0;
        c.gridy = 1;
        painel.add(new JLabel("CPF:"), c);
        c.gridx = 1;
        clienteCPFField = new JTextField();
        painel.add(clienteCPFField, c);

        c.gridx = 0;
        c.gridy = 2;
        painel.add(new JLabel("Tipo:"), c);
        c.gridx = 1;
        clienteTipoCombo = new JComboBox<>(new String[]{"Standard", "VIP"});
        painel.add(clienteTipoCombo, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        adicionarClienteButton = new JButton("Incluir Cliente");
        painel.add(adicionarClienteButton, c);

        c.gridy = 4;
        atualizarClienteButton = new JButton("Atualizar Cliente");
        painel.add(atualizarClienteButton, c);

        c.gridy = 5;
        removerClienteButton = new JButton("Remover Cliente");
        painel.add(removerClienteButton, c);

        adicionarClienteButton.addActionListener(e -> adicionarCliente());
        atualizarClienteButton.addActionListener(e -> atualizarCliente());
        removerClienteButton.addActionListener(e -> removerCliente());

        clienteTable = new JTable(new DefaultTableModel(new Object[]{"Nome", "CPF", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        clienteTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clienteTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                popularCamposClienteSelecionado();
            }
        });

        // tornar a área de clientes maior para melhor usabilidade
        clienteTable.setPreferredScrollableViewportSize(new Dimension(420, 220));
        // aumentar legibilidade: fonte maior e linhas mais altas
        clienteTable.setFont(clienteTable.getFont().deriveFont(14f));
        clienteTable.setRowHeight(26);
        // permitir barra horizontal e ajustar larguras das colunas para destacar o nome
        clienteTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane clienteScroll = new JScrollPane(clienteTable);
        // aumentar preferências para que o painel de clientes ocupe mais espaço
        clienteScroll.setPreferredSize(new Dimension(760, 320));
        clienteScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        javax.swing.table.TableColumnModel colModel = clienteTable.getColumnModel();
        if (colModel.getColumnCount() >= 3) {
            colModel.getColumn(0).setPreferredWidth(420); // Nome maior
            colModel.getColumn(1).setPreferredWidth(200); // CPF
            colModel.getColumn(2).setPreferredWidth(120); // Tipo
        }

        // Renderer para deixar a coluna Nome em negrito
        clienteTable.getColumnModel().getColumn(0).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(c.getFont().deriveFont(Font.BOLD, 14f));
                return c;
            }
        });

        // Double-click no cabeçalho para auto-ajustar colunas
        clienteTable.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // auto-ajusta cada coluna para o conteúdo
                    for (int col = 0; col < clienteTable.getColumnCount(); col++) {
                        int max = 50; // largura mínima
                        for (int row = 0; row < clienteTable.getRowCount(); row++) {
                            TableCellRenderer renderer = clienteTable.getCellRenderer(row, col);
                            Component comp = clienteTable.prepareRenderer(renderer, row, col);
                            max = Math.max(max, comp.getPreferredSize().width + 10);
                        }
                        clienteTable.getColumnModel().getColumn(col).setPreferredWidth(max);
                    }
                }
            }
        });

        c.gridy = 6;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        painel.add(clienteScroll, c);

        atualizarTabelaClientes();
        return painel;
    }

    private void atualizarComboClientes() {
        clienteComboModel.removeAllElements();
        clienteComboModel.addElement("Cliente Casual");
        for (Cliente cliente : clientes) {
            String tipo = cliente instanceof ClienteVIP ? "VIP" : "Standard";
            clienteComboModel.addElement(cliente.getNome() + " (" + tipo + ")");
        }
    }

    private void atualizarTabelaClientes() {
        DefaultTableModel modelo = (DefaultTableModel) clienteTable.getModel();
        modelo.setRowCount(0);
        for (Cliente cliente : clientes) {
            String tipo = cliente instanceof ClienteVIP ? "VIP" : "Standard";
            modelo.addRow(new Object[]{cliente.getNome(), cliente.getCpf(), tipo});
        }
    }

    private void popularCamposProdutoSelecionado() {
        int linha = menuTable.getSelectedRow();
        if (linha < 0) {
            return;
        }
        produtoCodigoField.setText((String) menuTable.getValueAt(linha, 0));
        produtoNomeField.setText((String) menuTable.getValueAt(linha, 1));
        String preco = ((String) menuTable.getValueAt(linha, 2)).replace("R$ ", "");
        produtoPrecoField.setText(preco);
        produtoEstoqueField.setText(String.valueOf(menuTable.getValueAt(linha, 3)));
        Produto produto = cardapio.get(linha);
        produtoTipoCombo.setSelectedIndex(produto instanceof Bebida ? 1 : 0);
    }

    private void popularCamposClienteSelecionado() {
        int linha = clienteTable.getSelectedRow();
        if (linha < 0) {
            return;
        }
        clienteNomeField.setText((String) clienteTable.getValueAt(linha, 0));
        clienteCPFField.setText((String) clienteTable.getValueAt(linha, 1));
        clienteTipoCombo.setSelectedItem(clienteTable.getValueAt(linha, 2));
    }

    private void limparCamposProduto() {
        produtoCodigoField.setText("");
        produtoNomeField.setText("");
        produtoPrecoField.setText("");
        produtoEstoqueField.setText("");
        produtoTipoCombo.setSelectedIndex(0);
    }

    private void limparCamposCliente() {
        clienteNomeField.setText("");
        clienteCPFField.setText("");
        clienteTipoCombo.setSelectedIndex(0);
    }

    private void adicionarProduto() {
        try {
            String codigo = produtoCodigoField.getText().trim();
            String nome = produtoNomeField.getText().trim();
            double preco = Double.parseDouble(produtoPrecoField.getText().trim());
            int estoque = Integer.parseInt(produtoEstoqueField.getText().trim());
            if (codigo.isEmpty() || nome.isEmpty()) {
                registrarLog("Código e nome são obrigatórios.");
                return;
            }
            Produto produto;
            if (produtoTipoCombo.getSelectedItem().equals("Bebida")) {
                produto = new Bebida(codigo, nome, preco, estoque, "M", 0);
            } else {
                produto = new Comida(codigo, nome, preco, estoque, 10, false);
            }
            cardapio.add(produto);
            atualizarTabelaCardapio();
            limparCamposProduto();
            registrarLog("Produto incluído: " + nome + " [" + codigo + "].");
        } catch (NumberFormatException ex) {
            registrarLog("Preço e estoque devem ser valores numéricos válidos.");
        }
    }

    private void atualizarProduto() {
        int linha = menuTable.getSelectedRow();
        if (linha < 0) {
            registrarLog("Selecione um produto para atualizar.");
            return;
        }
        try {
            Produto produto = cardapio.get(linha);
            String novoNome = produtoNomeField.getText().trim();
            double novoPreco = Double.parseDouble(produtoPrecoField.getText().trim());
            int novoEstoque = Integer.parseInt(produtoEstoqueField.getText().trim());
            if (!novoNome.isEmpty()) {
                produto.setNome(novoNome);
            }
            produto.setPrecoBase(novoPreco);
            produto.setQuantidadeEstoque(novoEstoque);
            atualizarTabelaCardapio();
            registrarLog("Produto atualizado: " + produto.getCodigo() + ".");
        } catch (NumberFormatException ex) {
            registrarLog("Preço e estoque devem ser valores numéricos válidos.");
        }
    }

    private void removerProduto() {
        int linha = menuTable.getSelectedRow();
        if (linha < 0) {
            registrarLog("Selecione um produto para remover.");
            return;
        }
        Produto produto = cardapio.remove(linha);
        atualizarTabelaCardapio();
        registrarLog("Produto removido: " + produto.getCodigo() + ".");
        limparCamposProduto();
    }

    private void adicionarCliente() {
        String nome = clienteNomeField.getText().trim();
        String cpf = clienteCPFField.getText().trim();
        String tipo = (String) clienteTipoCombo.getSelectedItem();
        if (nome.isEmpty() || cpf.isEmpty()) {
            registrarLog("Nome e CPF são obrigatórios.");
            return;
        }
        for (Cliente existente : clientes) {
            if (existente.getCpf().equals(cpf)) {
                registrarLog("Já existe cliente com este CPF.");
                return;
            }
        }
        Cliente cliente = tipo.equals("VIP") ? new ClienteVIP(nome, cpf) : new ClienteStandard(nome, cpf);
        clientes.add(cliente);
        atualizarTabelaClientes();
        atualizarComboClientes();
        limparCamposCliente();
        registrarLog("Cliente incluído: " + nome + " [" + cpf + "].");
    }

    private void atualizarCliente() {
        int linha = clienteTable.getSelectedRow();
        if (linha < 0) {
            registrarLog("Selecione um cliente para atualizar.");
            return;
        }
        String novoNome = clienteNomeField.getText().trim();
        if (novoNome.isEmpty()) {
            registrarLog("Nome não pode ficar vazio.");
            return;
        }
        Cliente cliente = clientes.get(linha);
        cliente.setNome(novoNome);
        atualizarTabelaClientes();
        atualizarComboClientes();
        registrarLog("Cliente atualizado: " + cliente.getCpf() + ".");
    }

    private void removerCliente() {
        int linha = clienteTable.getSelectedRow();
        if (linha < 0) {
            registrarLog("Selecione um cliente para remover.");
            return;
        }
        Cliente cliente = clientes.remove(linha);
        atualizarTabelaClientes();
        atualizarComboClientes();
        registrarLog("Cliente removido: " + cliente.getCpf() + ".");
        limparCamposCliente();
    }

    private JPanel criarPainelRodape() {
        JPanel painel = new JPanel(new BorderLayout(4, 4));
        painel.setBorder(BorderFactory.createTitledBorder("Registro de Ações"));

        // reduzir altura do rodapé para priorizar a área de gerenciamento de clientes
        logArea = new JTextArea(4, 20);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(800, 120));
        painel.add(logScroll, BorderLayout.CENTER);

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
            pedidoAtual = new Pedido(atendente);
        } else {
            pedidoAtual = new Pedido(atendente, clienteSelecionado);
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
            registrarLog(String.format("XP do cliente agora: %.2f.", pedidoAtual.getCliente().getSaldoXP()));
        }
        criarNovoPedido();
    }

    private void registrarLog(String mensagem) {
        logArea.append(mensagem + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
