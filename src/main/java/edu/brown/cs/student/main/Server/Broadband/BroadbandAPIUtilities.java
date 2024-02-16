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
import java.util.HashMap;
import java.util.List;



public class BroadbandAPIUtilities {
  /**
   * This method deserializes the broadband data. It takes in a string of json broadband data and converts it to java data.
   * This method gets the time of the request, the state name, county name and percentage of the broadband access and
   * returns a broadband data instance with those parameters.
   * @param jsonBroadbandData
   * @return
   * @throws IOException
   */

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

    return new BroadbandData(LocalDateTime.now().toString(), stateName, countyName, percentAccess);
  }

  /**
   * This method deserializes the state map from the json state map which it takes in to a java map.
   * Thsi method uses the json state map to extract the state information and returns a map of the state name and code.
   * @param jsonStateMap
   * @return
   * @throws IOException
   */

  public static HashMap<String, String> deserializeStateMap(HttpResponse<String> jsonStateMap)
      throws IOException {

    List<List<String>> stateList = buildListAdapter(jsonStateMap.body());

    HashMap<String, String> stateToCodeMap = new HashMap<>();
    for (int i = 1; i < stateList.size(); i++) {
      stateToCodeMap.put(stateList.get(i).get(0).toLowerCase(), stateList.get(i).get(1));
    }
    return stateToCodeMap;
  }

  /**
   * This method uses moshi to deserialize a json string passed into the method into java data and builds a list of
   * the converted data. This method is called above to create the state to code map and broadband data instance.
   * @param toDeserialize
   * @return
   * @throws IOException
   */
  public static List<List<String>> buildListAdapter(String toDeserialize) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    Type type = com.squareup.moshi.Types.newParameterizedType(List.class, List.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(type);
    return adapter.fromJson(toDeserialize);
  }

  /**
   * This method sends a get request to the API endpoint by building a request based on the query and returning the response
   * of type http response. This method throws a few exceptions: URISyntaxException if the provided query string is not a valid URI
   *  an IOException if an I/O error occurs while sending or receiving the request and an InterruptedException if the operation is interrupted
   * @param query
   * @return
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   */
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
