import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.*;
import edu.brown.cs.student.main.Searcher;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestSuite {
  /**
   * This test checks that an empty file is not parsed into something.
   *
   * @throws FileNotFoundException
   */
  @Test
  public void testParseEmpty() throws FileNotFoundException {
    String filePath = "/Users/jowet/Desktop/cs320/csv-jowethaile/data/emptyFile.csv";
    FileReader fileReader = new FileReader(filePath);
    StringCreator stringCreator = new StringCreator();
    CSVParser csvParser = new CSVParser(stringCreator, fileReader);
    assertEquals(csvParser.parse().size(), 0);
  }

  /**
   * This method is used to check the size of the parsed data, so I used the postsecondary-education
   * which has 17 lines so the size of the parser should be 17.
   *
   * @throws FileNotFoundException
   */
  @Test
  public void testParseNonEmpty() throws FileNotFoundException {
    String filePath =
        "/Users/jowet/Desktop/cs320/csv-jowethaile/data/census/postsecondary_education.csv";

    FileReader fileReader = new FileReader(filePath);
    StringCreator stringCreator = new StringCreator();
    CSVParser csvParser = new CSVParser(stringCreator, fileReader);
    assertEquals(
        csvParser.parse().size(), 17); // check that its parsing into the right amount of elm
  }

  /**
   * This test makes sure the parser is working correctly by checking the position of the values are
   * in the correct spot.
   *
   * @throws FileNotFoundException
   */
  @Test
  public void testParserValueSmall() throws FileNotFoundException {

    String filePath = "/Users/jowet/Desktop/cs320/csv-jowethaile/data/stars/ten-star.csv";
    FileReader fileReader = new FileReader(filePath);
    StringCreator stringCreator = new StringCreator();
    CSVParser csvParser = new CSVParser(stringCreator, fileReader);
    List<String> row = (List<String>) csvParser.parse().get(0); // Make sure to use the correct type
    String expectedValue = "StarID"; // Replace with the expected value
    assertEquals(expectedValue, row.get(0)); // Adjust the index based on your actual test
  }

  /**
   * This test checks object equality- that when I use a different type of object, for example I
   * created a pizza creator class that implements the creator from row class. I used this to make
   * sure that my parser is able to handle different object types by checking that it is parsing the
   * data as a list of pizzas.
   */
  @Test
  public void testParsePizzaObj() {

    String filePath = "/Users/jowet/Desktop/cs320/csv-jowethaile/data/pizza.csv";

    try (FileReader fileReader = new FileReader(filePath)) {
      PizzaCreator pizzaCreator = new PizzaCreator();
      ;
      CSVParser csvParser = new CSVParser(pizzaCreator, fileReader);
      List<Pizza> listPizza = new ArrayList<Pizza>();
      listPizza.add(new Pizza("cheese", "red", "cheddar", "1", "42"));
      assertEquals(listPizza.getClass(), csvParser.parse().getClass());
    } catch (IOException e) {
      System.err.println("File not found");
    }
  }

  /**
   * @throws FileNotFoundException
   */
//      @Test
//      public void testParseNoFile() throws FileNotFoundException {
//
//
//
//          String filePath = "/Users/jowet/Desktop/cs320/csv-jowethaile/data/nonexistent.csv";
//          FileReader fileReader = new FileReader(filePath);
//          StringCreator stringCreator = new StringCreator();
//
//          CSVParser csvParser = new CSVParser(stringCreator, fileReader);
//          assertThrows(FileNotFoundException.class, () -> csvParser.parse(), "expected
//   FileNotFoundException");
//
//      }

  /** This test just ensures that my searcher is working correctly on a small dataset. */
  @Test
  public void testSearchBasicSmallData() {
    String filePath = "/Users/jowet/Desktop/cs320/csv-jowethaile/data/stars/ten-star.csv";
    try (FileReader fileReader = new FileReader(filePath)) {

      StringCreator stringCreator = new StringCreator();
      CSVParser csvParser = new CSVParser(stringCreator, fileReader);
      Searcher searcher = new Searcher(csvParser, "Sol", false, -1, "none");
      String result = searcher.search().toString();
      String actual = "[[0, Sol, 0, 0, 0]]";
      assertEquals(result, actual);
    } catch (IOException e) {
      System.err.println("File not found");
    }
  }

  /**
   * This test checks that when a column number is inserted on a small data set the searcher handles
   * it well.
   */
  @Test
  public void testColNumBasic() {
    String filePath = "/Users/jowet/Desktop/cs320/csv-jowethaile/data/stars/ten-star.csv";
    try (FileReader fileReader = new FileReader(filePath)) {

      StringCreator stringCreator = new StringCreator();
      CSVParser csvParser = new CSVParser(stringCreator, fileReader);
      Searcher searcher = new Searcher(csvParser, "-1.15037", false, 4, "none");
      String result = searcher.search().toString();
      String actual = "[[70667, Proxima Centauri, -0.47175, -0.36132, -1.15037]]";
      assertEquals(result, actual);
    } catch (IOException e) {
      System.err.println("File not found");
    }
  }

  /**
   * This test checks that when a column name is inserted on a small data set that it returns the
   * correct value. Displaying that values can also be located by checking column names.
   */
  @Test
  public void testColNameBasic() {
    String filePath = "/Users/jowet/Desktop/cs320/csv-jowethaile/data/stars/ten-star.csv";
    try (FileReader fileReader = new FileReader(filePath)) {

      StringCreator stringCreator = new StringCreator();
      CSVParser csvParser = new CSVParser(stringCreator, fileReader);
      Searcher searcher = new Searcher(csvParser, "-15.24144", false, -1, "Z");
      String result = searcher.search().toString().replaceAll("\\s", "");
      String actual = "[[2,,43.04329,0.00285,-15.24144]]";
      assertEquals(result, actual);
    } catch (IOException e) {
      System.err.println("File not found");
    }
  }

  /**
   * This just ensures my program's functionality on a large data set, that it can still handle all
   * the lines.
   */
  @Test
  public void testLargeData() {
    String filePath = "/Users/jowet/Desktop/cs320/csv-jowethaile/data/stars/stardata.csv";
    try (FileReader fileReader = new FileReader(filePath)) {

      StringCreator stringCreator = new StringCreator();
      CSVParser csvParser = new CSVParser(stringCreator, fileReader);
      Searcher searcher = new Searcher(csvParser, "Josiah", false, -1, "none");
      String result = searcher.search().toString().replaceAll("\\s", "");
      String actual = "[[131,Josiah,57.85193,0.42402,108.88979]]";
      assertEquals(result, actual);
    } catch (IOException e) {
      System.err.println("File not found");
    }
  }

  /** This test that even data is malformed it still returns the desired row. */
  @Test
  public void testMalformed() {
    String filePath =
        "/Users/jowet/Desktop/cs320/csv-jowethaile/data/malformed/malformed_signs.csv";
    try (FileReader fileReader = new FileReader(filePath)) {

      StringCreator stringCreator = new StringCreator();
      CSVParser csvParser = new CSVParser(stringCreator, fileReader);
      Searcher searcher = new Searcher(csvParser, "Roberto", false, -1, "none");
      String result = searcher.search().toString().replaceAll("\\s", "");
      String actual = "[[Gemini,Roberto,Nick]]";
      assertEquals(result, actual);

    } catch (IOException e) {
      System.err.println("File not found");
    }
  }

  /**
   * This test checks when a value is present in multiple rows that they are all returned when
   * searched.
   */
  @Test
  public void testMultirows() {
    String filePath =
        "/Users/jowet/Desktop/cs320/csv-jowethaile/data/census/postsecondary_education.csv";
    try (FileReader fileReader = new FileReader(filePath)) {

      StringCreator stringCreator = new StringCreator();
      CSVParser csvParser = new CSVParser(stringCreator, fileReader);
      Searcher searcher = new Searcher(csvParser, "Women", false, -1, "none");
      String result = searcher.search().toString().replaceAll("\\s", "");

      String actual =
          "[[Asian,2020,2020,217156,BrownUniversity,235,brown-university,0.076027176,Women,2],[BlackorAfricanAmerican,2020,2020,217156,BrownUniversity,95,brown-university,0.03073439,Women,2],[NativeHawaiianorOtherPacificIslanders,2020,2020,217156,BrownUniversity,4,brown-university,0.00129408,Women,2],[HispanicorLatino,2020,2020,217156,BrownUniversity,207,brown-university,0.066968619,Women,2],[TwoorMoreRaces,2020,2020,217156,BrownUniversity,85,brown-university,0.027499191,Women,2],[AmericanIndianorAlaskaNative,2020,2020,217156,BrownUniversity,7,brown-university,0.002264639,Women,2],[Non-residentAlien,2020,2020,217156,BrownUniversity,281,brown-university,0.090909091,Women,2],[White,2020,2020,217156,BrownUniversity,660,brown-university,0.213523132,Women,2]]";
      assertEquals(result, actual);

    } catch (IOException e) {
      System.err.println("File not found");
    }
  }
  //    @Test
  //    public void testQuotes(){
  //            String filePath =
  // "/Users/jowet/Desktop/cs320/csv-jowethaile/data/census/income_by_race.csv";
  //            try(FileReader fileReader = new FileReader(filePath)) {
  //
  //                StringCreator stringCreator = new StringCreator();
  //                CSVParser csvParser = new CSVParser(stringCreator, fileReader);
  //                Searcher searcher = new Searcher(csvParser, "Dekalb County, GA", false, -1,
  // "none" );
  //                String result = searcher.search().toString().replaceAll("\\s", "");
  //                String actual = "[[6,Other,2020,2020,29375,2225,\"Dekalb County,
  // GA\",05000US44005,newport-county-ri]]";
  //
  //                assertEquals(result, actual);
  //
  //            } catch(IOException e){
  //                System.err.println("File not found");
  //            }
  //        }

}
