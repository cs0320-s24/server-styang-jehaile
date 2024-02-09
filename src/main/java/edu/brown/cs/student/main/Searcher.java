package edu.brown.cs.student.main;

import java.util.ArrayList;
import java.util.List;

/**
 * This is my searcher class which handles all the functionality of a searching for a word in a
 * given data set.
 *
 * @param <T>
 */
public class Searcher<T> {
  CSVParser<List<String>> parser;
  List<List<String>> parsedList;
  Boolean header;
  String value;
  Integer columnIdentifier;
  String columnName;

  /**
   * This is my searcher constructor takes in a CSV parser, the value to search for, a boolean
   * indicating whether there headers present in the data set, an integer which should indicate the
   * column index and a string which holds the column header. In my constructor I also call the
   * parse method on my instance of parser so that I can store this value in a list to iterate
   * through as I search.
   *
   * @param parser
   * @param value
   * @param header
   * @param columnIdentifier
   * @param columnName
   */
  public Searcher(
      CSVParser<List<String>> parser,
      String value,
      Boolean header,
      Integer columnIdentifier,
      String columnName) {
    this.parser = parser;
    this.parsedList = this.parser.parse();
    this.header = header;
    this.value = value;
    this.columnIdentifier = columnIdentifier;
    this.columnName = columnName;
  }

  /**
   * This is my search method. This is where I construct the results of searching for a value. In it
   * I call a method that assigned a column name to the corresponding column index number. I
   * separated this method into an if else statement depending on whether or not they are searching
   * with headers. The user should also state true for the header boolean if they intend to use a
   * column indexer.
   *
   * @return
   */
  public List<List<String>> search() {
    List<List<String>> searchResults = new ArrayList<>();

    if (!this.header) {
      // if there are no headers i just loop through the whole data set and add a row to the
      // returned list if it contains the desired value.

      for (List<String> row : this.parsedList) {

        for (int j = 0; j < row.size(); j++) {
          String indexedValue = row.get(j);
          if (indexedValue.equals(value)) {
            searchResults.add(row);
          }
        }
      }

    } else {
      // if search is desired to be completed through a column indexer or using a header name this
      // is the
      // process I indicated in my main class that if a user does not want to enter a column
      // identifier to use a
      // negative number. so this check if the number is negative, but a header was used ie a column
      // name
      // i call my method which converts column names into their index number. Then following the
      // same but more efficient
      // process of above it checks the index of a row to see if it matches.
      if (columnIdentifier < 0) {
        columnIdentifier = this.headerNumber(columnName);
      }
      for (List<String> row : this.parsedList) {
        if (row.get(columnIdentifier).equals(value)) {
          searchResults.add(row);
        }
      }
    }
    return searchResults;
  }

  /**
   * This function converts a header into the corresponding numerical index of the list.
   *
   * @param columnName
   * @return
   */
  public Integer headerNumber(String columnName) {
    List<String> headerRow = this.parsedList.get(0);
    for (int i = 0; i < headerRow.size(); i++) {
      String currHeader = headerRow.get(i);
      if (columnName.equals(currHeader)) {
        return i;
      }
    }
    return -1;
  }
}
