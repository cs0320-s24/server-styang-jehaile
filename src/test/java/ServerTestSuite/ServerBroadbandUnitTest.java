package ServerTestSuite;

import ServerTestSuite.Mocks.MockBroadbandSource;
import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandDataSourceInterface;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.CachingBroadbandDataSource;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class ServerBroadbandUnitTest {
  private CachingBroadbandDataSource cachingSource;
  private BroadbandDataSourceInterface mockedSource;

  @BeforeEach
  public void setup() {
    mockedSource = new MockBroadbandSource("california", "orange", 90.3);
    cachingSource = new CachingBroadbandDataSource(mockedSource);
  }

  @Test
  public void testCachingFunctionalityTimeExpiration() throws IOException, InterruptedException {
    BroadbandData initialResponse = cachingSource.getBroadbandData("california", "orange");

    Thread.sleep(1500);

    // Make the same request again and ensure that the response is different from the initial response
    BroadbandData cachedResponse = cachingSource.getBroadbandData("california", "orange");

    // Assert that the cached response is not equal to the initial response
    Assert.assertNotEquals(initialResponse.toString(), cachedResponse.toString());
  }

  @Test
  public void testCachingFunctionalityTimeNonExpiration() throws InterruptedException {

    BroadbandDataSourceInterface mockedSource = new MockBroadbandSource("california", "orange", 90.3);

    CachingBroadbandDataSource cachingSource = new CachingBroadbandDataSource(mockedSource);

    BroadbandData initialResponse = cachingSource.getBroadbandData("california", "orange");

    Thread.sleep(500);

    // Make the same request again and ensure that the response is different from the initial response
    BroadbandData cachedResponse = cachingSource.getBroadbandData("california", "orange");

    // Assert that the cached response is not equal to the initial response
    Assert.assertEquals(initialResponse.toString(), cachedResponse.toString());
  }

  @Test
  public void testCachingFunctionalityCacheLimit() throws InterruptedException {

    BroadbandDataSourceInterface mockedSource = new MockBroadbandSource("california", "orange", 90.3);

    CachingBroadbandDataSource cachingSource = new CachingBroadbandDataSource(mockedSource);

    BroadbandData initialResponse = cachingSource.getBroadbandData("california", "orange");

    Thread.sleep(500);

    // Make the same request again and ensure that the response is different from the initial response
    BroadbandData cachedResponse = cachingSource.getBroadbandData("california", "orange");

    // Assert that the cached response is not equal to the initial response
    Assert.assertEquals(initialResponse.toString(), cachedResponse.toString());
  }


}
