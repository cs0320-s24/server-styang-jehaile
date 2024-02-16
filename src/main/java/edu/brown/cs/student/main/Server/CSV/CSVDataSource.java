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
/**
 * This is our CSV data source class which is passed into our loadcsv, searchcsv, viewcsv handlers to extract the
 * data from the files. This class ensures that all the files must be loaded prior to being searched or viewed by utilizing a
 * boolean value.
 */

public class CSVDataSource {

  private CSVParser<List<String>> csvParser;
  private boolean isLoaded;

  /**
   * Constructor for CSVDataSource, takes in a boolean that is set to false originally.
   */

  public CSVDataSource() {
    this.isLoaded = false;
  }

  /**
   * LoadCSVData gets the absolute folder path of the file being used, concats that with the file name
   * passed in as a parameter, in the LoadCSVHandler, where this method is called, along with a boolean indicating to
   * the CSV parser whether here are headers present. This method reads the data, creates a strategy object which handles strings, then passes
   * these values into our CSV parser which parses the data. Lastly the boolean is set to true indicating that the
   * file was loaded. This method throws exceptions which are caught in the loadcsv handler class.
   * @param fileName String representing the file name the user whats to load
   * @param headers Boolean representing whether there are headers present
   * @throws IOException thrown if there are errors reading the file
   * @throws MalformedRowsException thrown if the csv file has malformed data
   * @throws FactoryFailureException thrown if there are errors parsing the file through our csv parser
   */
  public void loadCSVData(String fileName, boolean headers)
      throws IOException, MalformedRowsException, FactoryFailureException{
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

  /**
   * This method handles the searching of the data, returning the search results as a list of lists of strings.
   * This method takes in the value to search for, the header name and the column index. This method
   * initializes an instance of the search class and passes in the parser. We make a local variable to hold the
   * integer value of the column index. We check whether there are column indexes passed in through
   * a try catch which catches illegal arguments. We then use the search class to search the csv for the row containing
   * the value and return it. The exceptions thrown in this methodl is caught in the SearchCSVHandler class
   * where we call this method.
   * @param toSearch String representing the value to search for in the data
   * @param headerName String representing the header name the user can choose to search for a value through
   * @param columnIndex String representing the column index the user can choose to search through
   * @return Returns a list of list of strings of the rows in the CSV that contains the searched result
   * @throws IndexOutOfBoundsException thrown if searching for a column  index beyond the limits of the csv file
   * @throws NoSuchElementException thrown if searching for an element that does not exist
   * @throws IllegalArgumentException thrown if the argument type is invalid
   */

  public List<List<String>> searchCSV(String toSearch, String headerName, String columnIndex) throws IndexOutOfBoundsException, NoSuchElementException, IllegalArgumentException {
    Search search = new Search(this.csvParser);
    int columnIndexInt;
    if (columnIndex != null) {
      try {
        columnIndexInt = Integer.parseInt(columnIndex);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException();
      }
      return search.searchCSV(toSearch, columnIndexInt);
    }

    if (headerName != null) {
      return search.searchCSV(toSearch, headerName);
    }

    return search.searchCSV(toSearch);
  }

  /**
   * This method returns the contents of a csv file after it is parsed as a list of lists of strings and adds
   * the list of headers, if they are present in a data set to the start of what will become the json. This
   * method is called in the viewcsvhandler class.
   * @return Represents the return which is a list of list of strings of the data in the csv file
   */

  public List<List<String>> viewCSV() {
    if (this.csvParser.getHeaderList() != null) {
      List<List<String>> data = this.csvParser.getCSVContents();
      data.add(0, this.csvParser.getHeaderList());
      return data;
    }
    return this.csvParser.getCSVContents();
  }

  /**
   * This method is called in our viewcsvhandler class and returns the boolean created in this class which
   * states the status of whether data is loaded.
   * @return a Boolean value indicating whether a file is loaded.
   */
  public boolean isLoaded() {
    return this.isLoaded;
  }
}
