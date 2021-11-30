package project;

import java.io.*;
import java.util.*;

import asciiTable.AsciiTable;

/**
 */
public abstract class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1587266131752035700L;
	protected String id;
	protected String firstName;
	protected String lastName;
	protected String middleName;
	protected String login;
	protected long password;
	protected Vector<Mail> mailBox;
	protected Role role;
	protected boolean blocked;

	/**
	 * @param firstName
	 * @param lastName
	 * @param middleName
	 * @param login
	 * @param password
	 * @param role
	 */
	public User(String firstName, String lastName, String middleName, String login, String password, Role role) {
		this.mailBox = new Vector<Mail>();
		this.id = Intranet.INSTANCE.generateUserId();
		this.firstName = firstName;
		this.lastName = lastName;
		this.login = login;
		this.password = password.hashCode();
		this.role = role;
		this.middleName = middleName;
		this.blocked = false;
	}

	/**
	 * @param password
	 * @param login
	 * @return
	 */
	public boolean signIn(String login, String password) {
		return this.login.equals(login) && this.password == password.hashCode();
	}

	protected void block() {
		this.blocked = true;
	}

	protected void unBlock() {
		this.blocked = false;
	}
	

	public Role getRole() {
		return this.role;
	}

	public String getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, id, lastName, login, middleName, password, role);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		return Objects.equals(firstName, other.firstName) && Objects.equals(id, other.id)
				&& Objects.equals(lastName, other.lastName) && Objects.equals(login, other.login)
				&& Objects.equals(middleName, other.middleName) && password == other.password && role == other.role;
	}

	/**
	 * @return
	 */


	public static int compareByRole(User o1, User o2) {
		return Integer.compare(Order.rolePriorityTable.get(o1.getRole()), 
			Order.rolePriorityTable.get(o2.getRole()));
	}

	/**
	 * @return
	 */
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * @return
	 */
	public String getMiddleName() {
		return this.middleName;
	}

	public String toString() {
		return "ID: " + id 
		+ "\nName: " + getFullName() 
		+ "\nLogin: " + login 
		+ "\nBlocked: " + (blocked ? "Yes" : "No") 
		+ "\nRole: " + role.toString();
	}

	/**
	 * @return
	 */
	public String getFullName() {
		String result = this.firstName + " " + this.lastName;
		if (this.middleName != "")
			result = result + " " + this.middleName;
		return result;
	}

	public boolean isBlocked() {
		return this.blocked;
	}

	public void setBlockedStatus(boolean isBlocked) {
		this.blocked = isBlocked;
	}

	/**
	 * @param title
	 * @param text
	 * @param recipient
	 * @throws NoSuchElementException
	 */
	public void sendMail(String title, String text, String recipient) throws NoSuchElementException {
		Mail mail = new Mail(title, text, this.id);
		Intranet.INSTANCE.sendMail(mail, recipient, this);
	}


	public abstract void mainMenu(BufferedReader br);

	public static String writePlainText(BufferedReader br) {
		String text = "";
		String lastLine = " :c ";	
		try {
			while (!lastLine.equals("")) {
				lastLine = br.readLine();
				text += lastLine + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text.trim();
	}

	public void writeMailMenu(BufferedReader br) {
		String receiverLogin = "";
		String title = "";
		System.out.println("Type 0 or exit, to return");
		try {
			System.out.println("Please enter the mail's title:");
			title = br.readLine().trim();
			if (title.equalsIgnoreCase("exit") || title.equals("0")) return;
			System.out.println("Please enter the receiver's login");
			receiverLogin = br.readLine().trim();
			if (receiverLogin.equalsIgnoreCase("exit") || receiverLogin.equals("0")) return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Please write a message, just send empty line to finish");
		Mail mail = new Mail(title, writePlainText(br), this.id);
		try {
			Intranet.INSTANCE.sendMail(mail, receiverLogin, this);
		} catch (NoSuchElementException e) {
			System.err.println("Incorrect receiver's login");
		}

	}
	
	public void writeOrderMenu(BufferedReader br, OrderType orderType) {
		String title = "";
		System.out.println("Type 0 or exit, to return");
		try {
			System.out.println("Please enter the mail's title:");
			title = br.readLine().trim();
			if (title.equalsIgnoreCase("exit")|| title.equals("0")) return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Please write a message, just send empty line to finish");
		Order order = new Order(title, writePlainText(br), this.id);
		Intranet.INSTANCE.addLog("ORDER SEND", this, "JUST WROTE " + orderType.toString() + " ORDER");
		if (orderType.equals(OrderType.IT)) Intranet.INSTANCE.addITOrder(order);
		if (orderType.equals(OrderType.OR)) Intranet.INSTANCE.addOROrder(order);
		
	}
	
	private String getSenderName(String senderId) {
		User user = Intranet.INSTANCE.getUserByID(senderId);
		if (user.role.equals(Role.MANAGER)) return "OR";
		if (user.role.equals(Role.TECH_SUPPORT)) return "IT Support";
		if (user.role.equals(Role.ADMIN)) return "Admin";
		return user.getFullName();
	}
	
	public void viewMailBox(BufferedReader br) {
		while (true) {
			String[] header = { "#", "From", "Title", "Date" };
			String[][] data = new String[mailBox.size()][4];
			
			for (int i = 0; i < mailBox.size(); i++) {
				Mail mail = mailBox.get(mailBox.size() - i -1);
				data[i][0] = "" + (i+1);
				if (!mail.isRead) data[i][0]+="*";
				data[i][1] = getSenderName(mail.senderId);
				data[i][2] = mail.title;
				data[i][3] = mail.sendTime.toLocaleString();
			}
			System.out.println(AsciiTable.getTable(header, data));
			try {
				System.out.println("Enter the number of mail to read it");
				System.out.println("type 0 or exit to back");
				String input = br.readLine();
				if (input.equalsIgnoreCase("exit") || input.equals("0")) break;
				int mailId = mailBox.size() - Integer.parseInt(input);
				Mail mail = mailBox.get(mailId);
				mail.read();
				System.out.println(mail.toString());
				String tmp = br.readLine();
				if (tmp.equalsIgnoreCase("exit") || input.equals("0")) break;

			} catch (IOException e) {
				continue;
			} catch (NumberFormatException e1) {
				System.err.println("Please enter the number");
				continue;
			} catch (ArrayIndexOutOfBoundsException e2) {
				System.err.println("Please enter the CORRECT number");
				continue;
			}
		}

	}
	



}
