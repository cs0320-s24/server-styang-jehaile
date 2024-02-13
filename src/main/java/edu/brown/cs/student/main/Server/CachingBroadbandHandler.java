package edu.brown.cs.student.main.Server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import spark.Request;
import spark.Response;
import spark.Route;

public class CachingBroadbandHandler implements Route, BroadbandHandlerGeneric {

  private final BroadbandHandlerGeneric toWrap;
  private final LoadingCache<String, Object> cache;

  public CachingBroadbandHandler(BroadbandHandlerGeneric toWrap) {
    this.toWrap = toWrap;

    // Look at the docs -- there are lots of builder parameters you can use
    //   including ones that affect garbage-collection (not needed for Server).
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
                new CacheLoader<String, Object>() {
                  private Request request;

                  @Override
                  public Object load(String requestKey) throws Exception {
                    String[] splitKey = requestKey.split(",");
                    StateCountyRequest mockRequest = new StateCountyRequest(splitKey[0], splitKey[1]);
                    return toWrap.handle(mockRequest, null);
                  }
                });
  }
  //cache in the datasource rather than handler

  //can we do integration testing without calling api and unit tests of the data source
//testing w/o invocating a real api/calling census api
    //how to set up integration tests, need to bypass real census api- cache in datasource(instead baseline proxing
  @Override
  public Object handle(Request request, Response response) {
    System.out.println(cache.asMap());
    String targetState = request.queryParams("state");
    String targetCounty = request.queryParams("county");
    String requestKey = (targetState + "," + targetCounty);
    Object result = cache.getUnchecked(requestKey);
    return result;
  }
}
