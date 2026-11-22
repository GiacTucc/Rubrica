package dev.rubrica.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.rubrica.model.Persona;
import dev.rubrica.model.Rubrica;
import dev.rubrica.repository.FileRubricaRepository;

public class RubricaController {
  private final Rubrica rubrica;
  private final FileRubricaRepository repository;

  public RubricaController(Rubrica rubrica, FileRubricaRepository repository) {
    //Rubrica contiene i contatti in memoria.
    //FileRubricaRepository si occupa di leggere e salvare i contatti su file.
    this.rubrica = Objects.requireNonNull(rubrica, "rubrica"); //requireNonNull controlla che non sia null, altrimenti lancia eccezione
    this.repository = Objects.requireNonNull(repository, "repository");
  }

  public void loadPersone() throws IOException {
    rubrica.replacePersone(repository.loadPersone());
  }

  public List<Persona> getPersone() {
    //Ritorna una copia della lista delle persone, quindi non permetto di modificare l'originale
    return List.copyOf(rubrica.getPersone());
  }

  public Persona getPersona(int indice) {
    return rubrica.getPersona(indice);
  }

  public void addPersona(Persona persona) throws IOException {
    //Copia le persone attuali in una nuova lista
    List<Persona> persone = new ArrayList<>(rubrica.getPersone());
    //Aggiunge la nuova persona alla lista
    persone.add(persona);
    //Salva la lista aggiornata su file, se il salvataggio fallisce e viene 
    //lanciata un'IOException, il codice si interrompe qui e la Rubrica 
    //originale in memoria non è stata modificata.
    repository.savePersone(persone);
    //Se tutto ok, allora aggiorna la rubrica in memoria
    rubrica.addPersona(persona);
  }

  public void editPersona(int indice, Persona persona) throws IOException {
    List<Persona> persone = new ArrayList<>(rubrica.getPersone());
    persone.set(indice, persona);
    repository.savePersone(persone);
    rubrica.editPersona(indice, persona);
  }

  public void deletePersona(int indice) throws IOException {
    List<Persona> persone = new ArrayList<>(rubrica.getPersone());
    persone.remove(indice);
    repository.savePersone(persone);
    rubrica.deletePersona(indice);
  }
}
