package edu.brown.cs.student.TestCreators.TestPersonParser;

import edu.brown.cs.student.main.CSVParser.CreatorFromRow;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.util.List;

public class TestPersonCreator implements CreatorFromRow<Person> {
  public Person create(List<String> row) throws FactoryFailureException {
    if (row.size() < 2) {
      throw new FactoryFailureException(
          "Invalid row size. Row must contain at least 2 values.", row);
    }

    String name = row.get(0);
    String ageString = (row.get(1));

    try {
      int age = Integer.parseInt(ageString);
      return new Person(name, age);
    } catch (NumberFormatException e) {
      throw new FactoryFailureException("Invalid age value in row: " + ageString, row);
    }
  }
}
