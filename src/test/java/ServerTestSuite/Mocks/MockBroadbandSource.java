package ServerTestSuite.Mocks;

import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandDataSourceInterface;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;

public class MockBroadbandSource implements BroadbandDataSourceInterface {
  private final String state;
  private final String county;
  private final double percentAccess;

  public MockBroadbandSource(String state, String county, double percentAccess) {
    this.state = state;
    this.county = county;
    this.percentAccess = percentAccess;
  }

  @Override
  public BroadbandData getBroadbandData(String state, String county) {
    // wisconsin and dane is base case
    if (state.equals("") || county.equals("")) {
      throw new NoSuchElementException();
    }
    if ((!state.equalsIgnoreCase("wisconsin") && (!county.equalsIgnoreCase("dane")))) {
      String time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
      return new BroadbandData(time, state, county, percentAccess);

    } else {
      return new BroadbandData(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString(),
          "wisconsin", "dane", 90.0);
    }
  }
}