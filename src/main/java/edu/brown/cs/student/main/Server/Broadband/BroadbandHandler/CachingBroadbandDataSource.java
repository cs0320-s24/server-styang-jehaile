package edu.brown.cs.student.main.Server.Broadband.BroadbandHandler;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import java.util.concurrent.TimeUnit;

public class CachingBroadbandDataSource implements BroadbandDataSourceInterface {

  private final LoadingCache<String, BroadbandData> cache;

  public CachingBroadbandDataSource(BroadbandDataSourceInterface toWrap) {

    this.cache =
        CacheBuilder.newBuilder()
            // How many entries maximum in the cache?
            .maximumSize(1)
            // How long should entries remain in the cache?
            .expireAfterWrite(10, TimeUnit.MINUTES)
            // Keep statistical info around for profiling purposes
            .recordStats()
            .build(
                // Strategy pattern: how should the cache behave when it's asked for something it
                // doesn't have?
                new CacheLoader<>() {
                  @Override
                  public BroadbandData load(String requestKey) throws Exception {
                    String[] splitKey = requestKey.split(",");
                    return toWrap.getBroadbandData(splitKey[0], splitKey[1]);
                  }
                });
  }

  @Override
  public BroadbandData getBroadbandData(String state, String county) {
    String requestKey = (state + "," + county);
    return cache.getUnchecked(requestKey);
  }
}
