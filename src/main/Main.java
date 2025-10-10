package main;

import view.TelaPrincipal;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                TelaPrincipal telaPrincipal = new TelaPrincipal();
                telaPrincipal.setVisible(true);
            } catch (Exception e) {
                System.err.println("Erro ao inicializar a aplicação:");
                e.printStackTrace();
            }
        });
    }
}
