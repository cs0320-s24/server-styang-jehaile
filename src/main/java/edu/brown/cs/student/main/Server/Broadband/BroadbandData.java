package edu.brown.cs.student.main.Server.Broadband;

/**
 * Record containing the fields of the broadband data. This method is used throughout the broadband
 * package to models a broadband received from the API.
 *
 * @param time          String of the time the data was queried
 * @param state         String of the state
 * @param county        String of the county
 * @param percentAccess Double representing how much percentage access is available
 */
public record BroadbandData(String time, String state, String county, double percentAccess) {

  /**
   * This method overrides java's to string method to redefine how it should behave for broadband
   * data. The method returns the time, state, county and percentage of access as a string concated
   * with the field name.
   *
   * @return Returns a string of the time, state, county and percentage broadband
   */
  @Override
  public String toString() {
    return "{time="
        + time
        + ", state="
        + state
        + ", county="
        + county
        + ", percentAccess="
        + percentAccess
        + "}";
  }
}
