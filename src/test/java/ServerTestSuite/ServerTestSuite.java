package ServerTestSuite;

import edu.brown.cs.student.main.Server.LoadCSVHandler;
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
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
                System.out.print("beforeall");
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
//        LoadCSVHandler loadCSVHandler = new LoadCSVHandler();
//        Request request = new Request();
//        loadCSVHandler.handle()
//        Server server = new Server();
//        server

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
//        LoadCSVHandler loadCSVHandler = new LoadCSVHandler();
//        Request request = new Request();
//        loadCSVHandler.handle()
//        Server server = new Server();
//        server

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
//        LoadCSVHandler loadCSVHandler = new LoadCSVHandler();
//        Request request = new Request();
//        loadCSVHandler.handle()
//        Server server = new Server();
//        server

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




}
