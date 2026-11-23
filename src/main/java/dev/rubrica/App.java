package dev.rubrica;

import dev.rubrica.controller.RubricaController;
import dev.rubrica.model.Rubrica;
import dev.rubrica.repository.FileRubricaRepository;
import dev.rubrica.view.MainWindow;
import java.io.IOException;
import java.nio.file.Path;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {

        //invokeLater fa usare un thread dedicato per la GUI, chiamato EDT (Event Dispatch Thread).
        SwingUtilities.invokeLater(() -> {
            RubricaController controller = new RubricaController(
                    new Rubrica(), new FileRubricaRepository(Path.of("informazioni.txt")));
            try {
                controller.reloadRubricaFromRepository();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Impossibile caricare la rubrica: " + e.getMessage(),
                        "Errore di caricamento",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            MainWindow finestra = new MainWindow(controller);
            finestra.setVisible(true);
        });
    }
}
