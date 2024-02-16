package ServerTestSuite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.main.Server.Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * This is our testing suite for the CSV server classes and methods. Within this class we test the
 * LoadCSVHandler, SearchCSVHandler and ViewCSVHandler by running their respective endpoints and
 * ensuring that they expect accordingly. This test class starts the server locally, sends web
 * requests and evaluates the response.
 */
public class ServerCSVIntegrationTestSuite {

  private static Thread serverThread;

  /**
   * This method sets up the server thread before any of the tests are run. This also sleeps so that
   * interference is reduced.
   */
  @BeforeAll
  public static void setUp() {
    // Start the server in a new thread
    serverThread =
        new Thread(
            () -> {
              try {
                Server.main(new String[] {});

              } catch (Exception e) {
                e.printStackTrace();
              }
            });
    serverThread.start();
    // Wait for the server to start (optional)
    try {
      Thread.sleep(2000); // Wait for 2 seconds to let the server start
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /** This method stops the server after the tests are run. */
  @AfterAll
  public static void tearDown() {
    // Stop the server
    serverThread.interrupt();
  }

  /**
   * This test runs the endpoint for loading a csv works by checking the response code.
   *
   * @throws IOException thrown if there are errors loading file, establishing the connection,
   *     reading through buffer reader
   */
  @Test
  public void testLoadCSV() throws IOException {
    URL url = new URL("http://localhost:1234/loadcsv");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    int responseCode = connection.getResponseCode();
    assertEquals(200, responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
  }

  /**
   * This tests the view csv endpoint by checking the response code.
   *
   * @throws IOException throw if there is an issue viewing the file.
   */
  @Test
  public void testViewCSV() throws IOException {

    URL url = new URL("http://localhost:1234/viewcsv");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    int responseCode = connection.getResponseCode();
    assertEquals(200, responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
  }

  /**
   * Tests that load csv displays the response that a file was successfully loaded.
   *
   * @throws IOException thrown if there are errors loading file, establishing the connection,
   *     reading through buffer reader
   */
  @Test
  public void testLoadCSVResponse() throws IOException {
    String csvFileName = "ten-star.csv";
    URL url = new URL("http://localhost:1234/loadcsv?fileName=" + csvFileName + "&headers=true");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    int responseCode = connection.getResponseCode();
    assertEquals(200, responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    assertTrue(response.toString().contains("Loaded successfully!"));
  }

  /**
   * Tests that search csv has the correct response code.
   *
   * @throws IOException thrown if there are errors loading/searching file, establishing the
   *     connection, reading through buffer reader
   */
  @Test
  public void testSearchCSV() throws IOException {
    URL url = new URL("http://localhost:1234/searchcsv");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    int responseCode = connection.getResponseCode();
    assertEquals(200, responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
  }

  /**
   * This test ensures that a user can not view a file that has been loaded by checking the string.
   *
   * @throws IOException thrown if there are issues viewing the file, using the reader or
   *     establishing the connection
   */
  @Test
  public void testViewBeforeLoad() throws IOException {

    String csvFileName = "dol_ri_earnings_disparity.csv";
    URL url = new URL("http://localhost:1234/viewcsv?fileName=" + csvFileName);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    int responseCode = connection.getResponseCode();
    assertEquals(200, responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    assertTrue(response.toString().contains("Failed to view. Please load CSV file."));
  }

  /**
   * This tests that a file can not be searched without being loaded
   *
   * @throws IOException thrown if there are issues establishing a connection, using the reader to
   *     search
   */
  @Test
  public void testSearchBeforeLoad() throws IOException {

    String csvFileName = "dol_ri_earnings_disparity.csv";
    URL url = new URL("http://localhost:1234/searchcsv?fileName=" + csvFileName);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    int responseCode = connection.getResponseCode();
    assertEquals(200, responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    System.out.print(response.toString());

    assertTrue(response.toString().contains("CSV File not loaded."));
  }

  /**
   * This tests the contents of the view csv by checking that it contains the correct values.
   *
   * @throws IOException thrown if there are errors loading the file, viewing the file or
   *     establishing a connection
   * @throws InterruptedException thrown if the thread.sleep method fails to execute
   */
  @Test
  public void testContentsOfViewCSV() throws IOException, InterruptedException {
    String csvFileName = "ten-star.csv";

    URL loadUrl = new URL("http://localhost:1234/loadcsv?fileName=" + csvFileName + "&headers=true");
    HttpURLConnection loadConnection = (HttpURLConnection) loadUrl.openConnection();
    loadConnection.setRequestMethod("GET");
    assertEquals(200, loadConnection.getResponseCode());

    Thread.sleep(3000);

    URL viewUrl = new URL("http://localhost:1234/viewcsv?fileName=" + csvFileName);
    HttpURLConnection viewConnection = (HttpURLConnection) viewUrl.openConnection();
    viewConnection.setRequestMethod("GET");

    int responseCode = viewConnection.getResponseCode();
    assertEquals(200, responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(viewConnection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    System.out.print(response.toString());

    assertTrue(response.toString().contains("STARID"));
    assertTrue(response.toString().contains("PROPERNAME"));
    assertTrue(response.toString().contains("X"));
    assertTrue(response.toString().contains("Y"));
    assertTrue(response.toString().contains("Z"));
    assertTrue(response.toString().contains("Sol"));
    assertTrue(response.toString().contains("Proxima Centauri"));
    assertTrue(response.toString().contains("-0.01729"));
    assertTrue(response.toString().contains("[\"2\",\"\",\"43.04329\",\"0.00285\",\"-15.24144\"]"));
  }

  /**
   * Similar to the previous test this handles displaying the contents of a larger data file to the
   * user to check that our program also works with large datasets.
   *
   * @throws IOException thrown if there are errors loading the file, viewing the file or
   *     establishing a connection
   * @throws InterruptedException thrown if the thread.sleep method fails to execute
   */
  @Test
  public void testContentsOfViewCSVLargeData() throws IOException, InterruptedException {
    String csvFileName = "income_by_race.csv";

    URL loadUrl = new URL("http://localhost:1234/loadcsv?fileName=" + csvFileName + "&headers=true");
    HttpURLConnection loadConnection = (HttpURLConnection) loadUrl.openConnection();
    loadConnection.setRequestMethod("GET");
    assertEquals(200, loadConnection.getResponseCode());

    Thread.sleep(3000);

    URL viewUrl = new URL("http://localhost:1234/viewcsv?fileName=" + csvFileName);
    HttpURLConnection viewConnection = (HttpURLConnection) viewUrl.openConnection();
    viewConnection.setRequestMethod("GET");

    int responseCode = viewConnection.getResponseCode();
    assertEquals(200, responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(viewConnection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    System.out.print(response.toString());

    assertTrue(response.toString().contains("Asian"));
    assertFalse(response.toString().contains("Simon"));
    assertTrue(response.toString().contains("washington-county-ri"));
    assertTrue(
        response
            .toString()
            .contains(
                "[\"0\",\"Total\",\"2020\",\"2020\",\"84282\",\"2629\",\"\\\"Newport County, RI\\\"\",\"05000US44005\",\"newport-county-ri\"]"));
  }

  /**
   * This test checks the contents of the search csv to ensure that user is receiving the correct
   * information when search through the data from the value they input.
   *
   * @throws IOException thrown if there are errors loading the file, searching the file, using the
   *     reader or establishing a connection
   * @throws InterruptedException thrown if the thread.sleep method fails to execute
   */
  @Test
  public void testContentsOfSearchCSV() throws IOException, InterruptedException {
    String csvFileName = "ten-star.csv";

    URL loadUrl = new URL("http://localhost:1234/loadcsv?fileName=" + csvFileName + "&headers=true");
    HttpURLConnection loadConnection = (HttpURLConnection) loadUrl.openConnection();
    loadConnection.setRequestMethod("GET");
    assertEquals(200, loadConnection.getResponseCode());

    Thread.sleep(3000);
    String value = "Barnard%27s%20Star";

    URL viewUrl =
        new URL("http://localhost:1234/searchcsv?fileName=" + csvFileName + "&toSearch=" + value);
    HttpURLConnection viewConnection = (HttpURLConnection) viewUrl.openConnection();
    viewConnection.setRequestMethod("GET");

    int responseCode = viewConnection.getResponseCode();
    assertEquals(200, responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(viewConnection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    System.out.print(response.toString());

    assertTrue(response.toString().contains("Barnard's Star"));
    assertTrue(
        response
            .toString()
            .contains(
                "{\"responseType\":\"Success\",\"responseMap\":{\"Matches:\":[[\"87666\",\"Barnard's Star\",\"-0.01729\",\"-1.81533\",\"0.14824\"]]}}"));
    assertFalse(response.toString().contains("Jowet"));
  }

  /**
   * This tests that if a value is not present in the data set no matches are shown.
   *
   * @throws IOException thrown if there are errors loading the file, searching the file, using the
   *     reader or establishing a connection
   * @throws InterruptedException thrown if the thread.sleep method fails to execute
   */
  @Test
  public void testSearchCSVInvalids() throws IOException, InterruptedException {
    String csvFileName = "ri_city_and_town_income.csv";

    URL loadUrl = new URL("http://localhost:1234/loadcsv?fileName=" + csvFileName + "&headers=true");
    HttpURLConnection loadConnection = (HttpURLConnection) loadUrl.openConnection();
    loadConnection.setRequestMethod("GET");
    assertEquals(200, loadConnection.getResponseCode());

    Thread.sleep(3000);
    String value = "Hello";

    URL viewUrl =
        new URL("http://localhost:1234/searchcsv?fileName=" + csvFileName + "&toSearch=" + value);
    HttpURLConnection viewConnection = (HttpURLConnection) viewUrl.openConnection();
    viewConnection.setRequestMethod("GET");

    int responseCode = viewConnection.getResponseCode();
    assertEquals(200, responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(viewConnection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    System.out.print(response.toString());
    assertEquals(
        response.toString(), "{\"responseType\":\"Success\",\"responseMap\":{\"Matches:\":[]}}");
  }

  /**
   * This test that search works while using header names as well by checking the contents of the
   * user's value
   *
   * @throws IOException thrown if there are errors loading the file, searching the file, using the
   *     reader or establishing a connection
   * @throws InterruptedException thrown if there are issues with the thread sleeping
   */
  @Test
  public void testSearchCSVHeader() throws IOException, InterruptedException {
    String csvFileName = "ri_city_and_town_income.csv";

    URL loadUrl = new URL("http://localhost:1234/loadcsv?fileName=" + csvFileName + "&headers=true");
    HttpURLConnection loadConnection = (HttpURLConnection) loadUrl.openConnection();
    loadConnection.setRequestMethod("GET");
    assertEquals(200, loadConnection.getResponseCode());

    Thread.sleep(3000);
    String value = "Burrillville";
    String header = "Median%20Household%20Income";

    URL viewUrl =
        new URL(
            "http://localhost:1234/searchcsv?fileName="
                + csvFileName
                + "&toSearch="
                + value
                + "&headerName"
                + header);
    HttpURLConnection viewConnection = (HttpURLConnection) viewUrl.openConnection();
    viewConnection.setRequestMethod("GET");

    int responseCode = viewConnection.getResponseCode();
    assertEquals(200, responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(viewConnection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    System.out.print(response.toString());

    assertTrue(
        response
            .toString()
            .contains(
                "[[\"Burrillville\",\"\\\"96,824.00\\\"\",\"\\\"109,340.00\\\"\",\"\\\"39,470.00\\\"\"]"));
    assertTrue(response.toString().contains("Burrillville"));
  }

  /**
   * This test searching while using the column index integer so that the user can search columns by
   * checking what the program displays.
   *
   * @throws IOException thrown if there are errors loading (parsing) or searching the file.
   * @throws InterruptedException thrown if the thread.sleep method fails to execute
   */
  @Test
  public void testSearchCSVColumnIndex() throws IOException, InterruptedException {
    String csvFileName = "ri_city_and_town_income.csv";
    URL loadUrl = new URL("http://localhost:1234/loadcsv?fileName=" + csvFileName + "&headers=true");
    HttpURLConnection loadConnection = (HttpURLConnection) loadUrl.openConnection();
    loadConnection.setRequestMethod("GET");
    assertEquals(200, loadConnection.getResponseCode());

    Thread.sleep(3000);
    String value = "114,202.00";
    String index = "2";

    URL viewUrl =
        new URL(
            "http://localhost:1234/searchcsv?fileName="
                + csvFileName
                + "&toSearch="
                + value
                + "&columnIndex"
                + index);
    HttpURLConnection viewConnection = (HttpURLConnection) viewUrl.openConnection();
    viewConnection.setRequestMethod("GET");

    int responseCode = viewConnection.getResponseCode();
    assertEquals(200, responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(viewConnection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    System.out.print(response.toString());
    assertTrue(response.toString().contains("114,202.00"));
    assertTrue(
        response
            .toString()
            .contains(
                "\"responseMap\":{\"Matches:\":[[\"South Kingstown\",\"\\\"102,242.00\\\"\",\"\\\"114,202.00\\\"\",\"\\\"42,080.00\\\"\"]]}"));
  }
}
