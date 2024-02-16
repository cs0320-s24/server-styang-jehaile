package edu.brown.cs.student.main.Server.Broadband;

import java.time.LocalDateTime;

public record BroadbandData(String time, String state, String county, double percentAccess) {

  @Override
  public String toString() {
    return "{time=" + time + ", state=" + state + ", county=" + county + ", percentAccess=" + percentAccess + "}";
  }
}
