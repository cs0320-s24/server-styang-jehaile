package edu.brown.cs.student.main.Server.Broadband.BroadbandHandler;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import java.util.concurrent.TimeUnit;

/**
 * This class handles the caching og information retrieved from the ACS in order to avoid sending excessive network requests.
 * This class wraps the broadband data source interface and caches the resposnse ot improve the efficiency of the program.
 * This is our proxy class so that the caller will interact with this class rather than multiple calls to the
 * real data source. We use the google's guava cache class to help manage this functionality.

 */
public class CachingBroadbandDataSource implements BroadbandDataSourceInterface {

  private final LoadingCache<String, BroadbandData> cache;

    /**
     * The constructor takes in an instance of the broadband source interface so that the caching class has
     * access to the broadband data getter in the interface. The constructor uses an instance of the guava loading cache class
     * to create the cacher, we set the max size to 6 entries, that last for 10 minutes and use the cache loader
     * to load the cache by taking in the request key which would be the state and county from the getter.
     * @param toWrap represents an instance of the broadband data source interface to get the data
     */
  public CachingBroadbandDataSource(BroadbandDataSourceInterface toWrap) {

    this.cache =
        CacheBuilder.newBuilder()
            .maximumSize(2)
            .expireAfterWrite(1000, TimeUnit.MILLISECONDS)
            .recordStats()
            .build(
                new CacheLoader<>() {
                  @Override
                  public BroadbandData load(String requestKey) throws Exception {
                    String[] splitKey = requestKey.split(",");
                    return toWrap.getBroadbandData(splitKey[0], splitKey[1]);
                  }
                });
  }

    /**
     * This method gets the broadband data takes in parammeters of the state and county name. This concatenates
     * the state and county take as the request string which is passed into getunchecked which retrieves the value associated with
     * the key from the cache or loads it if it is not present in the cache.
     *
     * @param state String representing state being queried.
     * @param county String representing the county being queried
     * @return the broadband data to the cache to be stored
     */
  @Override
  public BroadbandData getBroadbandData(String state, String county) {
    String requestKey = (state + "," + county);
    return cache.getUnchecked(requestKey);
  }
}
