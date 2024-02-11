package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {
    private HashMap<String, String> stateToCode;

    public BroadbandHandler() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest stateToCodeRequest =
                HttpRequest.newBuilder()
                        .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
                        .GET()
                        .build();

        // Send that API request then store the response in this variable. Note the generic type.
        HttpResponse<String> stateToCode =
                HttpClient.newBuilder()
                        .build()
                        .send(stateToCodeRequest, HttpResponse.BodyHandlers.ofString());

        Moshi moshi = new Moshi.Builder().build();
        Type type = com.squareup.moshi.Types.newParameterizedType(List.class, List.class);
        JsonAdapter<List<List<String>>> adapter = moshi.adapter(type);
        List<List<String>> stateList = adapter.fromJson(stateToCode.body());
        HashMap<String, String> stateToCodeMap = new HashMap<>();
        for (int i = 1; i < stateList.size(); i++) {
            stateToCodeMap.put(stateList.get(i).get(0), stateList.get(i).get(1));
        }
        this.stateToCode = stateToCodeMap;
    }

    @Override
    public Object handle(Request request, Response response) throws URISyntaxException, IOException, InterruptedException {
        String targetState = request.queryParams("state");
        String targetCounty = request.queryParams("county");
        Map<String, Object> responseMap = new HashMap<>();

        // get state code from map
        // Add failure response if state not in map
        String stateCode = this.stateToCode.get(targetState);
        String countyCode = "";

        // get county code from county
        HttpRequest stateToCodeRequest =
                HttpRequest.newBuilder()
                        .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode))
                        .GET()
                        .build();

        // Send that API request then store the response in this variable. Note the generic type.
        // HANDLE EXCEPTIONS
        HttpResponse<String> countyToCode =
                HttpClient.newBuilder()
                        .build()
                        .send(stateToCodeRequest, HttpResponse.BodyHandlers.ofString());

        Moshi moshi = new Moshi.Builder().build();

        Type type = com.squareup.moshi.Types.newParameterizedType(List.class, List.class);
        JsonAdapter<List<List<String>>> adapter = moshi.adapter(type);
        List<List<String>> countyList = adapter.fromJson(countyToCode.body());
//    HashMap<String, String> countyMap = new HashMap<>();
        for (int i = 1; i < countyList.size(); i++) {
            if (countyList.get(i).get(3).equals(targetCounty)) {
                countyCode = countyList.get(i).get(3);
                break;
            }
            // failure response (no county)
        }


        // send request
        return null;
    }

    private String sendRequest(String stateCode, String countyCode)
            throws URISyntaxException, IOException, InterruptedException { // figure out error handling

        HttpRequest buildDataRequest =
                HttpRequest.newBuilder()
                        .uri(
                                new URI(
                                        "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03" +
                                                "_022E&for=county:" + countyCode + "&in=state:" + stateCode))
                        .GET()
                        .build();

        // Send that API request then store the response in this variable. Note the generic type.
        HttpResponse<String> sentDataResponse =
                HttpClient.newBuilder()
                        .build()
                        .send(buildDataRequest, HttpResponse.BodyHandlers.ofString());

        return sentDataResponse.body();
    }
}
