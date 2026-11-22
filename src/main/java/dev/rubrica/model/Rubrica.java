package dev.rubrica.model;

import java.util.ArrayList;
import java.util.List;

public class Rubrica {
  private final List<Persona> persone = new ArrayList<>();

  public void addPersona(Persona p) {
    persone.add(p);
  }

  public Persona getPersona(int indice) {
    return persone.get(indice);
  }

  public void editPersona(int indice, Persona persona) {
    persone.set(indice, persona);
  }

  public void deletePersona(int indice) {
    persone.remove(indice);
  }

  public List<Persona> getPersone() {
    return List.copyOf(persone);
  }

  public void replacePersone(List<Persona> nuovePersone) {
    List<Persona> copia = List.copyOf(nuovePersone);
    persone.clear();
    persone.addAll(copia);
  }

  public int getNumberPersone() {
    return persone.size();
  }

}
