package edu.brown.cs.student.main.Server;

import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.SearchUtility.SearchStrategy;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

public class ViewCSVHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {

        String fileName = request.queryParams("fileName");

        Boolean headers;
        try {
            headers = Boolean.valueOf(request.queryParams("headers")); // Could change to not wrapper
        } catch (IllegalArgumentException e){
            return new LoadCSVHandler.LoadCSVFailureResponse().serialize();
        }

        try {
            FileReader fileReader = new FileReader(fileName);
            SearchStrategy strategyObj = new SearchStrategy();

            CSVParser csvParser = new CSVParser(fileReader, strategyObj, headers);
            csvParser.parse();

        } catch (IOException e) {
            return new LoadCSVHandler.LoadCSVFailureResponse().serialize();
        }
        return new LoadCSVHandler.LoadCSVSuccessResponse().serialize();
    }

}
