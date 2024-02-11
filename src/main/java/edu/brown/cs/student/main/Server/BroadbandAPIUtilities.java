package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;

public class BroadbandAPIUtilities { // error handling?
  public static BroadbandData deserializeBroadbandData(String jsonBroadbandData)
      throws IOException {
    // Initializes Moshi
    Moshi moshi = new Moshi.Builder().build();

    // Initializes an adapter to an Activity class then uses it to parse the JSON.
    JsonAdapter<BroadbandData> adapter = moshi.adapter(BroadbandData.class);

    BroadbandData broadbandData = adapter.fromJson(jsonBroadbandData);

    return broadbandData;
  }
}
