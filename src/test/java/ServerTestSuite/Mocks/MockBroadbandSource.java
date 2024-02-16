package ServerTestSuite.Mocks;

import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandDataSourceInterface;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
  public BroadbandData getBroadbandData(String fakeState, String fakeCounty) {
    return new BroadbandData(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString(), state, county, percentAccess);
  }
}