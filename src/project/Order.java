package project;

import java.io.*;
import java.util.*;
import asciiTable.*;

/**
 */
public class Order extends Mail {

  /**
   *
   */
  private static final long serialVersionUID = -2016003901205725175L;
  private OrderState state;
  public static HashMap<Role, Integer> rolePriorityTable = new HashMap<Role, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put(Role.ADMIN, 1);
			put(Role.TECH_SUPPORT, 2);
			put(Role.MANAGER, 3);
			put(Role.TEACHER, 4);
			put(Role.STUDENT, 5);
		}
	};
  
  public Order(String title, String text, String senderID) {
    super(title, text, senderID);
    this.state = OrderState.UNREAD;
  }
  /**
   * @param state
   */
  public void setState(OrderState state) {
    this.state = state;
  }

  /**
   * @return
   */
  public String toString() {
    User sender = getSender();
    String[][] data = {
      { "Title:", title },
      { "Sender:", sender.getFullName() },
      { "Sender role:", sender.getRole().toString() },
      { "Sended time:", this.sendTime.toLocaleString() }
    };
    return AsciiTable.getTable(data) + "\n" + this.text;
  }

  /**
   * @return
   */
  public OrderState getState() {
    return this.state;
  }

  public static int compareDoneFirst(Order o1, Order o2) {
    int res1 = o1.state.equals(OrderState.DONE) ? -1 : 0;
    int res2 = o2.state.equals(OrderState.DONE) ? -1 : 0;
    return Integer.compare(res1, res2);
  }

  public static int compareAcceptedFirst(Order o1, Order o2) {
    int res1 = o1.state.equals(OrderState.ACCEPTED) ? -1 : 0;
    int res2 = o2.state.equals(OrderState.ACCEPTED) ? -1 : 0;
    return Integer.compare(res1, res2);
  }

  public static int compareRejectedFirst(Order o1, Order o2) {
    int res1 = o1.state.equals(OrderState.REJECTED) ? -1 : 0;
    int res2 = o2.state.equals(OrderState.REJECTED) ? -1 : 0;
    return Integer.compare(res1, res2);
  }

  public static int compareUnreadFirst(Order o1, Order o2) {
    int res1 = o1.state.equals(OrderState.UNREAD) ? -1 : 0;
    int res2 = o2.state.equals(OrderState.UNREAD) ? -1 : 0;
    return Integer.compare(res1, res2);
  }

  public static int compareBySenderRole(Order o1, Order o2) {
    return Integer.compare(rolePriorityTable.get(o1.getSender().getRole()),
        rolePriorityTable.get(o2.getSender().getRole()));
  }

  public static void viewOrder(BufferedReader br, Order order, String fromId) {
    order.read();

    while (true) {
      System.out.println(order.toString());
      try {
        System.out.println("Please choose one of the options below:");
        System.out.println("1) Set state to Accepted");
        System.out.println("2) Set state to Rejected");
        System.out.println("3) Set state to Done");
        System.out.println("4) Reply to this order");
        System.out.println("0) Back");
        String option = br.readLine();
        if (option.equals("1")) {
          order.setState(OrderState.ACCEPTED);
        }
        if (option.equals("2")) {
          order.setState(OrderState.REJECTED);
        }
        if (option.equals("3")) {
          order.setState(OrderState.DONE);
        }
        if (option.equals("4")) {
          User receiver = order.getSender();
          System.out.println("Please enter title");
          String title = br.readLine();
          System.out.println("Please write a message, just send empty line to finish");
          String text = User.writePlainText(br);
          Mail mail = new Mail(title, text, fromId);
          try {
            Intranet.INSTANCE.sendMail(mail, receiver.login, Intranet.INSTANCE.getUserByID(fromId));
          } catch (NoSuchElementException e) {
            System.err.println("Incorrect receiver's login");
          }
        }
        if (option.equals("0")) {
          break;
        }

      } catch (IOException e) {
				System.err.println("Please enter the number!");
				continue;
			} 
    }
  }


  /**
   * @param br
   * @param orderType
   */
  public static void viewOrders(BufferedReader br, OrderType orderType, String fromId) {
    Vector<Order> orders = null;
    if (orderType.equals(OrderType.OR)) {
      orders = Intranet.INSTANCE.getOROrders();
    }
    if (orderType.equals(OrderType.IT)) {
      orders = Intranet.INSTANCE.getITOrders();
    }
    while (true) {
      String[] header = {"#", "Sender", "Sender Role", "Title", "Date", "State"};
      String[][] data = new String[orders.size()][6];
      for (int i = 0; i < orders.size(); i++) {
        Order order = orders.get(i);
        User sender = order.getSender();
        data[i][0] = "" + (i+1) + (order.isRead ? "" : "*");
        data[i][1] = sender.getFullName();
        data[i][2] = sender.getRole().toString();
        data[i][3] = order.getTitle();
        data[i][4] = order.sendTime.toLocaleString();
        data[i][5] = order.getState().toString();
      }
      System.out.println(AsciiTable.getTable(header, data));

      try {
        System.out.println("Please choose one of the options below:");
        System.out.println("1) Choose order by #");
        System.out.println("2) Sort by date");
        System.out.println("3) Sort by done state");
        System.out.println("4) Sort by rejected state");
        System.out.println("5) Sort by accepted state");
        System.out.println("6) Sort by unread state");
        System.out.println("7) Sort by sender role");
        System.out.println("8) Reverse orders list");
        System.out.println("0) Back");
        String option = br.readLine();
        if (option.equals("1")) {
          System.out.println("Please enter # of the order");
          int orderIndex = Integer.parseInt(br.readLine())-1;
          Order order = orders.get(orderIndex);
          Order.viewOrder(br, order, fromId);
        }
        if (option.equals("2")) {
          Collections.sort(orders, (o1, o2) -> o1.sendTime.compareTo(o2.sendTime));
        }
        if (option.equals("3")) {
          Collections.sort(orders, Order::compareDoneFirst);
        }
        if (option.equals("4")) {
          Collections.sort(orders, Order::compareRejectedFirst);
        }
        if (option.equals("5")) {
          Collections.sort(orders, Order::compareAcceptedFirst);
        }
        if (option.equals("6")) {
          Collections.sort(orders, Order::compareUnreadFirst);
        }
        if (option.equals("7")) {
          Collections.sort(orders, Order::compareBySenderRole);
        }
        if (option.equals("8")) {
          Collections.reverse(orders);
        }
        if (option.equals("0")) {
          break;
        }


      } catch (IOException e) {
				System.err.println("Please enter the number!");
				continue;
			} catch (NumberFormatException e) {
				System.err.println("Please enter the NUMBER!");
				continue;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Please enter CORRECT number!");
				continue;
      } 
    }
  }
}
