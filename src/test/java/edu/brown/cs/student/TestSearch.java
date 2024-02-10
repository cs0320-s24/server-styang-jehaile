package edu.brown.cs.student;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Exceptions.MalformedRowsException;
import edu.brown.cs.student.main.SearchUtility.Search;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class TestSearch {

  /**
   * Parses the expected output from a multi-line string and returns a list of lists representing
   * the matches that search returns.
   *
   * @param input A multi-line string representing the expected return.
   * @return A List<List<String>> representing the matching rows.
   */
  private List<List<String>> parseExpectedMatches(String input) {
    List<List<String>> result = new ArrayList<>();

    String[] lines = input.split("\n");
    for (String line : lines) {
      String[] values = line.split(",");
      result.add(Arrays.asList(values));
    }

    return result;
  }

  /** Tests the case where an item is present in the CSV file without specifying a column. */
  @Test
  public void testItemPresentNoColumn()
      throws IOException, MalformedRowsException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches =
        this.parseExpectedMatches(
            """
                green,blue,purple
                purple,lilac,flame""");

    Assert.assertEquals(testSearch.searchCSV("purple"), expectedMatches);
  }

  /** Tests the case where an item is present in the header of the CSV file. */
  @Test
  public void testItemPresentInHeader()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches = new ArrayList<>();

    Assert.assertEquals(testSearch.searchCSV("color1"), expectedMatches);
  }

  /** Tests searching for a substring in the CSV file. */
  @Test
  public void testSubstring() throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches =
        this.parseExpectedMatches(
            """
                red,orange,yellow
                green,blue,purple""");

    Assert.assertEquals(testSearch.searchCSV("re"), expectedMatches);
  }

  /** Tests case insensitivity in search. */
  @Test
  public void testCaseInsensitivity()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches =
        this.parseExpectedMatches(
            """
                green,blue,purple
                purple,lilac,flame
                """);

    Assert.assertEquals(testSearch.searchCSV("PURPLE"), expectedMatches);
  }

  /** Tests the case where an item is not present in the CSV file without specifying a column. */
  @Test
  public void testItemNotPresentNoColumn()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches = new ArrayList<>();

    Assert.assertEquals(testSearch.searchCSV("lavender"), expectedMatches);
  }

  /** Tests searching for an item in a specific header column. */
  @Test
  public void testItemPresentHeaderSearch()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches =
        this.parseExpectedMatches("""
                green,blue,purple
                """);

    Assert.assertEquals(testSearch.searchCSV("purple", "color3"), expectedMatches);
  }

  /** Tests case insensitivity in header search (header being case-insensitive) */
  @Test
  public void testHeaderSearchCaseInsensitivity()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches =
        this.parseExpectedMatches("""
                green,blue,purple
                """);

    Assert.assertEquals(testSearch.searchCSV("purple", "COLOR3"), expectedMatches);
  }

  /**
   * Tests the case where an item is present in the CSV file, but under a different header column
   * than specified.
   */
  @Test
  public void testItemPresentDifferentHeader()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches = new ArrayList<>();

    Assert.assertEquals(testSearch.searchCSV("blue", "color3"), expectedMatches);
  }

  /** Tests the case where an item is not present in the CSV file with a specific header column. */
  @Test
  public void testItemNotPresentHeader()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches = new ArrayList<>();

    Assert.assertEquals(testSearch.searchCSV("lavender", "color2"), expectedMatches);
  }

  /** Tests searching with a non-existent header. */
  @Test
  public void testHeaderNonExistent()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();

    Assert.assertThrows(
        NoSuchElementException.class, () -> testSearch.searchCSV("purple", "color4"));
  }

  /** Tests searching for an item by index. */
  @Test
  public void testItemPresentIndex()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches =
        this.parseExpectedMatches("""
                purple,lilac,flame
                """);

    Assert.assertEquals(testSearch.searchCSV("purple", 0), expectedMatches);
  }

  /** Tests searching for an item by an index that it is not in. */
  @Test
  public void testItemPresentDifferentIndex()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches = new ArrayList<>();

    Assert.assertEquals(testSearch.searchCSV("blue", 2), expectedMatches);
  }

  /** Tests searching for an item that is not present by index. */
  @Test
  public void testItemNotPresentIndex()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches = new ArrayList<>();

    Assert.assertEquals(testSearch.searchCSV("lavender", 1), expectedMatches);
  }

  /** Tests an index out of bounds that is too large. */
  @Test
  public void testIndexOutOfBoundsTooLarge()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();

    Assert.assertThrows(IndexOutOfBoundsException.class, () -> testSearch.searchCSV("purple", 3));
  }

  /** Tests a negative index. */
  @Test
  public void testIndexNegative()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("search_test_file_no_subdir.csv", true);
    testSearch.parseCSV();

    Assert.assertThrows(IndexOutOfBoundsException.class, () -> testSearch.searchCSV("purple", -1));
  }

  /** Tests injection defense when attempting to access an inaccessible file. */
  @Test
  public void testInjectionDefense() {
    Assert.assertThrows(
        AccessDeniedException.class, () -> new Search("../inaccessibledata.csv", true));
  }

  /** Tests file that is not stored within data folder, but outside. */
  @Test
  public void testFileExistsOutsideFolder() {
    Assert.assertThrows(
        FileNotFoundException.class, () -> new Search("inaccessibledata.csv", true));
  }

  /** Tests searching in a file located in a subfolder within the data folder. */
  @Test
  public void testFileInSubfolder()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("testfiles/headers_test_file.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches =
        this.parseExpectedMatches("""
                green,blue,purple
                """);

    Assert.assertEquals(testSearch.searchCSV("purple"), expectedMatches);
  }

  /** Tests searching for a non-existent file. */
  @Test
  public void testFileDoesNotExist() {
    Assert.assertThrows(FileNotFoundException.class, () -> new Search("nonexistent.csv", true));
  }

  /** Tests searching for whitespace in the CSV file. */
  @Test
  public void testSearchWhitespace()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("testfiles/test_whitespace.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches =
        this.parseExpectedMatches(
            """
                A,B, ,D
                I, ,K,L
                """);

    Assert.assertEquals(testSearch.searchCSV(" "), expectedMatches);
  }

  /** Tests searching for an empty string in the CSV file. */
  @Test
  public void testSearchEmptyString()
      throws MalformedRowsException, IOException, FactoryFailureException {
    Search testSearch = new Search("testfiles/test_whitespace.csv", true);
    testSearch.parseCSV();
    List<List<String>> expectedMatches =
        this.parseExpectedMatches(
            """
                A,B, ,D
                E,F,G,H
                I, ,K,L
                M,N,O,P
                """);

    Assert.assertEquals(testSearch.searchCSV(""), expectedMatches);
  }
}
