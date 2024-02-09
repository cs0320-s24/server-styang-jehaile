package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This is my parser class which is of type T. My parser constructor takes in a CreatorFromRow class
 * and a Reader.
 *
 * @param <T>
 */
public class CSVParser<T> {
  private Reader reader;
  private CreatorFromRow<T>
      creator; // create the string and return row5, used to handle different data, convert the
  // parse line to a row
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  /**
   * In my constructor I set the creator and reader passed in as the global variables used later in
   * the class.
   *
   * @param creator
   * @param reader
   */
  public CSVParser(CreatorFromRow<T> creator, Reader reader) { // ReaderClass reader taken out
    this.creator = creator;
    this.reader = reader;
  }

  /**
   * In my parse method I create a list of whatever data type preferred. I encapsulate most of my
   * method in a try catch block so that I can check if any errors occured. I use an instance of
   * buffer reader to read the file passed in I store that file line in a string. The file is read
   * as long as the next line is not null. I use the regexsplitcsvrow to split by commas if it is
   * outside the double quotes. I then call the creator's create method. I store all the values in a
   * list of the type specified and continuing to read the next line.
   *
   * @return
   */
  public List<T> parse() {
    List<T> dataValues = new ArrayList<>();

    try (BufferedReader fileReader = new BufferedReader(this.reader)) {

      String line = fileReader.readLine();

      while (line != null) {
        String[] array = regexSplitCSVRow.split(line);
        List<String> list = List.of(array); // creating a list (which is one row)/
        T obj = this.creator.create(list); // change the list to an obj
        dataValues.add(obj);
        line = fileReader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (FactoryFailureException e) {
      throw new RuntimeException(e);
    }

    if (dataValues.isEmpty()) {
      System.out.print("This file is empty");
    }
    return dataValues;
  }
}
