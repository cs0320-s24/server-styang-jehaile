package ServerTestSuite;

import ServerTestSuite.Mocks.MockBroadbandSource;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandDataSource;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandDataSourceInterface;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandHandler;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.CachingBroadbandDataSource;
import org.junit.jupiter.api.BeforeEach;
import spark.Spark;

public class ServerBroadbandIntegrationTest {
  @BeforeEach
  public void setup() {
    // Re-initialize parser, state, etc. for every test method

    // Use *MOCKED* data when in this test environment.
    // Notice that the WeatherHandler code doesn't need to care whether it has
    // "real" data or "fake" data. Good separation of concerns enables better testing.
    BroadbandDataSourceInterface mockedSource = new MockBroadbandSource(new BroadbandData("California", "Orange", 90.3));
    Spark.get("/broadband", new BroadbandHandler(new CachingBroadbandDataSource(mockedSource)));
    Spark.awaitInitialization(); // don't continue until the server is listening

//    // New Moshi adapter for responses (and requests, too; see a few lines below)
//    //   For more on this, see the Server gearup.
//    Moshi moshi = new Moshi.Builder().build();
//    adapter = moshi.adapter(mapStringObject);
//    weatherDataAdapter = moshi.adapter(WeatherData.class);
  }
}
