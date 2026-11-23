package dev.rubrica.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dev.rubrica.model.Persona;

public class FileRubricaRepository {
  private final Path file;

  public FileRubricaRepository(Path file) {
    this.file = file.toAbsolutePath(); // C:\Users\...
  }

  public List<Persona> readPersoneFromFile() throws IOException {
    InputStream input;
    try {
      input = Files.newInputStream(file);
    } catch (NoSuchFileException e) {
      return new ArrayList<>();
    }

    List<Persona> persone = new ArrayList<>();
    try (Scanner scanner = new Scanner(input, StandardCharsets.UTF_8)) {
      int numeroRiga = 0;
      while (scanner.hasNextLine()) {
        numeroRiga++;
        persone.add(parsePersona(scanner.nextLine(), numeroRiga));
      }
      if (scanner.ioException() != null) {
        throw scanner.ioException();
      }
    }
    return persone;
  }

  private Persona parsePersona(String riga, int numeroRiga) throws IOException {
    String[] campi = riga.split(";", -1);
    if (campi.length != 5) {
      throw new IOException("Formato non valido alla riga " + numeroRiga + " di " + file);
    }
    try {
      int eta = Integer.parseInt(campi[4]);
      if (!Persona.isEtaValida(eta)) {
        throw new IOException("Età non valida alla riga " + numeroRiga + " di " + file
            + ": deve essere compresa tra 0 e 120.");
      }
      return new Persona(campi[0], campi[1], campi[2], campi[3], eta);
    } catch (NumberFormatException e) {
      throw new IOException("Età non valida alla riga " + numeroRiga + " di " + file, e);
    }
  }

  public void savePersone(List<Persona> persone) throws IOException {
    for (Persona persona : persone) {
      validateCampo(persona.getNome());
      validateCampo(persona.getCognome());
      validateCampo(persona.getIndirizzo());
      validateCampo(persona.getTelefono());
      if (!Persona.isEtaValida(persona.getEta())) {
        throw new IOException("L'età deve essere compresa tra 0 e 120.");
      }
    }

    // Il file originale viene sostituito solo dopo aver scritto tutti i dati.
    Files.createDirectories(file.getParent());
    Path temporaneo = Files.createTempFile(file.getParent(), "rubrica-", ".tmp");
    try {
      writePersone(temporaneo, persone);
      replaceFile(temporaneo);
    } catch (IOException e) {
      try {
        Files.deleteIfExists(temporaneo);
      } catch (IOException cleanupError) {
        // Conserva l'errore originale anche se la pulizia del file temporaneo fallisce.
        e.addSuppressed(cleanupError);
      }
      throw e;
    }
  }

  private void writePersone(Path destinazione, List<Persona> persone) throws IOException {
    PrintStream output = new PrintStream(Files.newOutputStream(destinazione), false, StandardCharsets.UTF_8);
    try (output) { // try-with-resources, quando si esce dal blocco try anche nel caso di errore
                   // chiude lo stream
      for (Persona persona : persone) {
        output.println(String.join(";",
            persona.getNome(),
            persona.getCognome(),
            persona.getIndirizzo(),
            persona.getTelefono(),
            Integer.toString(persona.getEta())));
      }
    }
    // PrintStream non lancia IOException durante la scrittura, controlla anche la
    // chiusura.
    if (output.checkError()) {
      throw new IOException("Errore durante la scrittura del file " + file);
    }
  }

  private void replaceFile(Path temporaneo) throws IOException {
    try {
      Files.move(temporaneo, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    } catch (AtomicMoveNotSupportedException e) {
      // Alcuni filesystem non supportano la sostituzione atomica (evitare una situazione in cui il file definitivo viene eliminato o modificato a metà dell'operazione)
      Files.move(temporaneo, file, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  private void validateCampo(String campo) throws IOException {
    if (campo == null || campo.contains(";") || campo.contains("\n") || campo.contains("\r")
        || campo.contains("\u0085") || campo.contains("\u2028") || campo.contains("\u2029")) {
      throw new IOException("I campi non possono contenere punto e virgola o andare a capo.");
    }
  }
}
