package edu.brown.cs.student.main.Server.Broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

public class BroadbandAPIUtilities {

  public static BroadbandData deserializeBroadbandData(String jsonBroadbandData)
      throws IOException {

    List<List<String>> countyList = buildListAdapter(jsonBroadbandData);

    // Get percent broadband access
    double percentAccess = Double.parseDouble(countyList.get(1).get(1));

    // Get county and state name
    String countyFullName = countyList.get(1).get(0);
    String[] countySplit = countyFullName.split(", ");
    String countyName = countySplit[0];
    String stateName = countySplit[1];

    return new BroadbandData(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString(), stateName, countyName, percentAccess);
  }

  public static HashMap<String, String> deserializeStateMap(HttpResponse<String> jsonStateMap)
      throws IOException {

    List<List<String>> stateList = buildListAdapter(jsonStateMap.body());

    HashMap<String, String> stateToCodeMap = new HashMap<>();
    for (int i = 1; i < stateList.size(); i++) {
      stateToCodeMap.put(stateList.get(i).get(0).toLowerCase(), stateList.get(i).get(1));
    }
    return stateToCodeMap;
  }

  public static List<List<String>> buildListAdapter(String toDeserialize) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    Type type = com.squareup.moshi.Types.newParameterizedType(List.class, List.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(type);
    return adapter.fromJson(toDeserialize);
  }

  public static HttpResponse<String> getAPIResponse(String query)
      throws URISyntaxException, IOException, InterruptedException {
    HttpRequest buildDataRequest =
        HttpRequest.newBuilder()
            .uri(
                new URI(query))
            .GET()
            .build();

    HttpResponse<String> sentDataResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildDataRequest, HttpResponse.BodyHandlers.ofString());

    return sentDataResponse;
  }
}
