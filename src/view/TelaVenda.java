package view;

import controller.ClienteController;
import controller.ProdutoController;
import controller.VendaController;
import model.Cliente;
import model.Produto;
import model.ItemVenda;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ComboBoxModel;

public class TelaVenda extends javax.swing.JFrame {

    private final VendaController vendaController;
    private final ClienteController clienteController;
    private final ProdutoController produtoController;
    private Cliente clienteSelecionado;
    private List<Produto> produtosDisponiveis;
    private final List<ItemVenda> carrinho;
    private final DecimalFormat df;
    private int indiceItemSelecionado = -1;

    public TelaVenda() {
        initComponents();
        this.df = new DecimalFormat("R$ #,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
        this.vendaController = new VendaController();
        this.clienteController = new ClienteController();
        this.produtoController = new ProdutoController();
        this.carrinho = new ArrayList<>();

        carregarProdutosDisponiveis();
        atualizarTotalCarrinho();
        setLocationRelativeTo(null);

        tblItensVenda.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tblItensVendaMouseClicked(e);
            }
        });

        btnEditarItem.setEnabled(false);
        btnExcluirItem.setEnabled(false);
    }

    private void carregarProdutosDisponiveis() {
        try {
            produtosDisponiveis = produtoController.buscarTodos();

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("--- Selecione um Produto ---");

            for (Produto p : produtosDisponiveis) {
                String display = p.getNome();
                if (p.getCustoEmPontos() > 0) {
                    display += " (" + p.getCustoEmPontos() + " pts)";
                } else {
                    display += " (R$ " + String.format("%.2f", p.getPreco()) + ")"; // Usar String.format para evitar problemas com df
                }
                model.addElement(display);
            }
            cmbProduto.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Produto getProdutoSelecionado(String nomeDisplay) {
        if (nomeDisplay == null || nomeDisplay.contains("---")) {
            return null;
        }

        String nome = nomeDisplay;
        int parenteseIndex = nomeDisplay.lastIndexOf(" (");
        if (parenteseIndex != -1) {
            nome = nomeDisplay.substring(0, parenteseIndex);
        }

        for (Produto p : produtosDisponiveis) {
            if (p.getNome().equals(nome)) {
                return p;
            }
        }
        return null;
    }

    private void atualizarTabelaCarrinho() {
        DefaultTableModel model = (DefaultTableModel) tblItensVenda.getModel();
        model.setRowCount(0);

        if (model.getColumnCount() == 0) {
            model.setColumnIdentifiers(new Object[]{"ID Prod.", "Produto", "Qtd", "Preço Unit.", "Subtotal"});
        }

        for (ItemVenda item : carrinho) {
            String precoUnitStr = item.getPrecoUnitario() == 0.0 ? "(Resgatado)" : df.format(item.getPrecoUnitario());
            String subtotalStr = item.getSubtotal() == 0.0 ? "(0 pts)" : df.format(item.getSubtotal());

            model.addRow(new Object[]{
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                precoUnitStr,
                subtotalStr
            });

        }
        limparSelecaoCarrinho();
    }

    private void atualizarTotalCarrinho() {
        double total = 0.0;
        int pontosGastos = 0;

        for (ItemVenda item : carrinho) {
            total += item.getSubtotal();
            if (item.getPrecoUnitario() == 0.0) {
                pontosGastos += item.getProduto().getCustoEmPontos() * item.getQuantidade();
            }
        }

        String totalStr = "VALOR TOTAL: " + df.format(total);
        if (pontosGastos > 0) {
            totalStr += " (-" + pontosGastos + " pts)";
        }
        lblTotal.setText(totalStr);
    }

    private void limparSelecaoCarrinho() {
        this.indiceItemSelecionado = -1;
        tblItensVenda.clearSelection();
        btnEditarItem.setEnabled(false);
        btnExcluirItem.setEnabled(false);

        spnQuantidade.setValue(1);
        cmbProduto.setSelectedIndex(0);
    }

    private void resgatarComPontos() {
        if (clienteSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente primeiro.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Produto produto = getProdutoSelecionado((String) cmbProduto.getSelectedItem());

        if (produto == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para resgatar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (produto.getCustoEmPontos() <= 0) {
            JOptionPane.showMessageDialog(this, "Este produto não pode ser resgatado com pontos.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quantidadeResgate = 1;

        if (produto.getEstoque() < quantidadeResgate) {
            JOptionPane.showMessageDialog(this, "Estoque insuficiente para resgate. Disponível: " + produto.getEstoque(), "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int pontosNecessarios = produto.getCustoEmPontos() * quantidadeResgate;
        if (clienteSelecionado.getPontosAcumulados() < pontosNecessarios) {
            JOptionPane.showMessageDialog(this, "Pontos insuficientes para resgate. Necessário: "
                    + pontosNecessarios + ", Atual: "
                    + clienteSelecionado.getPontosAcumulados(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja resgatar " + quantidadeResgate + "x '" + produto.getNome() + "' por " + pontosNecessarios + " pontos?",
                "Confirmar Resgate", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                clienteController.subtrairPontos(clienteSelecionado.getId(), pontosNecessarios);

                produtoController.atualizarProduto(
                        produto.getId(),
                        produto.getNome(),
                        produto.getPreco(),
                        produto.getTipo(),
                        produto.getEstoque() - quantidadeResgate,
                        produto.getCustoEmPontos()
                );

                Produto produtoAtualizado = produtoController.findById(produto.getId());
                if (produtoAtualizado == null) {
                    throw new Exception("Produto não encontrado após atualização de estoque.");
                }
                ItemVenda itemResgatado = new ItemVenda(produto, quantidadeResgate, 0.0, 0.0);

                carrinho.add(itemResgatado);

                atualizarTabelaCarrinho();
                atualizarTotalCarrinho();

                clienteSelecionado = clienteController.buscarPorId(clienteSelecionado.getId());
                lblClientePontos.setText("Pontos Atuais: " + clienteSelecionado.getPontosAcumulados());

                carregarProdutosDisponiveis();

                JOptionPane.showMessageDialog(this, "Item resgatado e adicionado ao carrinho!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao processar o resgate: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                try {
                    if (clienteSelecionado != null) {
                        clienteSelecionado = clienteController.buscarPorId(clienteSelecionado.getId());
                        lblClientePontos.setText("Pontos Atuais: " + clienteSelecionado.getPontosAcumulados());
                    }
                } catch (Exception ex) {
                }
            }
        }
    }

    private void tblItensVendaMouseClicked(java.awt.event.MouseEvent evt) {
        int linha = tblItensVenda.getSelectedRow();

        if (linha < 0 || linha >= carrinho.size()) {
            limparSelecaoCarrinho();
            return;
        }

        ItemVenda itemClicado = carrinho.get(linha);
        this.indiceItemSelecionado = linha;

        btnExcluirItem.setEnabled(true);

        if (itemClicado.getPrecoUnitario() == 0.0) {
            btnEditarItem.setEnabled(false);
        } else {
            btnEditarItem.setEnabled(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        lblCpf = new javax.swing.JLabel();
        txtCpf = new javax.swing.JTextField();
        btnBuscarCliente = new javax.swing.JButton();
        lblClienteNome = new javax.swing.JLabel();
        lblClientePontos = new javax.swing.JLabel();
        btnResgatarPontos = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        lblProdutos = new javax.swing.JLabel();
        cmbProduto = new javax.swing.JComboBox<>();
        lblQuantidade = new javax.swing.JLabel();
        spnQuantidade = new javax.swing.JSpinner();
        btnAdicionarItem = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItensVenda = new javax.swing.JTable();
        lblTotal = new javax.swing.JLabel();
        btnFinalizarVenda = new javax.swing.JButton();
        btnVoltar = new javax.swing.JButton();
        btnEditarItem = new javax.swing.JButton();
        btnExcluirItem = new javax.swing.JButton();
        lblTotal1 = new javax.swing.JLabel();
        btnResgatarPontos1 = new javax.swing.JButton();
        btnFinalizarVenda1 = new javax.swing.JButton();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro de Venda(PDV)");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/banner2.png"))); // NOI18N

        lblTitulo.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 24)); // NOI18N
        lblTitulo.setText("Registro de venda (PDV):");

        lblCpf.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        lblCpf.setText("CPF do Cliente:");

        txtCpf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCpfActionPerformed(evt);
            }
        });

        btnBuscarCliente.setBackground(new java.awt.Color(252, 223, 228));
        btnBuscarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/tabler_search.png"))); // NOI18N
        btnBuscarCliente.setText("Buscar");
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        lblClienteNome.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        lblClienteNome.setText("Cliente: Nenhum Cliente Selecionado");

        lblClientePontos.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        lblClientePontos.setText("Pontos atuais: 0");

        btnResgatarPontos.setBackground(new java.awt.Color(255, 102, 0));
        btnResgatarPontos.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        btnResgatarPontos.setForeground(new java.awt.Color(255, 255, 255));
        btnResgatarPontos.setText("Resgatar item com pontos");
        btnResgatarPontos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResgatarPontosActionPerformed1(evt);
            }
        });

        lblProdutos.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        lblProdutos.setText("Produto:");

        cmbProduto.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        cmbProduto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblQuantidade.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        lblQuantidade.setText("Quantidade:");

        spnQuantidade.setModel(new javax.swing.SpinnerNumberModel());

        btnAdicionarItem.setBackground(new java.awt.Color(255, 179, 192));
        btnAdicionarItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/Vendas-menor.png"))); // NOI18N
        btnAdicionarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarItemActionPerformed(evt);
            }
        });

        tblItensVenda.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Qtd", "Proço Un.", "Subtotal"
            }
        ));
        jScrollPane1.setViewportView(tblItensVenda);

        lblTotal.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        lblTotal.setText("VALOR TOTAL: R$ 0,00");

        btnFinalizarVenda.setBackground(new java.awt.Color(51, 177, 35));
        btnFinalizarVenda.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        btnFinalizarVenda.setForeground(new java.awt.Color(255, 255, 255));
        btnFinalizarVenda.setText("Finalizar vendas");
        btnFinalizarVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarVendaActionPerformed(evt);
            }
        });

        btnVoltar.setBackground(new java.awt.Color(255, 102, 153));
        btnVoltar.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        btnVoltar.setForeground(new java.awt.Color(255, 255, 255));
        btnVoltar.setText("Voltar");
        btnVoltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoltarActionPerformed(evt);
            }
        });

        btnEditarItem.setBackground(new java.awt.Color(255, 102, 0));
        btnEditarItem.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        btnEditarItem.setForeground(new java.awt.Color(255, 255, 255));
        btnEditarItem.setLabel("EDITAR");
        btnEditarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarItemActionPerformed(evt);
            }
        });

        btnExcluirItem.setBackground(new java.awt.Color(242, 0, 0));
        btnExcluirItem.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 16)); // NOI18N
        btnExcluirItem.setForeground(new java.awt.Color(255, 255, 255));
        btnExcluirItem.setText("EXCLUIR");
        btnExcluirItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirItemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(jLabel2)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblTitulo)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblTotal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnVoltar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnFinalizarVenda))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblProdutos)
                                .addGap(27, 27, 27)
                                .addComponent(cmbProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(43, 43, 43)
                                .addComponent(lblQuantidade)
                                .addGap(18, 18, 18)
                                .addComponent(spnQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAdicionarItem, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblClientePontos)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnResgatarPontos))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblCpf)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnBuscarCliente))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(btnEditarItem, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnExcluirItem, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(101, 101, 101))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblClienteNome)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(24, 24, 24)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCpf, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblCpf)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblClienteNome)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblClientePontos)
                            .addComponent(btnResgatarPontos, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblProdutos)
                            .addComponent(cmbProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblQuantidade)
                            .addComponent(spnQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnAdicionarItem, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnExcluirItem, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnEditarItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal)
                    .addComponent(btnFinalizarVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVoltar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(83, Short.MAX_VALUE))
        );

        lblTotal1.setText("VALOR TOTAL: R$ 0,00");

        btnResgatarPontos1.setText("RESGATAR ITEM COM PONTOS");
        btnResgatarPontos1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResgatarPontosActionPerformed(evt);
            }
        });

        btnFinalizarVenda1.setText("FINALIZAR VENDA");
        btnFinalizarVenda1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarVendaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteActionPerformed
        String cpf = txtCpf.getText().trim().replaceAll("\\D", ""); // Limpa o CPF
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe um CPF para buscar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // ClienteController deve ter um método para buscar por CPF
            Cliente cliente = clienteController.buscarPorCpf(cpf);

            if (cliente != null) {
                this.clienteSelecionado = cliente;
                lblClienteNome.setText("Cliente: " + cliente.getNome());
                lblClientePontos.setText("Pontos Atuais: " + cliente.getPontosAcumulados());
                JOptionPane.showMessageDialog(this, "Cliente encontrado e selecionado!");
            } else {
                this.clienteSelecionado = null;
                lblClienteNome.setText("Cliente: Não Encontrado");
                lblClientePontos.setText("Pontos Atuais: 0");
                JOptionPane.showMessageDialog(this, "Cliente não encontrado.", "Erro de Busca", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar cliente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnBuscarClienteActionPerformed

    private void btnAdicionarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarItemActionPerformed
        Produto produto = getProdutoSelecionado((String) cmbProduto.getSelectedItem());
        int quantidade = (Integer) spnQuantidade.getValue();

        if (produto == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto válido.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (quantidade <= 0) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.", "Erro", JOptionPane.WARNING_MESSAGE);
            spnQuantidade.setValue(1);
            return;
        }

        if (this.indiceItemSelecionado != -1) {
            ItemVenda item = carrinho.get(this.indiceItemSelecionado);

            int estoqueNecessarioAtual = quantidade;
            int estoqueOriginalNoCarrinho = item.getQuantidade();
            int estoqueNovoProduto = produto.getEstoque(); // Estoque atual do produto selecionado no combo

            if (item.getProduto().getId() == produto.getId()) {
                if (estoqueNovoProduto < (estoqueNecessarioAtual - estoqueOriginalNoCarrinho)) {
                    JOptionPane.showMessageDialog(this, "Estoque insuficiente para adicionar mais unidades. Disponível: " + estoqueNovoProduto, "Erro", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                if (estoqueNovoProduto < estoqueNecessarioAtual) {
                    JOptionPane.showMessageDialog(this, "Estoque insuficiente para o *novo* produto. Disponível: " + estoqueNovoProduto, "Erro", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(produto.getPreco());
            item.setSubtotal(produto.getPreco() * quantidade);

        } else {
            if (produto.getEstoque() < quantidade) {
                JOptionPane.showMessageDialog(this, "Estoque insuficiente. Disponível: " + produto.getEstoque(), "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double subtotal = produto.getPreco() * quantidade;
            ItemVenda novoItem = new ItemVenda(produto, quantidade, produto.getPreco(), subtotal);
            carrinho.add(novoItem);
        }

        atualizarTabelaCarrinho();
        atualizarTotalCarrinho();
    }//GEN-LAST:event_btnAdicionarItemActionPerformed

    private void btnFinalizarVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarVendaActionPerformed
        if (carrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio. Adicione itens para finalizar a venda.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (clienteSelecionado == null) {
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Nenhum cliente foi selecionado. Deseja continuar a venda sem acumular pontos?",
                    "Confirmar Venda", JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.NO_OPTION) {
                return;
            }
        }

        try {
            vendaController.registrarVenda(clienteSelecionado, carrinho);
            carrinho.clear();
            atualizarTabelaCarrinho();
            atualizarTotalCarrinho();
            carregarProdutosDisponiveis();

            clienteSelecionado = null;
            lblClienteNome.setText("Cliente: Nenhum Cliente Selecionado");
            lblClientePontos.setText("Pontos atuais: 0");
            txtCpf.setText("");

            JOptionPane.showMessageDialog(this, "Venda registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao finalizar venda: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnFinalizarVendaActionPerformed

    private void txtCpfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCpfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCpfActionPerformed
    private void btnResgatarPontosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResgatarPontosActionPerformed
        resgatarComPontos();
    }//GEN-LAST:event_btnResgatarPontosActionPerformed

    private void btnVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoltarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnVoltarActionPerformed

    private void btnResgatarPontosActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResgatarPontosActionPerformed1
        resgatarComPontos();
    }//GEN-LAST:event_btnResgatarPontosActionPerformed1

    private void btnExcluirItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirItemActionPerformed
        if (indiceItemSelecionado < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item na tabela para excluir.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir este item do carrinho?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            ItemVenda itemRemovido = carrinho.get(indiceItemSelecionado);

            if (itemRemovido.getPrecoUnitario() == 0.0) {
                try {
                    int pontosDevolvidos = itemRemovido.getProduto().getCustoEmPontos() * itemRemovido.getQuantidade();
                    int estoqueDevolvido = itemRemovido.getQuantidade();
                    Produto produto = itemRemovido.getProduto();

                    if (clienteSelecionado != null) {
                        clienteController.adicionarPontos(clienteSelecionado.getId(), pontosDevolvidos);
                        clienteSelecionado = clienteController.buscarPorId(clienteSelecionado.getId());
                        lblClientePontos.setText("Pontos Atuais: " + clienteSelecionado.getPontosAcumulados());
                    }

                    Produto produtoDB = produtoController.findById(produto.getId()); // Busca estado atual do BD
                    if (produtoDB == null) {
                        throw new Exception("Produto não encontrado no banco para devolver estoque.");
                    }

                    produtoController.atualizarProduto(
                            produtoDB.getId(),
                            produtoDB.getNome(),
                            produtoDB.getPreco(),
                            produtoDB.getTipo(),
                            produtoDB.getEstoque() + estoqueDevolvido, // Incrementa estoque
                            produtoDB.getCustoEmPontos()
                    );

                    carregarProdutosDisponiveis();

                    JOptionPane.showMessageDialog(this, "Pontos e estoque do item resgatado foram devolvidos.", "Item Removido", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erro ao tentar reverter pontos/estoque do item resgatado: " + e.getMessage() + "\nPor favor, ajuste manualmente.", "Erro na Reversão", JOptionPane.ERROR_MESSAGE);
                }
            }

            carrinho.remove(indiceItemSelecionado);

            atualizarTabelaCarrinho();
            atualizarTotalCarrinho();
        } else {
            limparSelecaoCarrinho();
        }
    }//GEN-LAST:event_btnExcluirItemActionPerformed

    private void btnEditarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarItemActionPerformed
        if (indiceItemSelecionado < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item na tabela para editar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ItemVenda item = carrinho.get(indiceItemSelecionado);

        if (item.getPrecoUnitario() == 0.0) {
            JOptionPane.showMessageDialog(this, "Itens resgatados com pontos não podem ser editados.", "Ação Inválida", JOptionPane.WARNING_MESSAGE);
            limparSelecaoCarrinho();
            return;
        }

        spnQuantidade.setValue(item.getQuantidade());

        String produtoDisplay = item.getProduto().getNome();

        produtoDisplay += " (R$ " + String.format("%.2f", item.getProduto().getPreco()) + ")";

        ComboBoxModel<String> model = cmbProduto.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i) != null && model.getElementAt(i).equals(produtoDisplay)) { // Adicionado null check
                cmbProduto.setSelectedIndex(i);
                break;
            }
        }

        JOptionPane.showMessageDialog(this,
                "Item carregado para edição. Ajuste os campos e clique no botão '+' para atualizar o item.",
                "Editar Item",
                JOptionPane.INFORMATION_MESSAGE);
        spnQuantidade.requestFocus();
    }//GEN-LAST:event_btnEditarItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaVenda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaVenda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaVenda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaVenda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaVenda().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionarItem;
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnEditarItem;
    private javax.swing.JButton btnExcluirItem;
    private javax.swing.JButton btnFinalizarVenda;
    private javax.swing.JButton btnFinalizarVenda1;
    private javax.swing.JButton btnResgatarPontos;
    private javax.swing.JButton btnResgatarPontos1;
    private javax.swing.JButton btnVoltar;
    private javax.swing.JComboBox<String> cmbProduto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblClienteNome;
    private javax.swing.JLabel lblClientePontos;
    private javax.swing.JLabel lblCpf;
    private javax.swing.JLabel lblProdutos;
    private javax.swing.JLabel lblQuantidade;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotal1;
    private javax.swing.JSpinner spnQuantidade;
    private javax.swing.JTable tblItensVenda;
    private javax.swing.JTextField txtCpf;
    // End of variables declaration//GEN-END:variables
}
