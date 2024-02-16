package edu.brown.cs.student.main.Server.Broadband;

import java.time.LocalDateTime;

public record BroadbandData(LocalDateTime time, String state, String county, double percentAccess) {

  @Override
  public String toString() {
    return "{LocalDateTime=" + time + ", state=" + state + ", county=" + county + ", percentAccess=" + percentAccess + "}";
  }
}
