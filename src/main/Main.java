package main;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager; 
import view.TelaPrincipal;
import view.TelaPrincipal;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        try {
            // Configura o Look and Feel (Tema Claro)
            UIManager.setLookAndFeel( new FlatLightLaf() );
            
            // Você também pode tentar outros temas se quiser:
            // UIManager.setLookAndFeel( new com.formdev.flatlaf.FlatDarkLaf() ); // Tema Escuro
            // UIManager.setLookAndFeel( new com.formdev.flatlaf.FlatIntelliJLaf() ); // Tema (IntelliJ)
            // UIManager.setLookAndFeel( new com.formdev.flatlaf.FlatDarculaLaf() ); // Tema (Darcula)
            
        } catch( Exception ex ) {
            System.err.println( "Falha ao iniciar o Look and Feel FlatLaf." );
        }
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
