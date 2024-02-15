package ServerTestSuite.Mocks;

import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandDataSourceInterface;
import java.io.IOException;
import java.net.URISyntaxException;

public class MockBroadbandSource implements BroadbandDataSourceInterface {
  private final BroadbandData constantData;

  public MockBroadbandSource(BroadbandData broadbandData) {
    this.constantData = broadbandData;
  }

  @Override
  public BroadbandData getBroadbandData(String state, String county) {
    return this.constantData;
  }
}
