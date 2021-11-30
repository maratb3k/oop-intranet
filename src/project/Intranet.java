package project;

import java.io.*;
import java.util.*;

public class Intranet implements Serializable {

	static FileInputStream fis;
	static FileOutputStream fos;
	static ObjectOutputStream oos;
	static ObjectInputStream oin;
	private static final long serialVersionUID = 3404459326519621442L;
	public static final Intranet INSTANCE;
	private static final int idLength = 6;
	public static String databasePath = "./database/";
	private static int usersCount = 0;
	private static int coursesCount = 0;
	private HashSet<User> users;
	private Vector<Course> courses;
	private Vector<Discipline> disciplines;
	private Vector<Order> ITOrders;
	private Vector<Order> OROrders;
	public Date lastUpdate;

	static {
		INSTANCE = deserializeSystem();
		usersCount = INSTANCE.users.size();
		coursesCount = INSTANCE.courses.size();
		ScheduledTask dateUpdate = new ScheduledTask();
		dateUpdate.start();
	}

	private static Intranet deserializeSystem() {
		try {
			oin = new ObjectInputStream(new FileInputStream(databasePath + "intranet.dat"));
			return (Intranet) oin.readObject();
		} catch (Exception e) {
			return new Intranet();
		}
	}

	private Intranet() {
		this.users = new HashSet<User>();
		this.courses = new Vector<Course>();
		this.disciplines = new Vector<Discipline>();
		this.ITOrders = new Vector<Order>();
		this.OROrders = new Vector<Order>();
		this.lastUpdate = new Date();
	}

	public void serializeSystem() throws IOException {
		oos = new ObjectOutputStream(new FileOutputStream(databasePath + "intranet.dat"));
		oos.writeObject(this);
		oos.close();
	}

	public Vector<User> getAllUsers() {
		return new Vector<User>(this.users);
	}

	/**
	 * @return
	 */
	public Vector<Order> getITOrders() {
		return this.ITOrders;
	}

	/**
	 * @return
	 */
	public Vector<Order> getOROrders() {
		return this.OROrders;
	}

	/**
	 * @return
	 */
	public Vector<Discipline> getDisciplines() {
		return this.disciplines;
	}

	/**
	 * @return
	 */
	public Vector<Course> getCourses() {
		return this.courses;
	}

	private boolean checkDates(Date lastUpdate, Date incrementDate, Date curDate) {
		return lastUpdate.before(incrementDate) && incrementDate.before(curDate);
	}

	public void dateUpdate() {
		Date curDate = new Date();
		Date[] semesterIncrement = { new Date("1/09/2000"), new Date("1/01/2000"), new Date("1/06/2000") };
		Date newYearOfStudy = new Date("1/09/2000");

		if (checkDates(lastUpdate, newYearOfStudy, curDate)) {
			for (User user : this.users) {
				if (user.getRole().equals(Role.STUDENT)) {
					((Student) user).addYearOfStudy();
				}
			}
		}
		for (Date date : semesterIncrement) {
			if (checkDates(lastUpdate, date, curDate)) {
				for (User user : this.users) {
					if (user.getRole().equals(Role.STUDENT)) {
						((Student) user).addCurrentSemester();
					}
				}
			}
		}
		this.lastUpdate = curDate;
	}

	/**
	 * @return
	 */
	public String generateUserId() {
		String end = "0".repeat(idLength - Integer.toString(usersCount).length()) + Integer.toString(usersCount);
		Intranet.usersCount++;
		return Integer.toString(((new Date()).getYear()) - 100) + "B" + end;
	}

	/**
	 * @return
	 */
	public String generateCourseId() {
		String end = "0".repeat(idLength - Integer.toString(coursesCount).length()) + Integer.toString(coursesCount);
		Intranet.coursesCount++;
		return Integer.toString(((new Date()).getYear() - 100)) + "C" + end;
	}

	/**
	 * @param password
	 * @param login
	 * @return
	 */
	public User logIn(String login, String password) throws NoSuchElementException {
		for (User user : this.users) {
			if (user.signIn(login, password) && !user.isBlocked()) {
				addLog("LOGIN", user, "LOGIN: " + login);
				return user;
			}
		}
		throw new NoSuchElementException("User not found");
	}

	public User getUserByLogin(String login) throws NoSuchElementException {
		for (User user : this.users) {
			if (user.login.equals(login)) {
				return user;
			}
		}
		throw new NoSuchElementException("User not found");
	}

	public User getUserByID(String id) throws NoSuchElementException {
		for (User u : this.users) {
			if (u.id.equals(id))
				return u;
		}

		throw new NoSuchElementException("User not found");
	}

	public Course getCourseByID(String id) throws NoSuchElementException {
		for (Course c : this.courses) {
			if (c.getCourseId().equals(id))
				return c;
		}
		throw new NoSuchElementException("Course not found");
	}

	public void addUser(User user) {
		this.users.add(user);
	}

	public void addLog(String action, User user, String info) {
		try {
			Intranet.INSTANCE.serializeSystem();
			PrintWriter out = new PrintWriter(
					new BufferedWriter(new FileWriter(Intranet.databasePath + "logs.log", true)));
			out.println("[" + (new Date()).toString() + "] [" + user.getRole().toString() + "] [" + action + "] INFO:"
					+ info);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void addITOrder(Order order) {
		this.ITOrders.add(order);
	}

	public void addOROrder(Order order) {
		this.OROrders.add(order);
	}

	public void sendMail(Mail mail, String login, User sender) throws NoSuchElementException {
		User user = getUserByLogin(login);
		user.mailBox.add(mail);
		Intranet.INSTANCE.addLog("SEND MAIL", sender, "JUST WROTE MAIL TO USER WITH ID " + user.getId());
	}

}
