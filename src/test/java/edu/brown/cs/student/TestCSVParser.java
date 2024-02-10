package edu.brown.cs.student;

import edu.brown.cs.student.TestCreators.TestArrayCreator;
import edu.brown.cs.student.TestCreators.TestPersonParser.Person;
import edu.brown.cs.student.TestCreators.TestPersonParser.TestPersonCreator;
import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.CSVParser.CreatorFromRow;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Exceptions.MalformedRowsException;
import edu.brown.cs.student.main.SearchUtility.SearchStrategy;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/**
 * This class contains JUnit tests for the CSVParser class. It covers various scenarios for parsing
 * CSV files with and without headers, using different input sources, and testing exceptions in case
 * of inconsistent column counts and parsing errors. The tests also include cases for parsing into
 * different object types.
 */
public class TestCSVParser {

  /**
   * Parses the expected output from a multi-line string and returns a list of lists representing
   * the CSV content. Method uses simple regex, more complex tests conducted manually (e.g., values
   * with commas within quotations)
   *
   * @param input A multi-line string representing the expected CSV content.
   * @return A List<List<String>> representing the parsed CSV content.
   */
  private List<List<String>> parseExpectedOutput(String input) {
    List<List<String>> result = new ArrayList<>();

    String[] lines = input.split("\n");
    for (String line : lines) {
      String[] values = line.split(",");
      result.add(Arrays.asList(values));
    }

    return result;
  }

