package dev.rubrica.view.interfaces;

import java.io.IOException;

import dev.rubrica.model.Persona;

@FunctionalInterface //chiedere al compilatore di controllare che ci sia un solo metodo astratto
public interface SaveAction {
  void save(Persona persona) throws IOException;
}
