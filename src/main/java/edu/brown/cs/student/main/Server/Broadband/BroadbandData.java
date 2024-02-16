package edu.brown.cs.student.main.Server.Broadband;

import java.time.LocalDateTime;

/**
 * Record containing the fields of the broadband data. This method is used throughout the broadband package
 * to models a broadband received from the API.
 * @param time
 * @param state
 * @param county
 * @param percentAccess
 */
public record BroadbandData(String time, String state, String county, double percentAccess) {

  /**
   * This method overrides java's to string method to redefine how it should behave for broadband data. The
   * method returns the time, state, county and percentage of access as a string concated with the field name.
   * @return
   */
  @Override
  public String toString() {
    return "{time=" + time + ", state=" + state + ", county=" + county + ", percentAccess=" + percentAccess + "}";
  }
}
