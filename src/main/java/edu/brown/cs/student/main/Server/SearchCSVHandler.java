package edu.brown.cs.student.main.Server;

import spark.Request;
import spark.Response;
import spark.Route;

public class SearchCSVHandler implements Route {
    private final LoadCSVHandler loadCSVHandler;

    public SearchCSVHandler(LoadCSVHandler loadCSVHandler) {
        this.loadCSVHandler = loadCSVHandler;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {

    }
}
