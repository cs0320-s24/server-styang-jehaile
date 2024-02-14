package ServerTestSuite;

import edu.brown.cs.student.main.Server.CSV.LoadCSVHandler;
import edu.brown.cs.student.main.Server.Server;
import org.eclipse.jetty.http.MetaData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerTestSuite {
  // Write a test class that starts up your server locally, sends it web requests, and evaluates the
  // response.
  // (This will all be done on your local computer; no internet connection needed.)
//  @BeforeAll
//  public static void setUp() {
//      RestAssured.baseURI = "http://localhost";
//      RestAssured.port = 1234;
//  }
  private static Thread serverThread;

    @BeforeAll
    public static void setUp() {
        // Start the server in a new thread
        serverThread = new Thread(() -> {
            try {
                Server.main(new String[]{});

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

    @AfterAll
    public static void tearDown() {
        // Stop the server
        serverThread.interrupt();
    }

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
    @Test
    public void testViewBeforeLoad() throws IOException {

        String csvFileName = "ten-star.csv";
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
        System.out.print(response.toString());

        assertTrue(response.toString().contains("Failed to view. Please load CSV file."));
        }
    @Test
    public void testSearchBeforeLoad() throws IOException {

        String csvFileName = "ten-star.csv";
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
    @Test
    public void testContentsOfViewCSV() throws IOException, InterruptedException {
        String csvFileName = "ten-star.csv";

        URL loadUrl = new URL("http://localhost:1234/loadcsv?fileName=" + csvFileName);
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

        //System.out.println("Response: " + response.toString());

        assertTrue(response.toString().contains("StarID"));
        assertTrue(response.toString().contains("ProperName"));
        assertTrue(response.toString().contains("X"));
        assertTrue(response.toString().contains("Y"));
        assertTrue(response.toString().contains("Z"));
        assertTrue(response.toString().contains("Sol"));
        assertTrue(response.toString().contains("Proxima Centauri"));
        assertTrue(response.toString().contains("-0.01729"));
        assertTrue(response.toString().contains("\"Data\":[[\"StarID\",\"ProperName\",\"X\",\"Y\",\"Z\"]"));
    }



    public void testContentsOfViewCSVLargeData() throws IOException, InterruptedException {
        String csvFileName = "ten-star.csv";

        URL loadUrl = new URL("http://localhost:1234/loadcsv?fileName=" + csvFileName);
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

        //System.out.println("Response: " + response.toString());

        assertTrue(response.toString().contains("StarID"));
        assertTrue(response.toString().contains("ProperName"));
        assertTrue(response.toString().contains("X"));
        assertTrue(response.toString().contains("Y"));
        assertTrue(response.toString().contains("Z"));
        ;

    }
    @Test
    public void testContentsOfSearchCSV(){

    }


    }









