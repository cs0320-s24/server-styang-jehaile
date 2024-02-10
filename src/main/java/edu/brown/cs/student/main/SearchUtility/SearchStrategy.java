package edu.brown.cs.student.main.SearchUtility;

import edu.brown.cs.student.main.CSVParser.CreatorFromRow;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.util.ArrayList;
import java.util.List;

public class SearchStrategy implements CreatorFromRow<List<String>> {

  public ArrayList<String> create(List<String> row) throws FactoryFailureException {
    return new ArrayList<>(row);
  }
}
