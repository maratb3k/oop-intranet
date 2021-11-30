package project;

import java.io.*;
import java.util.*;

/**
 */
public class TechSupport extends User {

  /**
   *
   */
  private static final long serialVersionUID = 8816230701263099697L;

  /**
   * @param firstName
   * @param lastName
   * @param middleName
   * @param login
   * @param password
   * @param role
   */
  TechSupport(String firstName, String lastName, String middleName, String login, String password, Role role) {
    super(firstName, lastName, middleName, login, password, role);
  }

  /**
   * @param state
   * @param order
   */
  public void changeOrderState(Order order, OrderState state) {
  }

  /**
   * @param state
   * @return
   */
  public Vector<Order> getOrdersByState(OrderState state) {
    return null;
  }

  /**
   * @return
   */
  public Vector<Order> getAllOrders() {
    return null;
  }

  /**
   * 
   */
  @Override
  public void mainMenu(BufferedReader br) {
    try {
      while (true) {
        System.out.println("Please choose one of the options below:");
        System.out.println("1) Make request for Office register"); // +
        System.out.println("2) Send mail"); // +
        System.out.println("3) View IR orders"); // +
        System.out.println("4) View mailbox"); // +
        System.out.println("0) Exit"); // +

        String option = br.readLine();
        if (option.equals("1")) {
          super.writeOrderMenu(br, OrderType.OR);
        }
        if (option.equals("2")) {
          super.writeMailMenu(br);
        }
        if (option.equals("3")) {
          Order.viewOrders(br, OrderType.IT, this.getId());
        }
        if (option.equals("4")) {
          super.viewMailBox(br);
        }
        if (option.equals("0") || option.equalsIgnoreCase("exit")) {
          break;
        }

      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
