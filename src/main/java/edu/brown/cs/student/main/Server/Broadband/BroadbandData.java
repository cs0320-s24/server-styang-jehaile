package edu.brown.cs.student.main.Server.Broadband;

public record BroadbandData(String state, String county, double percentAccess) {

  @Override
  public String toString() {
    return "{state=" + state + ", county=" + county + ", percentAccess=" + percentAccess + "}";
  }
}
