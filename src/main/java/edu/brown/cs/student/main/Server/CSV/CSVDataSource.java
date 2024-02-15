package edu.brown.cs.student.main.Server.CSV;

import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.CSVParser.CreatorFromRow;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Exceptions.MalformedRowsException;
import edu.brown.cs.student.main.SearchUtility.Search;
import edu.brown.cs.student.main.SearchUtility.SearchStrategy;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

public class CSVDataSource {

  private CSVParser<List<String>> csvParser;
  private boolean isLoaded;

  public CSVDataSource() {
    this.isLoaded = false;
  }

  public void loadCSVData(String fileName, boolean headers)
      throws IOException, MalformedRowsException, FactoryFailureException {
    String folderPath = "data";
    Path absoluteFolderPath = Paths.get(folderPath).toAbsolutePath();
    Path csvPath =
        Paths.get(
            absoluteFolderPath
                + "/"
                + fileName); // Add injection protection and more specific error messages
    FileReader fileReader = new FileReader(csvPath.toString());
    CreatorFromRow<List<String>> strategyObj = new SearchStrategy();

    this.csvParser = new CSVParser<>(fileReader, strategyObj, headers);
    this.csvParser.parse();
    this.isLoaded = true;
  }

  public List<List<String>> searchCSV(String toSearch, String headerName, String columnIndex) throws IndexOutOfBoundsException, NoSuchElementException, IllegalArgumentException {
    Search search = new Search(this.csvParser);

    if (columnIndex != null) {
      try {
        int columnIndexInt = Integer.parseInt(columnIndex);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException();
      }
      return search.searchCSV(toSearch, columnIndex);
    }

    if (headerName != null) {
      return search.searchCSV(toSearch, headerName);
    }

    return search.searchCSV(toSearch);
  }

  public List<List<String>> viewCSV() {
    if (this.csvParser.getHeaderList() != null) {
      List<List<String>> data = this.csvParser.getCSVContents();
      data.add(0, this.csvParser.getHeaderList());
      return data;
    }
    return this.csvParser.getCSVContents();
  }

  public boolean isLoaded() {
    return this.isLoaded;
  }
}
