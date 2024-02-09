package edu.brown.cs.student.main;

import java.util.List;

/**
 * This is my pizza creator class that implements the creatorfromrow interface, with objects of type
 * pizza.
 */
public class PizzaCreator implements CreatorFromRow<Pizza> {
  /**
   * This method just redefines create for objects of type pizza and returns the pizza values, so
   * that my parser can make rows of type pizza.
   *
   * @param row
   * @return
   * @throws FactoryFailureException
   */
  @Override
  public Pizza create(List<String> row) throws FactoryFailureException {

    String name = row.get(0);
    String sauce = row.get(1);
    String cheese = row.get(2);
    String size = row.get(3);
    String orderID = row.get(4);

    return new Pizza(name, sauce, cheese, size, orderID);
  }
}
