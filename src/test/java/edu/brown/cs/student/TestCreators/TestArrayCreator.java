package edu.brown.cs.student.TestCreators;

import edu.brown.cs.student.main.CSVParser.CreatorFromRow;
import java.util.List;

public class TestArrayCreator implements CreatorFromRow<String[]> {
  public String[] create(List<String> row) {
    return row.toArray(new String[0]);
  }
}
