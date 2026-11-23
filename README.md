# Rubrica

Applicazione desktop Java Swing per gestire una rubrica di contatti. Consente di
aggiungere, modificare ed eliminare persone e salva i dati nel file
`informazioni.txt`.

## Requisiti

- Java 21
- Per compilare su Windows: il Maven Wrapper incluso nel progetto

Verifica la versione di Java:

```powershell
java -version
```

## Build

Apri PowerShell nella cartella del progetto ed esegui:

```powershell
.\mvnw.cmd package
```

Al termine, il JAR eseguibile si trova in `target\Rubrica.jar`.

## Avvio dal progetto

Dalla cartella principale del progetto:

```powershell
java -jar .\target\Rubrica.jar
```

In questo modo l'applicazione usa il file `informazioni.txt` presente nella
cartella principale.

## Distribuzione e avvio del JAR

Copia `target\Rubrica.jar` e `informazioni.txt` nella stessa cartella sul
computer di destinazione. Apri PowerShell in quella cartella ed esegui:

```powershell
java -jar Rubrica.jar
```

Il computer deve avere Java 21 o successivo installato. Le modifiche ai
contatti vengono salvate nel file `informazioni.txt` di quella cartella.

## Formato dei dati

Il file contiene una persona per riga, con i campi separati da punto e virgola:

```text
nome;cognome;indirizzo;telefono;eta
```

Se `informazioni.txt` non esiste all'avvio, la rubrica parte vuota; il file
verrà creato al primo salvataggio.