  /**
   * Test case for parsing a CSV file without headers using a FileReader. It verifies that the
   * CSVParser correctly parses the content and sets the CSV contents and header list accordingly.
   */
  @Test
  public void testParseNoHeadersFileReader()
      throws IOException, MalformedRowsException, FactoryFailureException {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    FileReader noHeadersCSVFileReader = new FileReader("data/testfiles/no_headers_test_file.csv");
    CSVParser<List<String>> csvParser =
        new CSVParser<>(noHeadersCSVFileReader, searchStrategy, false);
    List<List<String>> expectedOutput =
        this.parseExpectedOutput(
            """
                red,orange,yellow
                green,blue,purple
                turquoise,lilac,flame""");

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    csvParser.parse();

    Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());
  }

  /**
   * Test case for parsing a CSV file with headers using a FileReader. It ensures that the CSVParser
   * correctly parses the content and sets both the CSV contents and header list.
   */
  @Test
  public void testParseHeadersFileReader()
      throws IOException, MalformedRowsException, FactoryFailureException {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    FileReader HeadersCSVFileReader = new FileReader("data/testfiles/headers_test_file.csv");
    CSVParser<List<String>> csvParser = new CSVParser<>(HeadersCSVFileReader, searchStrategy, true);
    List<List<String>> expectedOutput =
        this.parseExpectedOutput(
            """
                red,orange,yellow
                green,blue,purple
                turquoise,lilac,flame""");
    ArrayList<String> expectedHeaders = new ArrayList<>();
    expectedHeaders.add("COLOR1");
    expectedHeaders.add("COLOR2");
    expectedHeaders.add("COLOR3");

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    csvParser.parse();

    Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
    Assert.assertEquals(expectedHeaders, csvParser.getHeaderList());
  }

  /**
   * Test case for parsing a CSV file with headers using a StringReader. It verifies that the
   * CSVParser correctly parses the content and sets both the CSV contents and header list.
   */
  @Test
  public void testParseHeadersStringReader()
      throws MalformedRowsException, IOException, FactoryFailureException {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader =
        new StringReader(
            """
                color1,color2,color3
                red,orange,yellow
                green,blue,purple
                turquoise,lilac,flame""");
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, true);
    List<List<String>> expectedOutput =
        this.parseExpectedOutput(
            """
                red,orange,yellow
                green,blue,purple
                turquoise,lilac,flame""");
    ArrayList<String> expectedHeaders = new ArrayList<>();
    expectedHeaders.add("COLOR1");
    expectedHeaders.add("COLOR2");
    expectedHeaders.add("COLOR3");

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    csvParser.parse();

    Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
    Assert.assertEquals(expectedHeaders, csvParser.getHeaderList());
  }

  /**
   * Test case for parsing a CSV file without headers using a StringReader. It ensures that the
   * CSVParser correctly parses the content and sets the CSV contents without a header list.
   */
  @Test
  public void testParseNoHeadersStringReader()
      throws MalformedRowsException, IOException, FactoryFailureException {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader =
        new StringReader(
            """
                red,orange,yellow
                green,blue,purple
                turquoise,lilac,flame""");
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, false);
    List<List<String>> expectedOutput =
        this.parseExpectedOutput(
            """
                red,orange,yellow
                green,blue,purple
                turquoise,lilac,flame""");

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    csvParser.parse();

    Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());
  }

  /**
   * Test case for handling MalformedRowsException when the column count is inconsistent in a CSV
   * file without headers.
   */
  @Test
  public void testParseInconsistentColumnCountExceptionNoHeaders() {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader =
        new StringReader(
            """
                red,orange,yellow
                green,blue
                turquoise,lilac,flame""");
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, false);

    Assert.assertThrows(MalformedRowsException.class, csvParser::parse);
  }

  /**
   * Test case for handling MalformedRowsException when the column count is inconsistent in a CSV
   * file with headers.
   */
  @Test
  public void testParseInconsistentColumnCountExceptionHeaders() {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader =
        new StringReader(
            """
                color1,color2,color3
                red,orange,yellow
                green,blue
                turquoise,lilac,flame""");
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, true);

    Assert.assertThrows(MalformedRowsException.class, csvParser::parse);
  }

  /**
   * Test case for handling MalformedRowsException when the first row is malformed in a CSV file
   * without headers.
   */
  @Test
  public void testParseInconsistentColumnCountExceptionFirstRowMalformed() {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader =
        new StringReader(
            """
                red,orange
                green,blue,purple
                turquoise,lilac,flame""");
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, false);

    Assert.assertThrows(MalformedRowsException.class, csvParser::parse);
  }

  /**
   * Test case for handling MalformedRowsException and parsing a CSV file with inconsistent column
   * count, but ignoring malformed rows, and without headers.
   */
  @Test
  public void testParseInconsistentColumnCountParsingNoHeader()
      throws IOException, FactoryFailureException {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader =
        new StringReader(
            """
                red,orange,yellow
                green,blue
                turquoise,lilac,flame""");
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, false);
    List<List<String>> expectedOutput =
        this.parseExpectedOutput("red,orange,yellow\n" + "turquoise,lilac,flame");

    List<List<String>> expectedMalformedRows = new ArrayList<>();
    expectedMalformedRows.add(new ArrayList<>());
    expectedMalformedRows.get(0).add("green");
    expectedMalformedRows.get(0).add("blue");

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    try {
      csvParser.parse();
    } catch (MalformedRowsException e) {
      Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
      Assert.assertEquals(expectedMalformedRows, e.getMalformedRows());
      Assert.assertNull(csvParser.getHeaderList());
    }
  }

  /**
   * Test case for handling MalformedRowsException and parsing a CSV file with inconsistent column
   * count, but ignoring malformed rows, and with headers.
   */
  @Test
  public void testParseInconsistentColumnCountParsingHeaders()
      throws IOException, FactoryFailureException {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader =
        new StringReader(
            """
                color1,color2,color3
                red,orange,yellow
                green,blue
                turquoise,lilac,flame""");
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, true);
    List<List<String>> expectedOutput =
        this.parseExpectedOutput("red,orange,yellow\n" + "turquoise,lilac,flame");

    List<List<String>> expectedMalformedRows = new ArrayList<>();
    expectedMalformedRows.add(new ArrayList<>());
    expectedMalformedRows.get(0).add("green");
    expectedMalformedRows.get(0).add("blue");

    List<String> expectedHeaderList = new ArrayList<>();
    expectedHeaderList.add("COLOR1");
    expectedHeaderList.add("COLOR2");
    expectedHeaderList.add("COLOR3");

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    try {
      csvParser.parse();
    } catch (MalformedRowsException e) {
      Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
      Assert.assertEquals(expectedMalformedRows, e.getMalformedRows());
      Assert.assertEquals(expectedHeaderList, csvParser.getHeaderList());
    }
  }

  /**
   * Test case for handling MalformedRowsException when the first row is malformed while parsing a
   * CSV file with inconsistent column count, ignoring malformed rows, and without headers.
   */
  @Test
  public void testParseInconsistentColumnCountParsingFirstRowMalformedNoHeaders()
      throws IOException, FactoryFailureException {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader =
        new StringReader(
            """
                red,orange
                green,blue,purple
                turquoise,lilac,flame""");
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, false);
    List<List<String>> expectedOutput = this.parseExpectedOutput("red,orange");

    List<List<String>> expectedMalformedRows = new ArrayList<>();
    expectedMalformedRows.add(new ArrayList<>());
    expectedMalformedRows.add(new ArrayList<>());
    expectedMalformedRows.get(0).add("green");
    expectedMalformedRows.get(0).add("blue");
    expectedMalformedRows.get(0).add("purple");
    expectedMalformedRows.get(1).add("turquoise");
    expectedMalformedRows.get(1).add("lilac");
    expectedMalformedRows.get(1).add("flame");

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    try {
      csvParser.parse();
    } catch (MalformedRowsException e) {
      Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
      Assert.assertEquals(expectedMalformedRows, e.getMalformedRows());
      Assert.assertNull(csvParser.getHeaderList());
    }
  }

  /**
   * Test case for handling MalformedRowsException while parsing a CSV file with inconsistent column
   * count, ignoring malformed rows, and with headers.
   */
  @Test
  public void testParseInconsistentColumnCountParsingFirstRowMalformedHeaders()
      throws IOException, FactoryFailureException {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader =
        new StringReader(
            """
                color1,color2
                red,orange,yellow
                green,blue,purple
                turquoise,lilac,flame""");
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, true);
    List<List<String>> expectedOutput = new ArrayList<>();

    List<List<String>> expectedMalformedRows = new ArrayList<>();
    expectedMalformedRows.add(new ArrayList<>());
    expectedMalformedRows.add(new ArrayList<>());
    expectedMalformedRows.add(new ArrayList<>());
    expectedMalformedRows.get(1).add("green");
    expectedMalformedRows.get(1).add("blue");
    expectedMalformedRows.get(1).add("purple");
    expectedMalformedRows.get(2).add("turquoise");
    expectedMalformedRows.get(2).add("lilac");
    expectedMalformedRows.get(2).add("flame");
    expectedMalformedRows.get(0).add("red");
    expectedMalformedRows.get(0).add("orange");
    expectedMalformedRows.get(0).add("yellow");

    List<String> expectedHeaderList = new ArrayList<>();
    expectedHeaderList.add("COLOR1");
    expectedHeaderList.add("COLOR2");

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    try {
      csvParser.parse();
    } catch (MalformedRowsException e) {
      Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
      Assert.assertEquals(expectedMalformedRows, e.getMalformedRows());
      Assert.assertEquals(expectedHeaderList, csvParser.getHeaderList());
    }
  }

  /**
   * Test case for handling an empty CSV file with headers. It ensures that the CSVParser sets both
   * the CSV contents and header list to empty lists.
   */
  @Test
  public void testNoContentsHeaders()
      throws MalformedRowsException, IOException, FactoryFailureException {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader = new StringReader("");
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, true);
    List<List<String>> expectedOutput = new ArrayList<>();
    List<List<String>> expectedHeaderList = new ArrayList<>();

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    csvParser.parse();

    Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
    Assert.assertEquals(expectedHeaderList, csvParser.getHeaderList());
  }

  /**
   * Test case for handling an empty CSV file without headers. It verifies that the CSVParser sets
   * the CSV contents to an empty list.
   */
  @Test
  public void testNoContentsNoHeaders()
      throws MalformedRowsException, IOException, FactoryFailureException {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader = new StringReader("");
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, false);
    List<List<String>> expectedOutput = new ArrayList<>();

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    csvParser.parse();

    Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());
  }

  /**
   * Test case for handling a CSV file with a single row and headers. It ensures that the CSVParser
   * sets the CSV contents to an empty list and the header list to the expected headers.
   */
  @Test
  public void testOneRowHeaders()
      throws MalformedRowsException, IOException, FactoryFailureException {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader = new StringReader("red,orange,yellow");
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, true);

    List<List<String>> expectedOutput = new ArrayList<>();

    List<String> expectedHeaders = new ArrayList<>();
    expectedHeaders.add("RED");
    expectedHeaders.add("ORANGE");
    expectedHeaders.add("YELLOW");

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    csvParser.parse();

    Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
    Assert.assertEquals(expectedHeaders, csvParser.getHeaderList());
  }

  /**
   * Test case for handling a CSV file with a single row without headers. It verifies that the
   * CSVParser sets the CSV contents to the expected output and the header list to null.
   */
  @Test
  public void testOneRowNoHeaders()
      throws MalformedRowsException, IOException, FactoryFailureException {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader = new StringReader("red,orange,yellow");
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, false);

    List<List<String>> expectedOutput = this.parseExpectedOutput("red,orange,yellow");

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    csvParser.parse();

    Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());
  }

  /**
   * Test case for handling a CSV file with empty columns and whitespace in columns. It verifies
   * that the CSVParser sets the CSV contents to the expected output and the header list to null.
   */
  @Test
  public void testMissingValuesNoHeaders()
      throws MalformedRowsException, IOException, FactoryFailureException {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader =
        new StringReader(
            """
                red,,yellow
                green,blue, ,
                 ,lilac,flame""");
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, false);
    List<List<String>> expectedOutput =
        this.parseExpectedOutput(
            """
                red,,yellow
                green,blue, ,
                 ,lilac,flame""");

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    csvParser.parse();

    Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());
  }

  @Test
  public void testCommasInQuotes() {}

  /**
   * Test case for parsing CSV file rows into arrays of strings without headers. It ensures that the
   * CSVParser sets the CSV contents and header list accordingly.
   */
  @Test
  public void testArrayCreatorObjectNoHeaders()
      throws MalformedRowsException, IOException, FactoryFailureException {
    CreatorFromRow<String[]> arrayStrategy = new TestArrayCreator();
    StringReader stringReader =
        new StringReader(
            """
                red,orange,yellow
                green,blue,purple
                turquoise,lilac,flame""");
    CSVParser<String[]> csvParser = new CSVParser<>(stringReader, arrayStrategy, false);
    List<String[]> expectedOutput = new ArrayList<>();
    expectedOutput.add(new String[] {"red", "orange", "yellow"});
    expectedOutput.add(new String[] {"green", "blue", "purple"});
    expectedOutput.add(new String[] {"turquoise", "lilac", "flame"});

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    csvParser.parse();

    List<String[]> csvOutput = csvParser.getCSVContents();

    Assert.assertEquals(expectedOutput.size(), csvOutput.size());

    for (int x = 0; x < csvOutput.size(); x++) {
      for (int i = 0; i < csvOutput.get(x).length; i++) {
        Assert.assertEquals(expectedOutput.get(x)[i], csvOutput.get(x)[i]);
      }
    }
    Assert.assertNull(csvParser.getHeaderList());
  }

  /**
   * Test case for parsing CSV file rows into arrays of strings with headers. It verifies that the
   * CSVParser sets the CSV contents and header list accordingly.
   */
  @Test
  public void testArrayCreatorObjectHeaders()
      throws MalformedRowsException, IOException, FactoryFailureException {
    CreatorFromRow<String[]> arrayStrategy = new TestArrayCreator();
    StringReader stringReader =
        new StringReader(
            """
                color1,color2,color3
                red,orange,yellow
                green,blue,purple
                turquoise,lilac,flame""");
    CSVParser<String[]> csvParser = new CSVParser<>(stringReader, arrayStrategy, true);

    List<String[]> expectedOutput = new ArrayList<>();
    expectedOutput.add(new String[] {"red", "orange", "yellow"});
    expectedOutput.add(new String[] {"green", "blue", "purple"});
    expectedOutput.add(new String[] {"turquoise", "lilac", "flame"});

    List<String> expectedHeaderList = new ArrayList<>();
    expectedHeaderList.add("COLOR1");
    expectedHeaderList.add("COLOR2");
    expectedHeaderList.add("COLOR3");

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    csvParser.parse();

    List<String[]> csvOutput = csvParser.getCSVContents();

    Assert.assertEquals(expectedOutput.size(), csvOutput.size());

    for (int x = 0; x < csvOutput.size(); x++) {
      for (int i = 0; i < csvOutput.get(x).length; i++) {
        Assert.assertEquals(expectedOutput.get(x)[i], csvOutput.get(x)[i]);
      }
    }
    Assert.assertEquals(expectedHeaderList, csvParser.getHeaderList());
  }

  /**
   * Test case for parsing CSV file rows into a custom object (Person class) with headers. It
   * ensures that the CSVParser sets the CSV contents and header list accordingly.
   */
  @Test
  public void testParseIntoObject()
      throws MalformedRowsException, IOException, FactoryFailureException {
    CreatorFromRow<Person> arrayStrategy = new TestPersonCreator();
    StringReader stringReader =
        new StringReader("""
            name,age
            harry,12
            jenny,54""");
    CSVParser<Person> csvParser = new CSVParser<>(stringReader, arrayStrategy, true);

    List<Person> expectedOutput = new ArrayList<>();
    expectedOutput.add(new Person("harry", 12));
    expectedOutput.add(new Person("jenny", 54));

    List<String> expectedHeaderList = new ArrayList<>();
    expectedHeaderList.add("NAME");
    expectedHeaderList.add("AGE");

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    csvParser.parse();

    Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
    Assert.assertEquals(expectedHeaderList, csvParser.getHeaderList());
  }

  /**
   * Test case for handling FactoryFailureException when parsing CSV file rows into custom objects
   * (Person class) with headers and encountering a parsing error.
   */
  @Test
  public void testParseIntoObjectException() {
    CreatorFromRow<Person> arrayStrategy = new TestPersonCreator();
    StringReader stringReader =
        new StringReader(
            """
                name,age
                harry,harrison
                jenny,jennifer""");
    CSVParser<Person> csvParser = new CSVParser<>(stringReader, arrayStrategy, true);

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    Assert.assertThrows(FactoryFailureException.class, csvParser::parse);
  }

  /*
  Below is a test exemplifying one of the shortcomings of the provided regex; it only considers
  double-quotes as value delimiters, when brackets, single quotes, and colons are also regularly
  used.
   */

  /*
  @Test
  public void testRegex() throws MalformedRowsException, IOException, FactoryFailureException {
    CreatorFromRow<List<String>> searchStrategy = new SearchStrategy();
    StringReader stringReader =
        new StringReader(
            """
                ID,Name,Details
                1,John,{"Age": 25, "City": "New York"}
                2,Alice,{"Age": 30, "City": "San Francisco"}
                3,Bob,{"Age": 22, "City": "Seattle"}
                """);
    CSVParser<List<String>> csvParser = new CSVParser<>(stringReader, searchStrategy, true);
    List<List<String>> expectedOutput = new ArrayList<>();
    expectedOutput.add(new ArrayList<>());
    expectedOutput.add(new ArrayList<>());
    expectedOutput.add(new ArrayList<>());
    expectedOutput.get(0).add("1");
    expectedOutput.get(0).add("John");
    expectedOutput.get(0).add("{\"Age\": 25, \"City\": \"New York\"}");
    expectedOutput.get(0).add("2");
    expectedOutput.get(0).add("Alice");
    expectedOutput.get(0).add("{\"Age\": 30, \"City\": \"San Francisco\"}");
    expectedOutput.get(0).add("3");
    expectedOutput.get(0).add("Bob");
    expectedOutput.get(0).add("{\"Age\": 22, \"City\": \"Seattle\"}");

    List<String> expectedHeaderList = new ArrayList<>();
    expectedHeaderList.add("ID");
    expectedHeaderList.add("NAME");
    expectedHeaderList.add("DETAILS");

    Assert.assertNull(csvParser.getCSVContents());
    Assert.assertNull(csvParser.getHeaderList());

    csvParser.parse();

    Assert.assertEquals(expectedOutput, csvParser.getCSVContents());
    Assert.assertEquals(expectedHeaderList, csvParser.getHeaderList());
   }
   */
}
