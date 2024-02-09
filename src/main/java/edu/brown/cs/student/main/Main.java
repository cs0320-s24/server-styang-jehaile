package edu.brown.cs.student.main;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/** The Main class of our project. This is where execution begins. */
public final class Main {
  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private Main(String[] args) {}

  /**
   * This is my run method which collaborates with user facing application. I first ask the user to
   * input the file path they want to search through. I interact with the user using a Scanner which
   * allows me to handle their next line. I store the values they provide: file path, search word,
   * column identifier, column name, and a boolean that see if there is a header or not. Then I
   * create a parser and pass that into my searcher. I default to the user only being to use
   * strings, however I test other CreatorFromRow classes that operate on differently shaped CSV
   * data in my test suite. I then search and print out the row containing the word. I throw an
   * error message file not found if the file does not exist.
   */
  private void run() {

    System.out.println("Please input the following lines in the following order: File Path");
    Scanner input = new Scanner(System.in);
    String filePath = input.nextLine();
    System.out.println("Please input the following lines in the following order: Search Word");
    String word = input.nextLine();
    System.out.println(
        "Please input the following lines in the following order: Column Identifying Number (please put negative integer if not using column indexer)");
    Integer columnNum = input.nextInt();
    input.nextLine();
    System.out.println(
        "Please input the following lines in the following order: Column Name (please put none if there are none)");
    String columnName = input.nextLine();
    System.out.println(
        "Please input the following lines in the following order: true or false depending on if headers are present in the file");
    Boolean h = input.nextBoolean();

    try (FileReader fileReader = new FileReader(filePath)) {

      StringCreator stringCreator = new StringCreator();
      CSVParser csvParser = new CSVParser(stringCreator, fileReader);

      Searcher searcher = new Searcher(csvParser, word, h, columnNum, columnName);

      System.out.print(searcher.search());

    } catch (IOException e) {
      System.err.println("File not found");
    }
  }
}
