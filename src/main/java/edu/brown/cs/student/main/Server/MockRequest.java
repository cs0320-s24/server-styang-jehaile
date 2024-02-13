package edu.brown.cs.student.main.Server;

import spark.Request;

public class MockRequest extends Request {

  private String state;
  private String county;

  public MockRequest(String state, String county) {
    this.state = state;
    this.county = county;
  }

  @Override
  public String queryParams(String name) {
    if ("state".equals(name)) {
      return this.state;
    } else if ("county".equals(name)) {
      return this.county;
    }
    return null;
  }
}
