package edu.brown.cs.student.main.Server;

import static spark.Spark.after;

import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandDataSource;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandHandler;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.CachingBroadbandDataSource;
import edu.brown.cs.student.main.Server.CSV.CSVDataSource;
import edu.brown.cs.student.main.Server.CSV.LoadCSVHandler;
import edu.brown.cs.student.main.Server.CSV.SearchCSVHandler;
import edu.brown.cs.student.main.Server.CSV.ViewCSVHandler;
import java.io.IOException;
import java.net.URISyntaxException;
import spark.Spark;

/**
 * Top-level class which contains the main() method which starts Spark and runs the various
 * handlers depending on what the user requests.
 *
 */
public class Server {
  /**
   * Our options are loadcsv, viewcsv, searchcsv and broadband,
   * we instantiate each of the respective handlers through this server class main method.
   * Load, view and search csv all have the same shared state which is the data source class.
   *  Meanwhile, the broadband class takes in the class which handles caching which takes in the broadband data source.
   *  This main method starts our server and proves the site and port for the user to use.
   * @param args
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   */
  public static void main(String[] args)
      throws URISyntaxException, IOException, InterruptedException {
    int port = 1234;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET /order and /activity endpoints
    CSVDataSource csvDataSource = new CSVDataSource();
    Spark.get("loadcsv", new LoadCSVHandler(csvDataSource));
    Spark.get("viewcsv", new ViewCSVHandler(csvDataSource));
    Spark.get("searchcsv", new SearchCSVHandler(csvDataSource));
    Spark.get("broadband", new BroadbandHandler(new CachingBroadbandDataSource(new BroadbandDataSource())));
    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }



}
