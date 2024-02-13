package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class BroadbandAPIUtilities { // error handling?
  public static BroadbandData deserializeBroadbandData(String jsonBroadbandData)
      throws IOException {

    Moshi moshi = new Moshi.Builder().build();
    Type type = com.squareup.moshi.Types.newParameterizedType(List.class, List.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(type);
    List<List<String>> countyList = adapter.fromJson(jsonBroadbandData);

    // Get percent broadband access
    double percentAccess = Double.parseDouble(countyList.get(1).get(1));

    // Get county and state name
    String countyFullName = countyList.get(1).get(0);
    String[] countySplit = countyFullName.split(", ");
    String countyName = countySplit[0];
    String stateName = countySplit[1];

    return new BroadbandData(stateName, countyName, percentAccess);
  }
}
