package edu.brown.cs.student.main.Server;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.JsonAdapter;

import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.SearchUtility.SearchStrategy;
import spark.Request;
import spark.Response;
import spark.Route;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LoadCSVHandler implements Route {
    private CSVParser<ArrayList<String>> csvParser;

    @Override
    public Object handle(Request request, Response response) throws Exception {

        String fileName = request.queryParams("fileName");

        Boolean headers;
        try {
            headers = Boolean.valueOf(request.queryParams("headers")); // Could change to not wrapper
        } catch (IllegalArgumentException e) {
            return new LoadCSVFailureResponse().serialize();
        }

        try {
            FileReader fileReader = new FileReader(fileName);
            SearchStrategy strategyObj = new SearchStrategy();

            this.csvParser = new CSVParser(fileReader, strategyObj, headers);
            this.csvParser.parse();
        } catch (IOException e) {
            return new LoadCSVFailureResponse().serialize();
        }
        return new LoadCSVSuccessResponse().serialize();
    }

    public record LoadCSVSuccessResponse(String responseType) {
        public LoadCSVSuccessResponse() {
            this("Loaded successfully! :)");
        }

        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(LoadCSVSuccessResponse.class).toJson(this);
        }
    }

    public record LoadCSVFailureResponse(String responseType) {
        public LoadCSVFailureResponse() {
            this("Failed to load! :(");
        }

        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(LoadCSVFailureResponse.class).toJson(this);
        }
    }

    public CSVParser<ArrayList<String>> getCSVParser() {
        return this.csvParser;
    }
}