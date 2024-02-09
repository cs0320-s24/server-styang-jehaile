package edu.brown.cs.student.main;

/** This is a class to just store the parameters of a pizza object, I use it in PizzaCreator. */
public class Pizza {
  String pizza;
  String sauce;
  String cheese;
  String size;
  String orderID;

  /**
   * This is the constructor for my pizza creator just set the global variables to the variable
   * passed in.
   *
   * @param pizza
   * @param sauce
   * @param cheese
   * @param size
   * @param orderID
   */
  public Pizza(String pizza, String sauce, String cheese, String size, String orderID) {
    this.pizza = pizza;
    this.sauce = sauce;
    this.cheese = cheese;
    this.size = size;
    this.orderID = orderID;
  }
}
