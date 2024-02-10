package edu.brown.cs.student.main.Exceptions;

import java.util.List;

public class MalformedRowsException extends Exception {
  private final List<List<String>> malformedRows;

  public MalformedRowsException(String message, List<List<String>> malformedRows) {
    super(message);
    this.malformedRows = malformedRows;
  }

  public List<List<String>> getMalformedRows() {
    return this.malformedRows;
  }
}
