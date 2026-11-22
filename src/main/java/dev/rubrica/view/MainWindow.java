package dev.rubrica.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import dev.rubrica.controller.RubricaController;
import dev.rubrica.model.Persona;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class MainWindow extends JFrame {
  private JTable tabella;
  private DefaultTableModel modelloTabella;

  private JButton btnNuovo;
  private JButton btnModifica;
  private JButton btnElimina;

  private final RubricaController controller;

  public MainWindow(RubricaController controller) {
    this.controller = controller;
    setTitle("Rubrica");
    setSize(650, 450);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    initializeComponents();
    loadPersone();

    btnNuovo.addActionListener(e -> addPersonaAction());
    btnModifica.addActionListener(e -> editPersonaAction());

    // Listener del pulsante Elimina
    btnElimina.addActionListener(e -> deletePersonaAction());
  }

  private void initializeComponents() {
    // Modello della tabella
    modelloTabella = new DefaultTableModel(
        new Object[] { "Nome", "Cognome", "Telefono" },
        0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    // Tabella
    tabella = new JTable(modelloTabella);
    // Scroll della tabella
    JScrollPane scrollPane = new JScrollPane(tabella);
    btnNuovo = ToolbarButtons.createButton("Nuovo", "nuovo");
    btnModifica = ToolbarButtons.createButton("Modifica", "modifica");
    btnElimina = ToolbarButtons.createButton("Elimina", "elimina");
    JToolBar barraStrumenti = ToolbarButtons.createToolbar(btnNuovo, btnModifica, btnElimina);
    setLayout(new BorderLayout());
    add(scrollPane, BorderLayout.CENTER);
    add(barraStrumenti, BorderLayout.NORTH);

  }

  private void loadPersone() {
    // Svuoto la tabella
    modelloTabella.setRowCount(0);
    for (Persona persona : controller.getPersone()) {
      modelloTabella.addRow(new Object[] {
          persona.getNome(),
          persona.getCognome(),
          persona.getTelefono()
      });
    }
  }

  private void deletePersonaAction() {
    // Restituisce l'indice della riga selezionata, -1 se nulla è selezionato.
    int indexRow = tabella.getSelectedRow();
    if (indexRow == -1) {
      JOptionPane.showMessageDialog(
          this,
          "Seleziona una persona da eliminare.");
      return;
    }
    // Converte l'indice visibile in quello del modello, anche se la tabella è
    // ordinata o filtrata.
    int index = tabella.convertRowIndexToModel(indexRow);
    Persona persona = controller.getPersona(index);
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Eliminare la persona " + persona.getNome() + " " + persona.getCognome() + "?",
        "Conferma eliminazione",
        JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
      try {
        controller.deletePersona(index);
        loadPersone();
      } catch (IOException e) {
        JOptionPane.showMessageDialog(
            this,
            "Impossibile eliminare la persona: " + e.getMessage(),
            "Errore di salvataggio",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void addPersonaAction() {
    Optional<Persona> persona = new EditorPersona(this, controller::addPersona).showDialog();
    if (persona.isPresent()) {
      // chiusa la finestra, se è stata aggiunta una persona ricarico la tabella
      loadPersone();
    }
  }

  private void editPersonaAction() {
    int indexRow = tabella.getSelectedRow();
    if (indexRow == -1) {
      JOptionPane.showMessageDialog(
          this,
          "Seleziona una persona da modificare.");
      return;
    }

    int index = tabella.convertRowIndexToModel(indexRow);
    Persona persona = controller.getPersona(index);
    Optional<Persona> personaEdited = new EditorPersona(this, persona, edited -> controller.editPersona(index, edited))
        .showDialog();
    if (personaEdited.isPresent()) {
      loadPersone();
    }
  }
}
