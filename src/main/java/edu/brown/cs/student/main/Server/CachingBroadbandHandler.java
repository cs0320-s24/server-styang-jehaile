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
  private final LoadingCache<Request, Object> cache;


  public CachingBroadbandHandler(BroadbandHandlerGeneric toWrap) {
    this.toWrap = toWrap;

    // Look at the docs -- there are lots of builder parameters you can use
    //   including ones that affect garbage-collection (not needed for Server).
    this.cache = CacheBuilder.newBuilder()
        // How many entries maximum in the cache?
        .maximumSize(1)
        // How long should entries remain in the cache?
        .expireAfterWrite(10, TimeUnit.MINUTES)
        // Keep statistical info around for profiling purposes
        .recordStats()
        .build(
            // Strategy pattern: how should the cache behave when
            // it's asked for something it doesn't have?
            new CacheLoader<Request, Object>() {
              private Request request;

              @Override
              public Object load(Request keyRequest) throws Exception {
                return toWrap.handle(keyRequest, null);
              }
            });
  }

  @Override
  public Object handle(Request request, Response response) {
    Object result = cache.getUnchecked(request);
    return result;
  }
}
