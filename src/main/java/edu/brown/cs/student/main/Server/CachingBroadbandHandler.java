package edu.brown.cs.student.main.Server;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import spark.Request;
import spark.Response;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CachingBroadbandHandler implements BroadbandHandlerGeneric {
  private final BroadbandHandlerGeneric toWrap;
  private final LoadingCache<Request, Object> cache;


  public CachingBroadbandHandler(BroadbandHandlerGeneric toWrap) {
    this.toWrap = toWrap;


    // Look at the docs -- there are lots of builder parameters you can use
    //   including ones that affect garbage-collection (not needed for Server).
    this.cache = CacheBuilder.newBuilder()
        // How many entries maximum in the cache?
        .maximumSize(5)
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
                        // Implement logic to load data associated with the key
//                        Request request = new Request(); // You need to create a Request object here
//                        Response response = new Response(); // You need to create a Response object here
                        return toWrap.handle(keyRequest, null);
//                        return (Collection<String>) toWrap.handle(new Request(), new Response());
//                        return (Collection<String>) toWrap.handle(this.request, this.response);
//                        Object result = toWrap.handle(this.request, this.response);
//                        if (result instanceof Collection) {
//                            return (Collection<String>) result;
//                        } else {
//                            return Collections.emptyList();
//                        }
                    }
                });
  }
//            new CacheLoader<Request, Response>() {
//                @Override
//                public Response load(Request request) throws Exception {
//
//                    return null;
//
//                }
//
//                @Override
//                public Collection<String> load(String key) throws Exception {
//                    Request request = new Request(); // You need to create a Request object here
//                    Response response = new Response(); // You need to create a Response object here
//                    return (Collection<String>) toWrap.handle(request, response);
//                }
//            @Override
//              public Response load(String responset) throws URISyntaxException, IOException, InterruptedException {
//                return null;
//              }
//            });


  @Override
  public Object handle(Request request, Response response) {
      return null;
  }
//  public Object handle(Request resquest){
//      return null;
//  }
}
