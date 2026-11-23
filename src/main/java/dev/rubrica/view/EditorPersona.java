package dev.rubrica.view;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.Window;
import java.io.IOException;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import dev.rubrica.model.Persona;
import dev.rubrica.view.interfaces.SaveAction;

public class EditorPersona extends JDialog {
  private final JTextField campoNome = new JTextField(20);
  private final JTextField campoCognome = new JTextField(20);
  private final JTextField campoIndirizzo = new JTextField(20);
  private final JTextField campoTelefono = new JTextField(20);
  private final JTextField campoEta = new JTextField(20);

  private Persona personaSalvata;
  private final SaveAction saveAction;

  public EditorPersona(Window ownerWindow, SaveAction saveAction) {
    // Richiamo costrutture principale (sotto con Optional), caso nuova persona
    this(ownerWindow, Optional.empty(), saveAction);
  }

  public EditorPersona(Window ownerWindow, Persona persona, SaveAction saveAction) {
    // Richiamo costrutture principale (sotto con Optional), caso modifica persona
    this(ownerWindow, Optional.of(persona), saveAction);
  }

  private EditorPersona(Window ownerWindow, Optional<Persona> persona, SaveAction saveAction) {
    super(
        ownerWindow,
        persona.isPresent() ? "Modifica persona" : "Nuova persona",
        Dialog.ModalityType.APPLICATION_MODAL); // Indica che l'utente non può interagire con le altre finestre
                                                // dell'applicazione finché non chiude questa

    this.saveAction = saveAction;
    JPanel campi = new JPanel(new GridLayout(5, 2, 8, 8));
    campi.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12)); // padding
    campi.add(new JLabel("Nome:"));
    campi.add(campoNome);
    campi.add(new JLabel("Cognome:"));
    campi.add(campoCognome);
    campi.add(new JLabel("Indirizzo:"));
    campi.add(campoIndirizzo);
    campi.add(new JLabel("Telefono:"));
    campi.add(campoTelefono);
    campi.add(new JLabel("Età:"));
    campi.add(campoEta);

    // :: si chiama method reference, ed equivale a: persona.ifPresent(p ->
    // populateFields(p));
    persona.ifPresent(this::populateFields);

    JButton btnSalva = ToolbarButtons.createButton("Salva", "salva");
    btnSalva.addActionListener(e -> savePersona());
    JButton btnAnnulla = ToolbarButtons.createButton("Annulla", "annulla");
    btnAnnulla.addActionListener(e -> dispose());

    JToolBar barraStrumenti = ToolbarButtons.createToolbar(btnSalva, btnAnnulla);

    // come disporre e organizzare gli elementi all'interno di un contenitore.
    // borderLayout divide il contenitore in 5 aree: north, south, east, west,
    // center
    setLayout(new BorderLayout());
    add(campi, BorderLayout.CENTER);
    add(barraStrumenti, BorderLayout.NORTH);
    getRootPane().setDefaultButton(btnSalva);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack(); // calcola quanto spazio serve per i componenti e ridimensiona la finestra di
            // conseguenza
    setLocationRelativeTo(ownerWindow);
  }

  public Optional<Persona> showDialog() {
    setVisible(true);
    return Optional.ofNullable(personaSalvata); // ofNullable() crea un Optional a partire da un valore che potrebbe
                                                // essere null
    //quando chiude il dialogo, ritorna la persona salvata
  }

  private void populateFields(Persona persona) {
    campoNome.setText(persona.getNome());
    campoCognome.setText(persona.getCognome());
    campoIndirizzo.setText(persona.getIndirizzo());
    campoTelefono.setText(persona.getTelefono());
    campoEta.setText(Integer.toString(persona.getEta()));
  }

  private void savePersona() {
    if (!validateFields()) {
      return;
    }
    if (!validateNomeECognome()) {
      return;
    }
    if (!validateTelefono()) {
      return;
    }
    if (!validateEta()) {
      return;
    }
    int eta = Integer.parseInt(campoEta.getText().trim());
    Persona persona = new Persona(
        campoNome.getText().trim(),
        campoCognome.getText().trim(),
        campoIndirizzo.getText().trim(),
        campoTelefono.getText().trim(),
        eta);
    try {
      saveAction.save(persona);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(
          this,
          "Impossibile salvare la persona: " + e.getMessage(),
          "Errore di salvataggio",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    personaSalvata = persona;
    dispose();
  }

  private boolean validateFields() {
    JTextField[] campi = {
        campoNome,
        campoCognome,
        campoIndirizzo,
        campoTelefono,
        campoEta
    };
    for (JTextField campo : campi) {
      if (campo.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(
            this,
            "Tutti i campi sono obbligatori.",
            "Dato mancante",
            JOptionPane.ERROR_MESSAGE);
        campo.requestFocusInWindow(); //sposta il focus sul campo di input
        return false;
      }
    }
    return true;
  }

  private boolean validateNomeECognome() {
    for (JTextField campo : new JTextField[] { campoNome, campoCognome }) {
      if (campo.getText().codePoints().anyMatch(Character::isDigit)) {
        JOptionPane.showMessageDialog(
            this,
            "Nome e cognome non possono contenere cifre.",
            "Dato non valido",
            JOptionPane.ERROR_MESSAGE);
        campo.requestFocusInWindow();
        return false;
      }
    }
    return true;
  }

  private boolean validateTelefono() {
    if (!campoTelefono.getText().trim().matches("(?:\\+[1-9][0-9]{0,2} ?)?[0-9]{6,12}")) {
      JOptionPane.showMessageDialog(
          this,
          "Inserisci da 6 a 12 cifre, con eventuale prefisso internazionale (es. +39).",
          "Telefono non valido",
          JOptionPane.ERROR_MESSAGE);
      campoTelefono.requestFocusInWindow();
      return false;
    }
    return true;
  }

  private boolean validateEta() {
    try {
      int eta = Integer.parseInt(campoEta.getText().trim());
      if (!Persona.isEtaValida(eta)) {
        JOptionPane.showMessageDialog(this, "L'età deve essere compresa tra 0 e 120.");
        campoEta.requestFocusInWindow();
        return false;
      }
      return true;
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Inserisci un'età numerica valida.");
      campoEta.requestFocusInWindow();
      return false;
    }
  }
}
