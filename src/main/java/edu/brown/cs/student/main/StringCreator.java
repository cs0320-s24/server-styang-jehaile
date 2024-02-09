package edu.brown.cs.student.main;

import java.util.List;

/** This class implements the creator from row interface so that it returns a list of strings. */
public class StringCreator implements CreatorFromRow<List<String>> {
  /**
   * This method overrides create to define how it should be done for a string.
   *
   * @param row
   * @return
   * @throws FactoryFailureException
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}
