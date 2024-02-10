package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.SearchUtility.SearchStrategy;
import edu.brown.cs.student.main.Server.LoadCSVHandler.LoadCSVFailureResponse;
import edu.brown.cs.student.main.Server.LoadCSVHandler.LoadCSVSuccessResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

public class ViewCSVHandler implements Route {
    private final LoadCSVHandler loadCSVHandler;

    public ViewCSVHandler(LoadCSVHandler loadCSVHandler) {
        this.loadCSVHandler = loadCSVHandler;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Map<String, Object> responseMap = new HashMap<>();

        if (this.loadCSVHandler.isLoaded()) {
            CSVParser<List<String>> csvParser = this.loadCSVHandler.getCSVParser();
            if (csvParser.getHeaderList() != null) {
                responseMap.put("Headers", csvParser.getHeaderList());
            }
            responseMap.put("Data", csvParser.getCSVContents());

            return new ViewCSVSuccessResponse(responseMap).serialize();
        } else {
            return new ViewCSVFailureResponse().serialize();
        }
    }

    public record ViewCSVSuccessResponse(String responseType, Map<String, Object> responseMap) {
        public ViewCSVSuccessResponse(Map<String, Object> responseMap) {
            this("Success", responseMap);
        }

        String serialize() { // error? check gearup code
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<ViewCSVSuccessResponse> adapter = moshi.adapter(ViewCSVSuccessResponse.class);
            return adapter.toJson(this);
        }
    }

    public record ViewCSVFailureResponse(String responseType) {
        public ViewCSVFailureResponse() {
            this("Failed to view. Please load CSV file.");
        }

        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(ViewCSVHandler.ViewCSVFailureResponse.class).toJson(this);
        }
    }

}
