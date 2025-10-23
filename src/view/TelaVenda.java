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

public class TelaVenda extends javax.swing.JFrame {

    /**
     * Creates new form TelaVenda
     */
    private final VendaController vendaController;
    private final ClienteController clienteController;
    private final ProdutoController produtoController;
    private Cliente clienteSelecionado;
    private List<Produto> produtosDisponiveis;
    private final List<ItemVenda> carrinho;
    private final DecimalFormat df;

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
    }

    private void carregarProdutosDisponiveis() {
        try {
            produtosDisponiveis = produtoController.buscarTodos();

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("--- Selecione um Produto ---");

            for (Produto p : produtosDisponiveis) {
                // Exibe nome e preço
                model.addElement(p.getNome() + " (R$ " + df.format(p.getPreco()) + ")");
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

        // Remove a parte do preço no final para buscar pelo nome
        String nome = nomeDisplay.substring(0, nomeDisplay.lastIndexOf(" (R$"));

        for (Produto p : produtosDisponiveis) {
            if (p.getNome().equals(nome)) {
                return p;
            }
        }
        return null;
    }

    private void atualizarTabelaCarrinho() {
        DefaultTableModel model = (DefaultTableModel) tblItensVenda.getModel();
        model.setRowCount(0); // Limpa a tabela

        // Define as colunas se necessário
        if (model.getColumnCount() == 0) {
            model.setColumnIdentifiers(new Object[]{"ID Prod.", "Produto", "Qtd", "Preço Unit.", "Subtotal"});
        }

        for (ItemVenda item : carrinho) {
            model.addRow(new Object[]{
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                df.format(item.getPrecoUnitario()),
                df.format(item.getSubtotal())
            });
        }
    }

    private void atualizarTotalCarrinho() {
        double total = 0.0;
        for (ItemVenda item : carrinho) {
            total += item.getSubtotal();
        }

        lblTotal.setText("VALOR TOTAL: R$ " + df.format(total));
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

        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Deseja resgatar '" + produto.getNome() + "' por " + produto.getCustoEmPontos() + " pontos?",
                    "Confirmar Resgate", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                vendaController.resgatarProduto(clienteSelecionado, produto);
                JOptionPane.showMessageDialog(this, "Produto resgatado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                clienteSelecionado = clienteController.buscarPorId(clienteSelecionado.getId());
                lblClientePontos.setText("Pontos Atuais: " + clienteSelecionado.getPontosAcumulados());

                carregarProdutosDisponiveis();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao resgatar produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblItensVenda);

        lblTotal.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        lblTotal.setText("VALOR TOTAL: R$ 0,00");

        btnFinalizarVenda.setBackground(new java.awt.Color(51, 177, 35));
        btnFinalizarVenda.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        btnFinalizarVenda.setForeground(new java.awt.Color(255, 255, 255));
        btnFinalizarVenda.setText("Finalizar vendas");
        btnResgatarPontos.setText("RESGATAR ITEM COM PONTOS");
        btnResgatarPontos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResgatarPontosActionPerformed(evt);
            }
        });

        btnFinalizarVenda.setText("FINALIZAR VENDA");
        btnFinalizarVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarVendaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblTitulo)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblClienteNome)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblTotal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                                .addComponent(btnBuscarCliente)))
                        .addGap(101, 101, 101))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(24, 24, 24)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCpf))
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
                .addGap(44, 44, 44)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal)
                    .addComponent(btnFinalizarVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(83, Short.MAX_VALUE))
        );

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
            return;
        }

        if (produto.getEstoque() < quantidade) {
            JOptionPane.showMessageDialog(this, "Estoque insuficiente. Disponível: " + produto.getEstoque(), "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Cria o ItemVenda
        double subtotal = produto.getPreco() * quantidade;
        ItemVenda novoItem = new ItemVenda(produto, quantidade, produto.getPreco(), subtotal);

        // Adiciona ao carrinho
        carrinho.add(novoItem);

        // Atualiza UI
        atualizarTabelaCarrinho();
        atualizarTotalCarrinho();

        // Reseta seleção
        cmbProduto.setSelectedIndex(0);
        spnQuantidade.setValue(1);
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
    private javax.swing.JButton btnFinalizarVenda;
    private javax.swing.JButton btnResgatarPontos;
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
    private javax.swing.JSpinner spnQuantidade;
    private javax.swing.JTable tblItensVenda;
    private javax.swing.JTextField txtCpf;
    // End of variables declaration//GEN-END:variables
}
