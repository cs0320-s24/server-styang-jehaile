package edu.brown.cs.student.main;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Exceptions.MalformedRowsException;
import edu.brown.cs.student.main.SearchUtility.Search;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** The Main class of our project. This is where execution begins. */
public final class Main {

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main().run();
  }

  /**
   * Executes the main functionality of the program, including parsing CSV files and performing
   * searches.
   */
  private void run() {
    Scanner scanner = new Scanner(System.in);

    Search search;
    // Query filepath
    System.out.println(
        "Please enter filename. Ensure that file is in 'data' folder. If located in a "
            + "subfolder, please enter in the format subfolder/filename, using it's absolute path"
            + " from the data folder");
    String csvName = scanner.nextLine();

    // Query headers
    boolean headersPresent;
    while (true) {
      System.out.println("Does the inputted file have headers? (Y/N)");
      String headers = scanner.nextLine();
      if (headers.equalsIgnoreCase("Y")) {
        headersPresent = Boolean.TRUE;
        break;
      } else if (headers.equalsIgnoreCase("N")) {
        headersPresent = Boolean.FALSE;
        break;
      } else {
        System.out.println("Please respond 'Y' for yes or 'N' for no");
      }
    }

    try {
      // Build Parser/Search Objects and check security clearance
      search = new Search(csvName, headersPresent);
    } catch (FileNotFoundException e) {
      System.err.println(
          "File could not be found. Please ensure that file is in 'data' folder and "
              + "entered in correct format.");
      return;
    } catch (AccessDeniedException e) {
      System.err.println(
          "File exists, but is not accessible to program. Please enter file "
              + "within the 'data' folder");
      return;
    }

    System.out.println(
        "Parsing data ... If row parsing errors are encountered, they will be printed below. "
            + "Rows that cannot be parsed due to invalid row length will not be searchable.\n");

    try {
      search.parseCSV();
    } catch (MalformedRowsException e) {
      System.err.println("The following rows could not be parsed due to invalid length:\n");
      for (List<String> malformedRow : e.getMalformedRows()) {
        System.out.println(malformedRow);
      }
      System.out.println();
      while (true) {
        System.out.println(
            "Would you still like to search data? Rows with invalid length will not be "
                + "searchable. (Y/N)");
        String keepSearching = scanner.nextLine();
        if (keepSearching.equalsIgnoreCase("Y")) {
          break;
        } else if (keepSearching.equalsIgnoreCase("N")) {
          return;
        } else {
          System.out.println("Please respond 'Y' for yes or 'N' for no");
        }
      }
    } catch (IOException e) {
      System.err.println("Encountered error while reading specified file.");
      return;
    } catch (FactoryFailureException e) {
      System.err.println(
          "Encountered object-creation error:"
              + e.getMessage()
              + " while parsing row:"
              + e.getRow());
      return;
    }

    // Query search item
    System.out.println("Please enter the term that you are searching for");
    String toSearch = scanner.nextLine();

    // Query column search
    while (true) {
      System.out.println("Would you like to search by column? (Y/N)");
      String columns = scanner.nextLine();
      if (columns.equalsIgnoreCase("Y")) {
        break;
      } else if (columns.equalsIgnoreCase("N")) {
        System.out.println("Matching rows are: \n");
        List<List<String>> matches = search.searchCSV(toSearch);
        for (List<String> matchRow : matches) {
          System.out.println(matchRow);
        }
        return;
      } else {
        System.out.println("Please respond 'Y' for yes or 'N' for no");
      }
    }

    // Query index/header
    while (true) {
      System.out.print("Would you like to search by header or index? (H/I)");
      String headersOrIndex = scanner.nextLine();
      if (headersOrIndex.equalsIgnoreCase("H")) {
        // Query header
        System.out.print(
            "What header would you like to search by? If multiple columns have the same header, "
                + "the first from the left will be searched.");
        String headerToSearch = scanner.nextLine().toUpperCase();

        try {
          List<List<String>> matches = search.searchCSV(toSearch, headerToSearch);
          System.out.println("Matching rows are: \n");
          for (List<String> matchRow : matches) {
            System.out.println(matchRow);
          }
          return;
        } catch (NoSuchElementException e) { // If header does not exist, re-prompt
          System.out.println("Column does not exist. Please enter existing header or index");
        }
      } else if (headersOrIndex.equalsIgnoreCase("I")) {
        // Query index
        System.out.print("What index would you like to search by?");
        int indexToSearch = scanner.nextInt();
        scanner.nextLine();

        try {
          List<List<String>> matches = search.searchCSV(toSearch, indexToSearch);
          System.out.println("Matching rows are:");
          for (List<String> matchRow : matches) {
            System.out.println(matchRow);
          }
          return;
        } catch (IndexOutOfBoundsException e) { // If index is out-of-bounds, re-prompt
          System.out.println("Column does not exist. Please enter existing header or index");
        }
      } else {
        System.out.println("Please respond 'H' for Headers or 'I' for Index");
      }
    }
  }
}
