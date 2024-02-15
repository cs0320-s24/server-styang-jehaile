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
 * Top-level class for this demo. Contains the main() method which starts Spark and runs the various
 * handlers (2).
 *
 * <p>Notice that the OrderHandler takes in a state (menu) that can be shared if we extended the
 * restaurant They need to share state (a menu). This would be a great opportunity to use dependency
 * injection. If we needed more endpoints, more functionality classes, etc. we could make sure they
 * all had the same shared state.
 */
public class Server {

  // What are the endpoints that we can access... What happens if you go to them?
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
